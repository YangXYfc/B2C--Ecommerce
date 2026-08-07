# E Role Trade Platform Skeleton Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a compilable and startable Spring Boot skeleton for every E-role API in the project document, with exact database scripts and no completed business logic.

**Architecture:** Use a modular monolith under `com.team.ecommerce`, split into common, trade, aftersales, merchant, and admin packages. Controllers fix the HTTP contract, services fix extension points and deliberately throw a consistent not-implemented exception, mapper interfaces reserve persistence contracts, and Java records model DTOs and database rows without introducing SQL mappings.

**Tech Stack:** Java 25, Spring Boot 3.5.16, Spring Web MVC, Jakarta Validation, MyBatis Spring Boot Starter 3.0.5, MySQL Connector/J, H2 (default skeleton runtime), JUnit 5, MockMvc, Maven.

## Global Constraints

- Preserve the two SQL code blocks from `B2C 多商家电商平台：任务分工与接口说明.md` byte-for-byte apart from the Markdown code fences and a normalized final newline.
- Implement only E-role routes from sections 5.3 through 5.6; do not implement D-role APIs.
- Do not implement JWT, inventory transactions, order/refund transition rules, mapper SQL, or real database reads and writes.
- Use `X-User-Id`, `X-Merchant-Id`, and `X-Admin-Id` request headers only as explicit skeleton identity seams.
- Every unfinished service operation throws `FeatureNotImplementedException`; no endpoint returns fabricated success data.
- Default profile starts against H2 with SQL initialization disabled; `dev` profile points to MySQL through environment variables.

---

## File Map

### Build and configuration

- `backend/pom.xml`: Java, Spring Boot, MyBatis, database, validation, and test dependencies.
- `backend/src/main/java/com/team/ecommerce/EcommerceApplication.java`: application entry point and mapper scan.
- `backend/src/main/resources/application.yml`: H2 default profile, disabled SQL initialization, MyBatis naming settings.
- `backend/src/main/resources/application-dev.yml`: MySQL 8 environment-variable template.

### Common contracts

- `common/api/ApiResponse.java`, `PageQuery.java`, `PageResult.java`: shared response and pagination records.
- `common/error/ErrorCode.java`, `BusinessException.java`, `FeatureNotImplementedException.java`, `GlobalExceptionHandler.java`: stable skeleton error behavior.

### Trade

- `trade/cart`: `CartEntity`, request/view records, `CartMapper`, `CartService`, `CartServiceSkeleton`, `CartController`.
- `trade/order`: `OrderStatus`, `OrderEntity`, `OrderItemEntity`, request/view records, `OrderMapper`, `OrderItemMapper`, `OrderService`, `OrderServiceSkeleton`, `OrderController`.
- `trade/payment`: `PaymentEntity`, `PaymentMapper`.
- `trade/inventory`: `InventoryItem`, `InventoryGateway`.

### Aftersales

- `aftersales/review`: `ReviewEntity`, review request/view records, `ReviewMapper`, `ReviewService`, `ReviewServiceSkeleton`, public and merchant controllers.
- `aftersales/refund`: `RefundStatus`, `RefundEntity`, request/view records, `RefundMapper`, `RefundService`, `RefundServiceSkeleton`, consumer, merchant, and admin controllers.

### Merchant and admin

- `merchant`: dashboard/order request and view records, `MerchantTradeService`, `MerchantTradeServiceSkeleton`, `MerchantTradeController`.
- `admin`: `BannerEntity`, `AdminLogEntity`, dashboard/banner/log view and request records, mappers, `PlatformAdminService`, `PlatformAdminServiceSkeleton`, public banner and admin controllers.

### Database, tests, and handoff

- `database/data.sql`, `database/schema.sql`: exact extractions.
- `backend/src/test/...`: context, route registry, status mapping, and SQL extraction tests.
- `docs/e-role-backend-skeleton.md`: module boundaries and implementation handoff.

---

### Task 1: Extract the authoritative database scripts

**Files:**
- Create: `backend/pom.xml`
- Create: `database/data.sql`
- Create: `database/schema.sql`
- Create: `backend/src/test/java/com/team/ecommerce/database/SqlExtractionTest.java`

**Interfaces:**
- Consumes: the first and second fenced `SQL` blocks below `# 附录sql：` in the root Markdown document.
- Produces: standalone scripts whose content equals the corresponding fenced-block content.

- [ ] **Step 1: Create the Maven descriptor**

