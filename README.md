# B2C--Ecommerce

课程作业：平台统一监管、多个商家入驻、消费者购买的 B2C 商城 Demo。

## B 角色消费者移动端

`frontend-user-mobile/` 提供“悦选”消费者 Android App 与微信小程序的 uni-app / Vue 3 代码框架，覆盖商品浏览、购物车、下单、订单、评价、退款、地址与账户页面，并提供可直接运行的 Mock 数据。

```bash
cd frontend-user-mobile
npm install
npm run dev:h5
```

完整启动、双端构建与后端联调说明见 `frontend-user-mobile/README.md`。

## E 角色后端（本分支）

本分支实现 E 角色负责的交易、售后和平台运营后端，包含：

- **交易模块** `trade/`：购物车、下单（按商家拆单）、模拟支付、取消订单（恢复库存）、确认收货
- **售后模块** `aftersales/`：评价（消费者评价 + 商家回复）、退款（申请 / 商家审核 / 退货物流 / 确认收货 / 申诉 / 管理员仲裁）
- **商家交易** `merchant/`：商家看板、订单查询、发货
- **平台运营** `admin/`：管理员统计、操作日志、轮播图管理

接口路径、字段与《B2C 多商家电商平台：任务分工与接口说明》保持一致。

## 运行环境

- Java 25、Spring Boot 3.5.16、Maven、MyBatis
- 默认使用内存 H2（自动执行 `schema-h2.sql` + `data-h2.sql`，含演示数据，开箱即用）
- `dev` 配置连接 MySQL，读取环境变量 `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`

## 启动与测试

```bash
# 默认启动（H2，含演示数据，端口 8080）
mvn -f backend/pom.xml spring-boot:run

# 使用 MySQL
DB_URL="jdbc:mysql://localhost:3306/jd_ecommerce?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
DB_USERNAME=root DB_PASSWORD=your_password \
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev

# 测试（53 个测试：路由注册、状态枚举、6 个服务集成测试、E2E 冒烟）
mvn -f backend/pom.xml test

# 独立 E2E 冒烟脚本（启动应用后运行）
python scripts/smoke_test.py
```

## 身份接入点（临时）

D 角色的 JWT 尚未接入，Controller 通过请求头识别身份：

- `X-User-Id`：消费者 ID
- `X-Merchant-Id`：商家 ID
- `X-Admin-Id`：管理员 ID

接入 JWT 后由安全上下文提供这些 ID。

## 演示账号（H2 / data.sql）

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 管理员 |
| merchant1 / merchant2 | 123456 | 商家 |
| user1 / user2 / user3 | 123456 | 消费者 |

## 数据库

- `database/schema.sql`、`database/data.sql`：权威 MySQL 脚本（原样来自开发文档附录）
- `backend/src/main/resources/schema-h2.sql`、`data-h2.sql`：由脚本 `scripts/gen-h2-sql.py` 生成的 H2 兼容版本（去掉 MySQL 专属语法、去重全局索引名）

## 设计说明

- `docs/e-role-backend-skeleton.md`：骨架交接说明
- `docs/superpowers/`：设计与实施记录
- `scripts/`：H2 SQL 生成脚本、E2E 冒烟测试脚本
