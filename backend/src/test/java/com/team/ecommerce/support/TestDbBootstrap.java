package com.team.ecommerce.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 集成测试专用：Spring 上下文就绪时，用仓库里的 {@code ../database/schema.sql} + {@code data.sql}
 * 重建专用测试库（profile=test 连接的是 jd_ecommerce_test）。
 *
 * <p>安全关键点：{@link #stripPreamble} 会把两个脚本开头的
 * {@code CREATE DATABASE jd_ecommerce} 与 {@code USE jd_ecommerce;} 全部剥掉，
 * 保证脚本只会作用于当前连接的库，绝不误写开发库。
 *
 * <p>{@code data.sql} 非幂等（重复执行会主键冲突），因此用 {@link AtomicBoolean} 保证
 * 每个 JVM 只执行一次；之后每个测试方法用 {@code @Transactional} 回滚，基线保持种子状态。
 *
 * <p>{@code schema.sql} 的 DROP 顺序只对全新库安全（空库上 DROP IF EXISTS 是空操作）；
 * 测试库已有旧表时，MySQL 会因外键（如 {@code merchant.fk_merchant_user -> user}）拒绝 DROP。
 * 因此重建期间关掉外键检查 {@code SET FOREIGN_KEY_CHECKS=0}，结束后再开启——与库的 DROP 顺序解耦。
 */
@Profile("test")
@Component
public class TestDbBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(TestDbBootstrap.class);
    private static final AtomicBoolean DONE = new AtomicBoolean(false);

    private final DataSource dataSource;

    public TestDbBootstrap(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (DONE.compareAndSet(false, true)) {
            reset();
        }
    }

    private void reset() {
        Path dbDir = databaseDir();
        try {
            String schema = stripPreamble(Files.readString(dbDir.resolve("schema.sql"), StandardCharsets.UTF_8));
            String data = stripPreamble(Files.readString(dbDir.resolve("data.sql"), StandardCharsets.UTF_8));
            try (Connection conn = dataSource.getConnection()) {
                ScriptUtils.executeSqlScript(conn, resourceOf("SET FOREIGN_KEY_CHECKS=0"));
                try {
                    ScriptUtils.executeSqlScript(conn, resourceOf(schema));
                } finally {
                    ScriptUtils.executeSqlScript(conn, resourceOf("SET FOREIGN_KEY_CHECKS=1"));
                }
                ScriptUtils.executeSqlScript(conn, resourceOf(data));
            }
            log.info("集成测试库已重建并灌入种子数据（schema.sql + data.sql）");
        } catch (Exception e) {
            throw new IllegalStateException("集成测试库初始化失败（请确认本机 MySQL 正在运行、root/123456 可连）", e);
        }
    }

    private static EncodedResource resourceOf(String sql) {
        return new EncodedResource(new ByteArrayResource(sql.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 去掉脚本开头的 {@code CREATE DATABASE ...} 与 {@code USE `...`;}（截到首个 {@code USE `} 语句的分号后）。
     */
    static String stripPreamble(String sql) {
        int use = sql.indexOf("USE `");
        if (use >= 0) {
            int semi = sql.indexOf(';', use);
            if (semi >= 0) {
                return sql.substring(semi + 1);
            }
        }
        return sql;
    }

    /** 从工作目录向上逐级找含 schema.sql 的 database 目录（Maven/IDEA 工作目录为 backend/）。 */
    private Path databaseDir() {
        for (Path p = Path.of("").toAbsolutePath().normalize(); p != null; p = p.getParent()) {
            Path candidate = p.resolve("database");
            if (Files.exists(candidate.resolve("schema.sql"))) {
                return candidate;
            }
        }
        throw new IllegalStateException("找不到 database/schema.sql，请在 backend/ 目录下运行集成测试");
    }
}
