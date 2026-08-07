package com.team.ecommerce.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class SqlExtractionTest {

    private static final Pattern SQL_BLOCK_PATTERN = Pattern.compile("```SQL\\R(.*?)\\R```", Pattern.DOTALL);

    @Test
    void dataSqlEqualsFirstAuthoritativeSqlBlock() throws IOException {
        Path root = Path.of("..").toAbsolutePath().normalize();
        List<String> blocks = extractSqlBlocks(root);

        assertEquals(normalize(blocks.get(0)), normalize(Files.readString(root.resolve("database/data.sql"))));
    }

    @Test
    void schemaSqlEqualsSecondAuthoritativeSqlBlock() throws IOException {
        Path root = Path.of("..").toAbsolutePath().normalize();
        List<String> blocks = extractSqlBlocks(root);

        assertEquals(normalize(blocks.get(1)), normalize(Files.readString(root.resolve("database/schema.sql"))));
    }

    private List<String> extractSqlBlocks(Path root) throws IOException {
        String source = Files.readString(root.resolve("B2C 多商家电商平台：任务分工与接口说明.md"));
        Matcher matcher = SQL_BLOCK_PATTERN.matcher(source);
        List<String> blocks = new ArrayList<>();
        while (matcher.find()) {
            blocks.add(matcher.group(1));
        }
        return blocks;
    }

    private String normalize(String value) {
        return value.replace("\r\n", "\n").replaceAll("\\n*$", "") + "\n";
    }
}