Use Spring Boot parent `3.5.16`, `<java.version>25</java.version>`, and MyBatis starter `3.0.5`. Add `spring-boot-starter-web`, `spring-boot-starter-validation`, MyBatis starter, runtime MySQL connector, runtime H2, and `spring-boot-starter-test`. Configure compiler parameter names and the Spring Boot Maven plugin.

- [ ] **Step 2: Write the failing extraction test**

Create `SqlExtractionTest` with two tests. Each test reads `../B2C 多商家电商平台：任务分工与接口说明.md`, extracts fenced blocks using `Pattern.compile("```SQL\\R(.*?)\\R```", Pattern.DOTALL)`, and compares block 0 with `../database/data.sql` and block 1 with `../database/schema.sql` after normalizing CRLF to LF and ensuring one final newline.

```java
assertEquals(normalize(blocks.get(0)), normalize(Files.readString(root.resolve("database/data.sql"))));
assertEquals(normalize(blocks.get(1)), normalize(Files.readString(root.resolve("database/schema.sql"))));
```

- [ ] **Step 3: Run the test to verify RED**

Run `mvn -f backend/pom.xml -Dtest=SqlExtractionTest test`. Expected failure: both destination SQL files are missing.

- [ ] **Step 4: Extract both blocks without editing their content**

Copy the first fenced block into `database/data.sql` and the second into `database/schema.sql`. Preserve statement order, comments, identifiers, inserts, indexes, foreign keys, and status descriptions.

- [ ] **Step 5: Run the extraction test to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=SqlExtractionTest test`. Expected: 2 tests pass.

- [ ] **Step 6: Commit**

```powershell
git add backend/pom.xml database backend/src/test/java/com/team/ecommerce/database/SqlExtractionTest.java
git commit -m "chore: extract authoritative database scripts"
```

### Task 2: Bootstrap the application and common error contract

**Files:**
- Modify: `backend/pom.xml`
- Create: `backend/src/main/java/com/team/ecommerce/EcommerceApplication.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/api/ApiResponse.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/api/PageQuery.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/api/PageResult.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/error/ErrorCode.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/error/BusinessException.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/error/FeatureNotImplementedException.java`
- Create: `backend/src/main/java/com/team/ecommerce/common/error/GlobalExceptionHandler.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/application-dev.yml`
- Create: `backend/src/test/java/com/team/ecommerce/EcommerceApplicationTest.java`
- Create: `backend/src/test/java/com/team/ecommerce/common/error/GlobalExceptionHandlerTest.java`

**Interfaces:**
- Produces: `ApiResponse<T>(String code, String message, T data)`, `PageQuery(int page, int size)`, `PageResult<T>(List<T> records, long total, int page, int size)`, and HTTP 501 for unfinished functionality.

- [ ] **Step 1: Write failing context and exception tests**

`EcommerceApplicationTest` uses `@SpringBootTest` and an empty `contextLoads()` method. `GlobalExceptionHandlerTest` exposes a test endpoint that throws `new FeatureNotImplementedException("cart.list")` and asserts status 501 plus JSON fields `code == "NOT_IMPLEMENTED"` and `message` containing `cart.list`.

- [ ] **Step 2: Run tests to verify RED**

Run `mvn -f backend/pom.xml -Dtest=EcommerceApplicationTest,GlobalExceptionHandlerTest test`. Expected: compilation fails because application/common types do not exist.

- [ ] **Step 3: Implement the minimum common contract**

`ErrorCode` contains `INVALID_ARGUMENT`, `NOT_IMPLEMENTED`, and `INTERNAL_ERROR`. `BusinessException` stores an `ErrorCode`. `FeatureNotImplementedException` formats `功能待实现: <operation>`. `GlobalExceptionHandler` maps validation and argument errors to 400, feature errors to 501, other business errors to 409, and unexpected errors to 500.

`application.yml` configures `jdbc:h2:mem:jd_ecommerce;MODE=MySQL;DB_CLOSE_DELAY=-1`, disables `spring.sql.init`, and disables the H2 console. `application-dev.yml` reads `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, with URL default `jdbc:mysql://localhost:3306/jd_ecommerce?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`.

