# E 角色交易平台后端骨架设计

## 1. 目标

在 `feature/trade-platform` 分支中，依据根目录《B2C 多商家电商平台：任务分工与接口说明》搭建 E 角色负责的完整 Java 后端接口骨架，并把文档附录中的 `schema.sql` 与 `data.sql` 原样提取为独立数据库脚本。

本次交付让后续开发者能够直接在既定目录、类和方法中补充业务逻辑，而不需要重新设计模块边界或接口路径。

## 2. 范围

### 2.1 包含

- Spring Boot 3.5.x、Java 25、MyBatis、MySQL 8 项目基础结构。
- E 角色全部 API 的 Controller 路由、请求 DTO、响应 DTO、Service 接口、Service 骨架实现、Mapper 接口和数据库实体。
- 订单与退款状态枚举，枚举值严格对应文档 SQL 中的数字状态。
- D/E 库存协作接口，仅声明检查、扣减、恢复方法，不实现库存事务。
- 统一响应、分页参数、分页响应、业务异常及“功能待实现”处理。
- 默认可启动的骨架配置，以及 MySQL 开发环境配置模板。
- 应用启动测试、路由存在性测试、状态枚举映射测试和 SQL 文件存在性测试。
- E 角色骨架说明和后续实现入口说明。

### 2.2 不包含

- JWT 生成、解析、鉴权和角色权限规则；该部分属于 D 角色。
- 用户、地址、商家入驻、店铺资料、分类、商品、SKU 和库存维护接口；该部分属于 D 角色。
- 订单拆分、价格复核、库存扣减或恢复的真实事务。
- 订单状态转换合法性判断。
- 退款资格校验、超时判断和退款状态转换合法性判断。
- MyBatis XML、注解 SQL 或真实数据库读写逻辑。
- 真实支付、真实物流、优惠券、秒杀、聊天、推荐算法和微服务。

## 3. 总体架构

后端采用模块化单体结构，根包为 `com.team.ecommerce`。每个业务模块采用 `controller / service / mapper / entity / dto` 分层：

1. Controller 固定接口路径、HTTP 方法和请求响应类型。
2. Service 接口固定后续业务实现需要遵守的方法契约。
3. Service 骨架实现统一抛出“功能待实现”业务异常，不伪造业务结果。
4. Mapper 接口声明与表结构相符的基础方法签名，但不提供 SQL。
5. Entity 与 `schema.sql` 字段对应，DTO 面向文档中的页面和操作语义。

默认配置使用内存数据库，仅用于验证应用上下文和接口骨架能够启动；`dev` 配置预留 MySQL 8 连接参数。MySQL 脚本不会由默认启动过程自动执行，避免修改原始 SQL 或引入兼容性差异。

## 4. 目录与模块

```text
backend/
├─ pom.xml
└─ src/
   ├─ main/
   │  ├─ java/com/team/ecommerce/
   │  │  ├─ EcommerceApplication.java
   │  │  ├─ common/
   │  │  ├─ trade/
   │  │  │  ├─ cart/
   │  │  │  ├─ order/
   │  │  │  ├─ payment/
   │  │  │  └─ inventory/
   │  │  ├─ aftersales/
   │  │  │  ├─ review/
   │  │  │  └─ refund/
   │  │  ├─ merchant/
   │  │  └─ admin/
   │  └─ resources/
   │     ├─ application.yml
   │     └─ application-dev.yml
   └─ test/java/com/team/ecommerce/
database/
├─ schema.sql
└─ data.sql
docs/
└─ e-role-backend-skeleton.md
```

`merchant` 只包含 E 负责的商家看板、订单、发货、退款和评价接口；不建立 D 负责的店铺资料和商品管理接口。`admin` 只包含 E 负责的统计、日志、轮播图和退款仲裁接口；不建立 D 负责的用户、商家及商品审核接口。

## 5. 接口覆盖

### 5.1 购物车、订单和模拟支付

- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{id}`
- `DELETE /api/cart/items/{id}`
- `DELETE /api/cart/selected`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `PUT /api/orders/{id}/cancel`
- `POST /api/orders/{id}/pay`
- `PUT /api/orders/{id}/confirm-receipt`

### 5.2 商家交易和评价

- `GET /api/merchant/dashboard`
- `GET /api/merchant/orders`
- `GET /api/merchant/orders/{id}`
- `PUT /api/merchant/orders/{id}/ship`
- `POST /api/reviews`
- `GET /api/products/{id}/reviews`
- `GET /api/merchant/reviews`
- `PUT /api/merchant/reviews/{id}/reply`

### 5.3 退款、退货、申诉和仲裁

- `POST /api/refunds`
- `GET /api/refunds`
- `GET /api/refunds/{id}`
- `PUT /api/refunds/{id}/return-logistics`
- `PUT /api/refunds/{id}/appeal`
- `GET /api/merchant/refunds`
- `PUT /api/merchant/refunds/{id}/audit`
- `PUT /api/merchant/refunds/{id}/confirm-return`
- `GET /api/admin/refunds`
- `PUT /api/admin/refunds/{id}/arbitrate`

### 5.4 平台运营和日志

- `GET /api/admin/dashboard`
- `GET /api/admin/logs`
- `GET /api/banners`
- `GET /api/admin/banners`
- `POST /api/admin/banners`
- `PUT /api/admin/banners/{id}`
- `DELETE /api/admin/banners/{id}`

## 6. 状态与协作边界

### 6.1 订单状态

`OrderStatus` 固定映射：`PENDING_PAYMENT(0)`、`PENDING_SHIPMENT(1)`、`SHIPPED(2)`、`RECEIVED(3)`、`REVIEWED(4)`、`CANCELLED(5)`。本次只提供枚举和值转换，不实现状态跳转规则。

### 6.2 退款状态

`RefundStatus` 固定映射：`PENDING(0)`、`MERCHANT_APPROVED(1)`、`RETURNING(2)`、`COMPLETED(3)`、`MERCHANT_REJECTED(4)`、`APPEALED(5)`、`ADMIN_APPROVED(6)`、`ADMIN_REJECTED(7)`。本次只提供枚举和值转换，不实现状态机校验。

### 6.3 库存协作

E 模块声明 `InventoryGateway`，包含库存检查、扣减和恢复方法。方法以 SKU ID 和数量列表为输入，具体实现由 D/E 后续联调决定。本次不创建 D 模块的库存 Mapper，也不直接修改 `product_sku`。

### 6.4 身份协作

Controller 不实现 JWT。需要用户、商家或管理员身份的方法在 Service 契约中显式接收对应 ID，后续由 D 角色的安全模块从登录上下文提供。骨架阶段通过请求头参数占位，使接口签名和身份依赖可见。

## 7. 数据库脚本

Markdown 附录中标记为 `data.sql` 和 `schema.sql` 的两个 SQL 代码块分别原样写入 `database/data.sql` 和 `database/schema.sql`。不调整建表顺序、字段、索引、外键、注释或演示数据。

Entity 只覆盖 E 业务会直接使用的表：`cart`、`orders`、`order_item`、`payment`、`refund`、`review`、`admin_log` 和 `banner`。D 所有表通过外键 ID 引用，不复制 D 的领域逻辑。

## 8. 错误处理与响应

所有接口使用统一响应结构，至少包含业务码、消息和数据。参数格式错误由统一异常处理器转换为明确的客户端错误；尚未实现的 Service 方法返回固定的“功能待实现”业务错误，避免空指针、伪成功或不可预测响应。

分页接口统一接受页码和每页数量，并返回数据列表、总数、页码和每页数量。骨架只固定类型，不查询真实数据。

## 9. 验证策略

- Maven 编译验证项目结构、依赖和所有类型签名。
- Spring Boot 上下文测试验证默认配置可以启动。
- MockMvc 路由测试验证文档中的 E 接口路径与 HTTP 方法均已注册。
- 枚举测试验证订单和退款状态数字映射与 SQL 注释一致。
- SQL 提取测试验证两个文件存在，并保留文档中的数据库标识、核心建表语句和演示数据标识。

测试不声称业务逻辑已经完成，只证明骨架完整、路径准确、状态定义一致且项目可继续开发。

## 10. 完成标准

- 文档列出的 E 角色 API 全部拥有可发现的 Controller 路由。
- 每个 E 模块拥有清晰的 Service、Mapper、Entity 和 DTO 骨架。
- D/E 边界通过 `InventoryGateway` 和身份 ID 参数明确表达。
- 两份 SQL 从 Markdown 原样提取到 `database` 目录。
- 项目能够完成 Maven 编译并通过骨架测试。
- 未实现 JWT、库存事务、状态机规则或真实 SQL 映射。
