-- ============================================================
-- 京东风格电商平台 - 数据库建表脚本 (schema.sql)
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- ============================================================

-- 创建数据库



-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(64)  NOT NULL,
    `password`    VARCHAR(128) NOT NULL,
    `phone`       VARCHAR(20)  DEFAULT NULL,
    `email`       VARCHAR(128) DEFAULT NULL,
    `avatar`      VARCHAR(255) DEFAULT NULL,
    `nickname`    VARCHAR(64)  DEFAULT NULL,
    `gender`      TINYINT      DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
);

-- ============================================================
-- 2. 商家表 (merchant)
-- ============================================================
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       NOT NULL,
    `shop_name`     VARCHAR(128) NOT NULL,
    `shop_logo`     VARCHAR(255) DEFAULT NULL,
    `description`   VARCHAR(512) DEFAULT NULL,
    `contact_phone` VARCHAR(20)  DEFAULT NULL,
    `status`        TINYINT      NOT NULL DEFAULT 1,
    `audit_status`  TINYINT      NOT NULL DEFAULT 0,
    `audit_remark`  VARCHAR(255) DEFAULT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_merchant_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 3. 商品分类表 (category)
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(64)  NOT NULL,
    `parent_id`  BIGINT       NOT NULL DEFAULT 0,
    `sort`       INT          NOT NULL DEFAULT 0,
    `icon`       VARCHAR(255) DEFAULT NULL,
    `status`     TINYINT      NOT NULL DEFAULT 1,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- ============================================================