- [ ] **Step 4: Run tests to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=EcommerceApplicationTest,GlobalExceptionHandlerTest test`. Expected: both test classes pass.

- [ ] **Step 5: Commit**

```powershell
git add backend/pom.xml backend/src
git commit -m "build: bootstrap E role Spring Boot backend"
```

### Task 3: Define state, persistence, and D/E collaboration contracts

**Files:**
- Create: `backend/src/main/java/com/team/ecommerce/trade/order/OrderStatus.java`
- Create: `backend/src/main/java/com/team/ecommerce/aftersales/refund/RefundStatus.java`
- Create: entity records for `CartEntity`, `OrderEntity`, `OrderItemEntity`, `PaymentEntity`, `ReviewEntity`, `RefundEntity`, `BannerEntity`, and `AdminLogEntity`.
- Create: mapper interfaces `CartMapper`, `OrderMapper`, `OrderItemMapper`, `PaymentMapper`, `ReviewMapper`, `RefundMapper`, `BannerMapper`, and `AdminLogMapper`.
- Create: `backend/src/main/java/com/team/ecommerce/trade/inventory/InventoryItem.java`
- Create: `backend/src/main/java/com/team/ecommerce/trade/inventory/InventoryGateway.java`
- Create: `backend/src/test/java/com/team/ecommerce/trade/order/OrderStatusTest.java`
- Create: `backend/src/test/java/com/team/ecommerce/aftersales/refund/RefundStatusTest.java`

**Interfaces:**
- Produces: `OrderStatus.fromCode(int)`, `RefundStatus.fromCode(int)`, and `InventoryGateway.check/deduct/restore(List<InventoryItem>)`.

- [ ] **Step 1: Write failing status mapping tests**

Assert every documented integer-to-enum mapping and assert `fromCode(-1)` throws `IllegalArgumentException` for both enums.

- [ ] **Step 2: Run status tests to verify RED**

Run `mvn -f backend/pom.xml -Dtest=OrderStatusTest,RefundStatusTest test`. Expected: compilation fails because enums do not exist.

- [ ] **Step 3: Implement state enums and collaboration interface**

Implement exact codes:

```java
PENDING_PAYMENT(0), PENDING_SHIPMENT(1), SHIPPED(2), RECEIVED(3), REVIEWED(4), CANCELLED(5)
```

```java
PENDING(0), MERCHANT_APPROVED(1), RETURNING(2), COMPLETED(3), MERCHANT_REJECTED(4), APPEALED(5), ADMIN_APPROVED(6), ADMIN_REJECTED(7)
```

Define `InventoryItem(Long skuId, Integer quantity)` and gateway methods `void check(List<InventoryItem> items)`, `void deduct(List<InventoryItem> items)`, and `void restore(List<InventoryItem> items)` without an implementation bean.

- [ ] **Step 4: Add entity records and mapper signatures**

Entity record component names mirror snake-case SQL columns in camelCase, including all columns for the eight E-owned tables. Each mapper is annotated `@Mapper` and declares `selectById`, module-specific paged selection methods, `insert`, `updateById`, and `deleteById` only where deletion is documented. No method carries `@Select`, `@Insert`, `@Update`, or `@Delete`.

- [ ] **Step 5: Run tests and compile to verify GREEN**

Run `mvn -f backend/pom.xml test`. Expected: current tests pass and mapper proxies load without SQL execution.

- [ ] **Step 6: Commit**

```powershell
git add backend/src
git commit -m "feat: define E role domain contracts"
```

### Task 4: Build cart, order, and payment interface skeletons

**Files:**
- Create DTO records under `trade/cart/dto`: `AddCartItemRequest(Long skuId, Integer quantity)`, `UpdateCartItemRequest(Integer quantity, Boolean selected)`, `CartItemView`, `CartView`.
- Create DTO records under `trade/order/dto`: `CreateOrderRequest(Long addressId, List<Long> cartItemIds, String remark)`, `OrderQuery(Integer status, int page, int size)`, `CancelOrderRequest(String reason)`, `OrderItemView`, `OrderSummaryView`, `OrderDetailView`, `PaymentView`.
- Create: `CartService`, `CartServiceSkeleton`, `CartController`.
- Create: `OrderService`, `OrderServiceSkeleton`, `OrderController`.
- Create: `backend/src/test/java/com/team/ecommerce/trade/TradeRouteRegistryTest.java`.

**Interfaces:**
- `CartService`: `getCart(userId)`, `addItem(userId, request)`, `updateItem(userId, itemId, request)`, `deleteItem(userId, itemId)`, `deleteSelected(userId)`.
- `OrderService`: `create(userId, request)`, `list(userId, query)`, `detail(userId, orderId)`, `cancel(userId, orderId, request)`, `pay(userId, orderId)`, `confirmReceipt(userId, orderId)`.

- [ ] **Step 1: Write the failing route registry test**

Read `RequestMappingHandlerMapping` and assert exact method/path pairs for all 11 section 5.3 routes. The test checks registration, not business results.

- [ ] **Step 2: Run route test to verify RED**

Run `mvn -f backend/pom.xml -Dtest=TradeRouteRegistryTest test`. Expected: route assertions fail because no trade controllers exist.

- [ ] **Step 3: Add DTOs, service contracts, and skeleton services**

Apply Jakarta validation to required IDs, positive quantities, and page sizes from 1 through 100. Annotate skeleton implementations with `@Service`. Use these exact unfinished-operation names: `cart.get`, `cart.addItem`, `cart.updateItem`, `cart.deleteItem`, `cart.deleteSelected`, `order.create`, `order.list`, `order.detail`, `order.cancel`, `order.pay`, and `order.confirmReceipt`.

- [ ] **Step 4: Add controllers**

Use `/api/cart` and `/api/orders` base mappings. Consumer operations require `X-User-Id`. Return `ApiResponse` types and delegate once to the corresponding service. Match the document's GET/POST/PUT/DELETE methods exactly.

- [ ] **Step 5: Run route test and full tests to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=TradeRouteRegistryTest test`, then `mvn -f backend/pom.xml test`. Expected: all tests pass.

