# E 角色后端骨架交接说明

## 当前交付

本分支依据《B2C 多商家电商平台：任务分工与接口说明》建立 E 角色负责的交易、售后和平台运营后端骨架。Controller 路径、DTO、Service 契约、Mapper 接口、数据库实体和状态枚举已经建立；Service 骨架统一返回 HTTP 501，表示具体业务尚待实现。

## 运行环境

- Java 25
- Spring Boot 3.5.16
- Maven 3.6.3 或更高版本
- MyBatis Spring Boot Starter 3.0.5
- MySQL 8（开发环境）
- H2（默认骨架启动环境）

在仓库根目录运行测试：

```bash
mvn -f backend/pom.xml test
```

默认启动（使用内存 H2，不执行建表脚本）：

```bash
mvn -f backend/pom.xml spring-boot:run
```

使用 MySQL 开发配置：

```bash
DB_URL="jdbc:mysql://localhost:3306/jd_ecommerce?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
DB_USERNAME=root DB_PASSWORD=your_password \
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev
```

Windows PowerShell 可先设置同名环境变量，再执行最后一条 Maven 命令。数据库初始化脚本位于 `database/schema.sql` 和 `database/data.sql`，两者均原样来自开发文档附录。

## 模块边界

- `common`：统一响应、分页和异常处理。
- `trade/cart`：购物车接口骨架。
- `trade/order`：下单、订单、取消、支付、收货接口骨架。
- `trade/payment`：模拟支付实体和 Mapper 契约。
- `trade/inventory`：与 D 角色协作的库存检查、扣减和恢复接口。
- `aftersales/review`：消费者评价和商家回复接口骨架。
- `aftersales/refund`：退款、退货、申诉、商家处理和管理员仲裁接口骨架。
- `merchant`：商家看板、订单查询和发货接口骨架。
- `admin`：平台统计、操作日志和轮播图接口骨架。

## 临时身份接入点

骨架尚未接入 D 角色负责的 JWT。Controller 使用以下请求头明确标出后续安全模块需要提供的身份：

- `X-User-Id`：消费者 ID。
- `X-Merchant-Id`：商家 ID。
- `X-Admin-Id`：管理员 ID。

接入 JWT 后，应由安全上下文提供这些 ID，并移除请求方直接指定身份的方式。

## 后续实现入口

1. 在各模块的 `*ServiceSkeleton` 中补充业务规则，或新增正式实现并替换骨架 Bean。
2. 在 Mapper 接口上增加 MyBatis XML 或注解 SQL；当前接口没有真实 SQL 映射。
3. 由 D/E 联调实现 `InventoryGateway`，并把订单创建、取消与库存操作纳入事务。
4. 根据 `OrderStatus` 和 `RefundStatus` 实现状态转换合法性校验。
5. 接入 D 角色的 JWT、用户、商家、商品和 SKU 能力，不在 E 模块复制这些领域逻辑。

## 明确未完成的内容

本骨架没有实现 JWT、真实数据库读写、库存事务、订单拆分、价格复核、订单状态机、退款状态机、真实支付或真实物流。调用业务接口收到 `NOT_IMPLEMENTED` 是当前阶段的预期行为，不代表路由缺失。
