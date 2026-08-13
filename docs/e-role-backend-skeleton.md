# E 角色后端实现说明

> 本文档由"骨架交接说明"更新而来。骨架阶段已全部完成，业务逻辑已实现并通过测试。

## 当前交付

本分支实现 E 角色负责的交易、售后和平台运营后端。Controller 路径、DTO、Service 契约、Mapper 接口、数据库实体和状态枚举在骨架阶段建立；本次提交补充了全部业务逻辑与真实 SQL 映射。

- **交易**：购物车（增删改查、勾选）、下单（按商家拆单、价格快照、扣库存）、模拟支付、取消订单（恢复库存）、确认收货、订单列表/详情
- **售后**：评价（购买/收货校验、商家回复）、退款状态机（申请 → 商家审核 → 寄回 → 确认收货完成；拒绝 → 申诉 → 管理员仲裁）
- **商家**：看板（订单数、销售额、待发货数）、订单查询、发货
- **平台运营**：管理员统计、操作日志、轮播图管理（含公开轮播图接口）

## 运行环境

- Java 25、Spring Boot 3.5.16、Maven 3.6.3+、MyBatis Spring Boot Starter 3.0.5
- MySQL 8（开发环境）、H2（默认骨架启动环境）

```bash
# 默认启动（H2 内存库，自动执行 schema-h2.sql + data-h2.sql，含演示数据）
mvn -f backend/pom.xml spring-boot:run

# MySQL 开发配置
DB_URL="jdbc:mysql://localhost:3306/jd_ecommerce?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
DB_USERNAME=root DB_PASSWORD=your_password \
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev

# 测试（53 个：路由注册、状态枚举、6 个服务集成测试）
mvn -f backend/pom.xml test
```

## 模块边界

- `common`：统一响应、分页、异常处理。
- `trade/cart`：购物车。
- `trade/order`：下单、订单、取消、支付、收货。
- `trade/payment`：模拟支付。
- `trade/inventory`：库存检查、扣减和恢复（`InventoryGatewayImpl` 直连 `product_sku` 表，`SELECT ... FOR UPDATE` 保证原子性）。
- `aftersales/review`：消费者评价和商家回复。
- `aftersales/refund`：退款、退货、申诉、商家处理和管理员仲裁（含状态机）。
- `merchant`：商家看板、订单查询和发货。
- `admin`：平台统计、操作日志和轮播图。

## 实现要点

### 身份接入点（临时）

D 角色的 JWT 尚未接入。Controller 使用以下请求头标出身份，接入 JWT 后应由安全上下文提供：

- `X-User-Id`：消费者 ID
- `X-Merchant-Id`：商家 ID
- `X-Admin-Id`：管理员 ID

### 数据访问

- Mapper 接口统一位于各模块 `mapper/` 包，SQL 写在 `resources/mapper/*.xml`，通过 `mybatis.mapper-locations` 加载。
- 实体为不可变 Java record，因此插入后通过 `SELECT LAST_INSERT_ID()`（Mapper 的 `lastInsertId()`）获取自增主键，不再使用 `useGeneratedKeys`（record 无 setter）。
- H2 演示脚本由 `scripts/gen-h2-sql.py` 从 MySQL 脚本自动生成，去掉 MySQL 专属语法并把全局索引名去重。

### 订单状态机（orders.status）

0-待支付 → 1-待发货（支付）→ 2-已发货（商家发货）→ 3-已收货（确认收货）→ 4-已评价；0 → 5-已取消（恢复库存）。

### 退款状态机（refund.status）

0-待审核 → 1-商家通过 → 2-寄回中（用户填物流）→ 3-退款完成（商家确认收货）
0 → 4-商家拒绝 → 5-用户申诉 → 6-管理员支持退款 / 7-管理员拒绝退款。

## 测试

- `EcommerceApplicationTest`：上下文加载
- `*RouteRegistryTest`：36 个 E 角色路由注册断言（并断言 D 角色路由未被注册）
- `OrderStatusTest` / `RefundStatusTest`：状态枚举映射
- `*ServiceIntegrationTest`（cart / order / review / refund / merchant / admin）：基于 H2 演示数据的业务规则验证
- `SqlExtractionTest`：校验 `schema.sql` / `data.sql` 与文档附录一致

## 与 D 角色的协作点

- 库存通过 `InventoryGateway` 直连 `product_sku` 表，待 D 角色联调时替换为正式网关。
- 下单读取收货地址、商品/SKU 信息使用只读 Mapper（`AddressReadMapper`、`ProductReadMapper`），不复制 D 模块领域逻辑。
- 商家店铺、商品、账号、审核接口归属 D 角色，本分支未实现。