- [ ] **Step 6: Commit**

```powershell
git add backend/src
git commit -m "feat: scaffold cart and order APIs"
```

### Task 5: Build review and refund interface skeletons

**Files:**
- Create review DTOs: `CreateReviewRequest`, `ReviewQuery`, `ReplyReviewRequest`, `ReviewView`.
- Create refund DTOs: `CreateRefundRequest`, `RefundQuery`, `ReturnLogisticsRequest`, `AppealRefundRequest`, `AuditRefundRequest`, `ArbitrateRefundRequest`, `RefundView`.
- Create: `ReviewService`, `ReviewServiceSkeleton`, `ReviewController`, `MerchantReviewController`.
- Create: `RefundService`, `RefundServiceSkeleton`, `RefundController`, `MerchantRefundController`, `AdminRefundController`.
- Create: `backend/src/test/java/com/team/ecommerce/aftersales/AftersalesRouteRegistryTest.java`.

**Interfaces:**
- `ReviewService`: create, listByProduct, listForMerchant, and reply methods with explicit user/merchant IDs.
- `RefundService`: create, listForUser, detail, submitReturnLogistics, appeal, listForMerchant, merchantAudit, confirmReturn, listForAdmin, and arbitrate methods.

- [ ] **Step 1: Write the failing route registry test**

Assert all 14 documented review/refund routes and their HTTP methods, including shared refund detail and public product review paths.

- [ ] **Step 2: Run the test to verify RED**

Run `mvn -f backend/pom.xml -Dtest=AftersalesRouteRegistryTest test`. Expected: route assertions fail.

- [ ] **Step 3: Add DTOs and service skeletons**

Validation rules: rating 1–5; refund amount positive; logistics company/number nonblank; audit/arbitration decisions non-null; reasons and replies nonblank. Skeleton operations throw the standard unfinished exception.

- [ ] **Step 4: Add consumer, merchant, and admin controllers**

Use `X-User-Id`, `X-Merchant-Id`, or `X-Admin-Id` based on the actor documented for each endpoint. Shared refund detail accepts whichever actor header will later be resolved by D security; expose optional headers without implementing authorization.

- [ ] **Step 5: Run route and full tests to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=AftersalesRouteRegistryTest test`, then `mvn -f backend/pom.xml test`. Expected: all tests pass.

- [ ] **Step 6: Commit**

```powershell
git add backend/src
git commit -m "feat: scaffold review and refund APIs"
```

### Task 6: Build merchant trade interface skeletons

**Files:**
- Create: `merchant/dto/MerchantDashboardView.java`, `ShipOrderRequest.java`, `MerchantOrderQuery.java`, `MerchantOrderView.java`.
- Create: `merchant/service/MerchantTradeService.java`, `MerchantTradeServiceSkeleton.java`.
- Create: `merchant/controller/MerchantTradeController.java`.
- Create: `backend/src/test/java/com/team/ecommerce/merchant/MerchantRouteRegistryTest.java`.

**Interfaces:**
- `MerchantTradeService`: `dashboard(merchantId)`, `listOrders(merchantId, query)`, `orderDetail(merchantId, orderId)`, and `ship(merchantId, orderId, request)`.

- [ ] **Step 1: Write failing merchant route tests**

Assert GET dashboard, GET order list, GET order detail, and PUT ship routes. Do not assert D-owned `/api/merchant/shop` or product routes.

- [ ] **Step 2: Run test to verify RED**

Run `mvn -f backend/pom.xml -Dtest=MerchantRouteRegistryTest test`. Expected: four missing route failures.

- [ ] **Step 3: Add DTOs, service skeleton, and controller**

`ShipOrderRequest` requires nonblank logistics company and tracking number. All routes require `X-Merchant-Id`; all service methods throw the standard unfinished exception.

- [ ] **Step 4: Run route and full tests to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=MerchantRouteRegistryTest test`, then `mvn -f backend/pom.xml test`. Expected: all tests pass.

