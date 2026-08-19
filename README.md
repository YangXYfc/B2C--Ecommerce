# B2C--Ecommerce

课程作业：平台统一监管、多个商家入驻、消费者购买的 B2C 商城 Demo。

## B 角色消费者移动端

`frontend-user-mobile/` 提供“悦选”消费者 Android App 与微信小程序的 uni-app / Vue 3 代码框架，覆盖商品浏览、购物车、下单、订单、评价、退款、地址与账户页面。开发环境默认连接真实后端，Mock 仅作为显式备用模式。

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
- 默认激活 `dev` 配置并连接 MySQL `jd_ecommerce`
- 数据库账号默认 `root`，密码读取 `DB_PASSWORD`（未设置时为课程演示值 `123456`）

## 启动与测试

```bash
# 1. 首次创建数据库（在 MySQL 客户端依次执行）
database/schema.sql
database/data.sql

# 2. 启动后端（Windows）
cd backend
set DB_PASSWORD=your_password
mvnw.cmd spring-boot:run

# 3. 分别启动所需前端
cd frontend-user-web && npm install && npm run dev
cd frontend-user-mobile && npm install && npm run dev:h5
cd frontend-merchant && npm install && npm run dev
cd frontend-admin && npm install && npm run dev

# 4. 后端测试
cd backend && mvnw.cmd test
```

## 身份接入

四个前端均发送 `Authorization: Bearer <token>`。交易模块为兼容现有控制器，同时发送对应身份头：

- `X-User-Id`：消费者 ID
- `X-Merchant-Id`：商家 ID
- `X-Admin-Id`：管理员 ID

JWT 拦截器负责登录与角色校验；上述身份头用于现有交易接口的数据范围参数。

## 演示账号（H2 / data.sql）

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 管理员 |
| merchant1 / merchant2 | 123456 | 商家 |
| user1 / user2 / user3 | 123456 | 消费者 |

## 数据库

- `database/schema.sql`、`database/data.sql`：权威 MySQL 脚本（原样来自开发文档附录）
- `backend/src/main/resources/schema-h2.sql`、`data-h2.sql`：保留的 H2 兼容脚本，当前默认运行配置不启用

## 设计说明

- `docs/e-role-backend-skeleton.md`：骨架交接说明
- `docs/superpowers/`：设计与实施记录
- `scripts/`：H2 SQL 生成脚本、E2E 冒烟测试脚本