-- 4. 商品表 (product)
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `merchant_id`   BIGINT        NOT NULL,
    `category_id`   BIGINT        NOT NULL,
    `name`          VARCHAR(200)  NOT NULL,
    `subtitle`      VARCHAR(255)  DEFAULT NULL,
    `main_image`    VARCHAR(255)  DEFAULT NULL,
    `sub_images`    TEXT          DEFAULT NULL,
    `description`   TEXT          DEFAULT NULL,
    `detail_html`   TEXT          DEFAULT NULL,
    `price`         DECIMAL(10,2) NOT NULL,
    `status`        TINYINT       NOT NULL DEFAULT 0,
    `audit_remark`  VARCHAR(255)  DEFAULT NULL,
    `sales_count`   INT           NOT NULL DEFAULT 0,
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_product_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 5. 商品SKU表 (product_sku)
-- ============================================================
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `product_id`  BIGINT        NOT NULL,
    `sku_name`    VARCHAR(200)  NOT NULL,
    `price`       DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2) DEFAULT NULL,
    `stock`       INT           NOT NULL DEFAULT 0,
    `attributes`  VARCHAR(512)  DEFAULT NULL,
    `sku_image`   VARCHAR(255)  DEFAULT NULL,
    `status`      TINYINT       NOT NULL DEFAULT 1,
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 6. 收货地址表 (address)
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT       NOT NULL,
    `name`       VARCHAR(64)  NOT NULL,
    `phone`      VARCHAR(20)  NOT NULL,
    `province`   VARCHAR(64)  NOT NULL,
    `city`       VARCHAR(64)  NOT NULL,
    `district`   VARCHAR(64)  DEFAULT NULL,
    `detail`     VARCHAR(255) NOT NULL,
    `is_default` TINYINT      NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 7. 购物车表 (cart)
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`              BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`         BIGINT   NOT NULL,
    `product_sku_id`  BIGINT   NOT NULL,
    `quantity`        INT      NOT NULL DEFAULT 1,
    `selected`        TINYINT  NOT NULL DEFAULT 1,
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `product_sku_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_sku` FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 8. 订单表 (orders)
-- ============================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `order_no`        VARCHAR(32)   NOT NULL,
    `user_id`         BIGINT        NOT NULL,
    `merchant_id`     BIGINT        NOT NULL,
    `total_amount`    DECIMAL(12,2) NOT NULL,
    `pay_amount`      DECIMAL(12,2) DEFAULT NULL,
    `status`          TINYINT       NOT NULL DEFAULT 0,
    `address_snapshot` VARCHAR(512) DEFAULT NULL,
    `remark`          VARCHAR(255)  DEFAULT NULL,
    `logistics_company` VARCHAR(64) DEFAULT NULL,
    `logistics_no`    VARCHAR(64)   DEFAULT NULL,
    `ship_time`       DATETIME      DEFAULT NULL,
    `receive_time`    DATETIME      DEFAULT NULL,
    `pay_time`        DATETIME      DEFAULT NULL,
    `cancel_time`     DATETIME      DEFAULT NULL,
    `cancel_reason`   VARCHAR(255)  DEFAULT NULL,
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_order_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 9. 订单明细表 (order_item)
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `order_id`        BIGINT        NOT NULL,
    `product_sku_id`  BIGINT        NOT NULL,
    `product_name`    VARCHAR(200)  NOT NULL,
    `sku_name`        VARCHAR(200)  DEFAULT NULL,
    `product_image`   VARCHAR(255)  DEFAULT NULL,
    `quantity`        INT           NOT NULL,
    `unit_price`      DECIMAL(10,2) NOT NULL,
    `subtotal`        DECIMAL(12,2) NOT NULL,
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_item_sku` FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 10. 退款表 (refund)
-- 退款状态机:
--   0-PENDING(待商家审核) → 1-MERCHANT_APPROVED(商家通过) → 2-RETURNING(用户寄回中) → 3-COMPLETED(退款完成)
--                          → 4-MERCHANT_REJECTED(商家拒绝) → 5-APPEALED(用户申诉) → 6-ADMIN_APPROVED(管理员支持退款) → 3
--                                                                → 7-ADMIN_REJECTED(管理员拒绝退款)
--   超时: 商家N小时未审核 → 用户可申诉 → 5-APPEALED
-- ============================================================
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund` (
    `id`                   BIGINT        NOT NULL AUTO_INCREMENT,
    `refund_no`            VARCHAR(32)   NOT NULL,
    `order_id`             BIGINT        NOT NULL,
    `user_id`              BIGINT        NOT NULL,
    `merchant_id`          BIGINT        NOT NULL,
    `reason`               VARCHAR(255)  NOT NULL,
    `description`          TEXT          DEFAULT NULL,
    `amount`               DECIMAL(12,2) NOT NULL,
    `status`               TINYINT       NOT NULL DEFAULT 0,
    `merchant_audit_time`  DATETIME      DEFAULT NULL,
    `merchant_remark`      VARCHAR(255)  DEFAULT NULL,
    `return_logistics_company` VARCHAR(64) DEFAULT NULL,
    `return_logistics_no`  VARCHAR(64)   DEFAULT NULL,
    `return_ship_time`     DATETIME      DEFAULT NULL,
    `merchant_confirm_time` DATETIME     DEFAULT NULL,
    `appeal_time`          DATETIME      DEFAULT NULL,
    `appeal_reason`        VARCHAR(255)  DEFAULT NULL,
    `admin_id`             BIGINT        DEFAULT NULL,
    `admin_handle_time`    DATETIME      DEFAULT NULL,
    `admin_remark`         VARCHAR(255)  DEFAULT NULL,
    `completed_time`       DATETIME      DEFAULT NULL,
    `timeout_hours`        INT           NOT NULL DEFAULT 48,
    `created_at`           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 11. 评价表 (review)
-- ============================================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`    BIGINT       NOT NULL,
    `product_id`  BIGINT       NOT NULL,
    `user_id`     BIGINT       NOT NULL,
    `content`     TEXT         DEFAULT NULL,
    `rating`      TINYINT      NOT NULL DEFAULT 5,
    `images`      TEXT         DEFAULT NULL,
    `is_anonymous` TINYINT     NOT NULL DEFAULT 0,
    `merchant_reply` TEXT      DEFAULT NULL,
    `merchant_reply_time` DATETIME DEFAULT NULL,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_product_user` (`order_id`, `product_id`, `user_id`),
    CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 12. 管理员操作日志表 (admin_log)
-- ============================================================
DROP TABLE IF EXISTS `admin_log`;
CREATE TABLE `admin_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `admin_id`    BIGINT       NOT NULL,
    `action`      VARCHAR(64)  NOT NULL,
    `target_type` VARCHAR(32)  DEFAULT NULL,
    `target_id`   BIGINT       DEFAULT NULL,
    `detail`      TEXT         DEFAULT NULL,
    `ip_address`  VARCHAR(64)  DEFAULT NULL,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 13. 支付记录表 (payment) - 模拟支付
-- ============================================================
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT,
    `payment_no`     VARCHAR(32)   NOT NULL,
    `order_id`       BIGINT        NOT NULL,
    `user_id`        BIGINT        NOT NULL,
    `amount`         DECIMAL(12,2) NOT NULL,
    `pay_method`     VARCHAR(32)   NOT NULL DEFAULT 'SIMULATED',
    `status`         TINYINT       NOT NULL DEFAULT 0,
    `pay_time`       DATETIME      DEFAULT NULL,
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT
);

-- ============================================================
-- 14. 轮播图/广告位表 (banner)
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(128) NOT NULL,
    `image_url`   VARCHAR(255) NOT NULL,
    `link_url`    VARCHAR(255) DEFAULT NULL,
    `sort`        INT          NOT NULL DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- ============================================================
-- 15. 店铺设置表 (shop_config) - 商家店铺配置
-- ============================================================
DROP TABLE IF EXISTS `shop_config`;
CREATE TABLE `shop_config` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `merchant_id`   BIGINT       NOT NULL,
    `config_key`    VARCHAR(64)  NOT NULL,
    `config_value`  VARCHAR(512) DEFAULT NULL,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_key` (`merchant_id`, `config_key`),
    CONSTRAINT `fk_config_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE
);

-- ============================================================
-- 建表完成
-- 表清单:
--   1.  user          - 用户表
--   2.  merchant      - 商家表
--   3.  category      - 商品分类表
--   4.  product       - 商品表
--   5.  product_sku   - 商品SKU表
--   6.  address       - 收货地址表
--   7.  cart          - 购物车表
--   8.  orders        - 订单表
--   9.  order_item    - 订单明细表
--   10. refund        - 退款表
--   11. review        - 评价表
--   12. admin_log     - 管理员操作日志表
--   13. payment       - 支付记录表(模拟支付)
--   14. banner        - 轮播图表
--   15. shop_config   - 店铺设置表
-- ============================================================