- [ ] **Step 5: Commit**

```powershell
git add backend/src
git commit -m "feat: scaffold merchant trade APIs"
```

### Task 7: Build platform operations interface skeletons

**Files:**
- Create admin DTOs: `AdminDashboardView`, `AdminLogQuery`, `AdminLogView`, `BannerRequest`, `BannerView`.
- Create: `PlatformAdminService`, `PlatformAdminServiceSkeleton`, `PublicBannerController`, `PlatformAdminController`.
- Create: `backend/src/test/java/com/team/ecommerce/admin/AdminRouteRegistryTest.java`.

**Interfaces:**
- `PlatformAdminService`: dashboard, listLogs, listEnabledBanners, listAllBanners, createBanner, updateBanner, and deleteBanner.

- [ ] **Step 1: Write failing platform route tests**

Assert the seven section 5.6 E-owned routes. Explicitly assert no controller in this branch registers D-owned admin user, merchant, or product routes.

- [ ] **Step 2: Run test to verify RED**

Run `mvn -f backend/pom.xml -Dtest=AdminRouteRegistryTest test`. Expected: route assertions fail.

- [ ] **Step 3: Add DTOs, service skeleton, and controllers**

Public `GET /api/banners` has no identity header. `/api/admin/**` operations require `X-Admin-Id`. `BannerRequest` requires title and image URL, with sort and enabled status fields. All operations throw the standard unfinished exception.

- [ ] **Step 4: Run route and full tests to verify GREEN**

Run `mvn -f backend/pom.xml -Dtest=AdminRouteRegistryTest test`, then `mvn -f backend/pom.xml test`. Expected: all tests pass.

- [ ] **Step 5: Commit**

```powershell
git add backend/src
git commit -m "feat: scaffold platform operations APIs"
```

### Task 8: Document the handoff and verify the complete skeleton

**Files:**
- Create: `docs/e-role-backend-skeleton.md`
- Modify: `README.md`
- Modify tests only if an observed integration defect requires a regression test first.

**Interfaces:**
- Produces: a concise map telling later implementers where to add authentication, inventory integration, transition rules, and SQL mappings.

- [ ] **Step 1: Write handoff documentation**

Document startup commands, default H2 versus `dev` MySQL configuration, the three temporary identity headers, package ownership, why services return 501, mapper SQL extension points, and the exact non-goals. Add backend/database links to the root README without replacing the existing project description.

- [ ] **Step 2: Verify SQL extraction**

Run `mvn -f backend/pom.xml -Dtest=SqlExtractionTest test`. Expected: 2 tests pass.

- [ ] **Step 3: Verify every route and state mapping**

Run `mvn -f backend/pom.xml -Dtest=*RouteRegistryTest,*StatusTest test`. Expected: all route and enum tests pass with zero failures.

- [ ] **Step 4: Verify the full build**

Run `mvn -f backend/pom.xml clean test` and `mvn -f backend/pom.xml package -DskipTests`. Expected: both commands exit 0.

- [ ] **Step 5: Smoke-test application startup**

Start `java -jar backend/target/b2c-ecommerce-backend-0.1.0-SNAPSHOT.jar --server.port=0`, wait for `Started EcommerceApplication`, and terminate the process. Expected: no datasource or mapper startup error.

- [ ] **Step 6: Audit scope and repository state**

Use `git diff main...HEAD --name-only` and confirm no D-role controller package, JWT implementation, mapper SQL/XML, or business state-transition code exists. Run `git diff --check` and confirm no whitespace errors.

- [ ] **Step 7: Commit documentation**

```powershell
git add README.md docs/e-role-backend-skeleton.md
git commit -m "docs: explain E role backend skeleton handoff"
```

## Plan Self-Review

- Spec coverage: all 36 E-owned routes, eight E-owned database tables, both state enumerations, inventory collaboration boundary, exact SQL extraction, startup configuration, and handoff documentation are assigned to tasks.
- Scope: JWT, D-role APIs, inventory mutations, state machines, and mapper SQL remain explicit non-goals.
- Type consistency: consumer, merchant, and admin identity parameters use stable `Long` IDs; pagination uses `PageQuery`/`PageResult`; all unfinished operations use the same exception contract.
- Verification: each production batch begins with a failing behavioral or structural test and ends with focused plus full-suite verification.
