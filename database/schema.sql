-- ============================================================
-- 京东风格电商平台 - 数据库建表脚本 (schema.sql)
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `jd_ecommerce`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `jd_ecommerce`;

-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(128) NOT NULL                COMMENT '密码(BCrypt加密)',
    `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    `email`       VARCHAR(128) DEFAULT NULL            COMMENT '邮箱',
    `avatar`      VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
    `nickname`    VARCHAR(64)  DEFAULT NULL            COMMENT '昵称',
    `gender`      TINYINT      DEFAULT 0               COMMENT '性别: 0-未知 1-男 2-女',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 0-禁用 1-正常',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户 MERCHANT-商家 ADMIN-管理员',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 商家表 (merchant)
-- ============================================================
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商家ID',
    `user_id`       BIGINT       NOT NULL                COMMENT '关联用户ID',
    `shop_name`     VARCHAR(128) NOT NULL                COMMENT '店铺名称',
    `shop_logo`     VARCHAR(255) DEFAULT NULL            COMMENT '店铺Logo URL',
    `description`   VARCHAR(512) DEFAULT NULL            COMMENT '店铺描述',
    `contact_phone` VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 0-停业 1-营业中',
    `audit_status`  TINYINT      NOT NULL DEFAULT 0      COMMENT '审核状态: 0-待审核 1-审核通过 2-审核拒绝',
    `audit_remark`  VARCHAR(255) DEFAULT NULL            COMMENT '审核备注',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_audit_status` (`audit_status`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_merchant_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- ============================================================
-- 3. 商品分类表 (category)
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`       VARCHAR(64)  NOT NULL                COMMENT '分类名称',
    `parent_id`  BIGINT       NOT NULL DEFAULT 0      COMMENT '父分类ID, 0-顶级分类',
    `sort`       INT          NOT NULL DEFAULT 0      COMMENT '排序(越小越靠前)',
    `icon`       VARCHAR(255) DEFAULT NULL            COMMENT '分类图标URL',
    `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 0-隐藏 1-显示',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 4. 商品表 (product)
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id`   BIGINT        NOT NULL                COMMENT '商家ID',
    `category_id`   BIGINT        NOT NULL                COMMENT '分类ID',
    `name`          VARCHAR(200)  NOT NULL                COMMENT '商品名称',
    `subtitle`      VARCHAR(255)  DEFAULT NULL            COMMENT '副标题',
    `main_image`    VARCHAR(255)  DEFAULT NULL            COMMENT '主图URL',
    `sub_images`    TEXT          DEFAULT NULL            COMMENT '子图URL列表(JSON数组)',
    `description`   TEXT          DEFAULT NULL            COMMENT '商品详情描述',
    `detail_html`   TEXT          DEFAULT NULL            COMMENT '商品详情富文本(HTML)',
    `price`         DECIMAL(10,2) NOT NULL                COMMENT '最低价格(展示用)',
    `status`        TINYINT       NOT NULL DEFAULT 0      COMMENT '状态: 0-待审核 1-上架(在售) 2-下架 3-审核拒绝',
    `audit_remark`  VARCHAR(255)  DEFAULT NULL            COMMENT '审核备注',
    `sales_count`   INT           NOT NULL DEFAULT 0      COMMENT '销量',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_name` (`name`),
    KEY `idx_sales_count` (`sales_count`),
    CONSTRAINT `fk_product_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 5. 商品SKU表 (product_sku)
-- ============================================================
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    `product_id`  BIGINT        NOT NULL                COMMENT '商品ID',
    `sku_name`    VARCHAR(200)  NOT NULL                COMMENT 'SKU名称',
    `price`       DECIMAL(10,2) NOT NULL                COMMENT '销售价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL         COMMENT '原价(划线价)',
    `stock`       INT           NOT NULL DEFAULT 0      COMMENT '库存数量',
    `attributes`  VARCHAR(512)  DEFAULT NULL            COMMENT '规格属性(JSON, 如 {"颜色":"红色","尺寸":"XL"})',
    `sku_image`   VARCHAR(255)  DEFAULT NULL            COMMENT 'SKU图片URL',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态: 0-禁用 1-启用',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- ============================================================
-- 6. 收货地址表 (address)
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`    BIGINT       NOT NULL                COMMENT '用户ID',
    `name`       VARCHAR(64)  NOT NULL                COMMENT '收货人姓名',
    `phone`      VARCHAR(20)  NOT NULL                COMMENT '收货人手机号',
    `province`   VARCHAR(64)  NOT NULL                COMMENT '省',
    `city`       VARCHAR(64)  NOT NULL                COMMENT '市',
    `district`   VARCHAR(64)  DEFAULT NULL            COMMENT '区/县',
    `detail`     VARCHAR(255) NOT NULL                COMMENT '详细地址',
    `is_default` TINYINT      NOT NULL DEFAULT 0      COMMENT '是否默认: 0-否 1-是',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================================
-- 7. 购物车表 (cart)
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id`         BIGINT   NOT NULL                COMMENT '用户ID',
    `product_sku_id`  BIGINT   NOT NULL                COMMENT 'SKU ID',
    `quantity`        INT      NOT NULL DEFAULT 1      COMMENT '数量',
    `selected`        TINYINT  NOT NULL DEFAULT 1      COMMENT '是否选中: 0-未选中 1-已选中',
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `product_sku_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_selected` (`selected`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_sku` FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 8. 订单表 (orders)
-- ============================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`        VARCHAR(32)   NOT NULL                COMMENT '订单编号(唯一)',
    `user_id`         BIGINT        NOT NULL                COMMENT '用户ID',
    `merchant_id`     BIGINT        NOT NULL                COMMENT '商家ID',
    `total_amount`    DECIMAL(12,2) NOT NULL                COMMENT '订单总金额',
    `pay_amount`      DECIMAL(12,2) DEFAULT NULL            COMMENT '实付金额',
    `status`          TINYINT       NOT NULL DEFAULT 0      COMMENT '订单状态: 0-待支付 1-待发货 2-已发货 3-已收货 4-已评价 5-已取消',
    `address_snapshot` VARCHAR(512) DEFAULT NULL            COMMENT '下单时收货地址快照(JSON)',
    `remark`          VARCHAR(255)  DEFAULT NULL            COMMENT '订单备注',
    `logistics_company` VARCHAR(64) DEFAULT NULL            COMMENT '物流公司',
    `logistics_no`    VARCHAR(64)   DEFAULT NULL            COMMENT '物流单号',
    `ship_time`       DATETIME      DEFAULT NULL            COMMENT '发货时间',
    `receive_time`    DATETIME      DEFAULT NULL            COMMENT '收货时间',
    `pay_time`        DATETIME      DEFAULT NULL            COMMENT '支付时间',
    `cancel_time`     DATETIME      DEFAULT NULL            COMMENT '取消时间',
    `cancel_reason`   VARCHAR(255)  DEFAULT NULL            COMMENT '取消原因',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_order_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 9. 订单明细表 (order_item)
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`        BIGINT        NOT NULL                COMMENT '订单ID',
    `product_sku_id`  BIGINT        NOT NULL                COMMENT 'SKU ID',
    `product_name`    VARCHAR(200)  NOT NULL                COMMENT '商品名称(快照)',
    `sku_name`        VARCHAR(200)  DEFAULT NULL            COMMENT 'SKU名称(快照)',
    `product_image`   VARCHAR(255)  DEFAULT NULL            COMMENT '商品图片(快照)',
    `quantity`        INT           NOT NULL                COMMENT '购买数量',
    `unit_price`      DECIMAL(10,2) NOT NULL                COMMENT '下单时单价',
    `subtotal`        DECIMAL(12,2) NOT NULL                COMMENT '小计金额',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_sku_id` (`product_sku_id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_item_sku` FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

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
    `id`                   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `refund_no`            VARCHAR(32)   NOT NULL                COMMENT '退款编号(唯一)',
    `order_id`             BIGINT        NOT NULL                COMMENT '订单ID',
    `user_id`              BIGINT        NOT NULL                COMMENT '用户ID',
    `merchant_id`          BIGINT        NOT NULL                COMMENT '商家ID',
    `reason`               VARCHAR(255)  NOT NULL                COMMENT '退款原因',
    `description`          TEXT          DEFAULT NULL            COMMENT '退款描述',
    `amount`               DECIMAL(12,2) NOT NULL                COMMENT '退款金额',
    `status`               TINYINT       NOT NULL DEFAULT 0      COMMENT '退款状态: 0-待审核 1-商家通过 2-寄回中 3-退款完成 4-商家拒绝 5-用户申诉 6-管理员支持退款 7-管理员拒绝退款',
    `merchant_audit_time`  DATETIME      DEFAULT NULL            COMMENT '商家审核时间',
    `merchant_remark`      VARCHAR(255)  DEFAULT NULL            COMMENT '商家审核备注',
    `return_logistics_company` VARCHAR(64) DEFAULT NULL          COMMENT '寄回物流公司',
    `return_logistics_no`  VARCHAR(64)   DEFAULT NULL            COMMENT '寄回物流单号',
    `return_ship_time`     DATETIME      DEFAULT NULL            COMMENT '用户寄回时间',
    `merchant_confirm_time` DATETIME     DEFAULT NULL            COMMENT '商家确认收货时间',
    `appeal_time`          DATETIME      DEFAULT NULL            COMMENT '用户申诉时间',
    `appeal_reason`        VARCHAR(255)  DEFAULT NULL            COMMENT '申诉原因',
    `admin_id`             BIGINT        DEFAULT NULL            COMMENT '处理仲裁的管理员ID',
    `admin_handle_time`    DATETIME      DEFAULT NULL            COMMENT '管理员处理时间',
    `admin_remark`         VARCHAR(255)  DEFAULT NULL            COMMENT '管理员仲裁备注',
    `completed_time`       DATETIME      DEFAULT NULL            COMMENT '退款完成时间',
    `timeout_hours`        INT           NOT NULL DEFAULT 48     COMMENT '商家审核超时时间(小时)',
    `created_at`           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_admin_id` (`admin_id`),
    CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_refund_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

-- ============================================================
-- 11. 评价表 (review)
-- ============================================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id`    BIGINT       NOT NULL                COMMENT '订单ID',
    `product_id`  BIGINT       NOT NULL                COMMENT '商品ID',
    `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
    `content`     TEXT         DEFAULT NULL            COMMENT '评价内容',
    `rating`      TINYINT      NOT NULL DEFAULT 5      COMMENT '评分: 1-5星',
    `images`      TEXT         DEFAULT NULL            COMMENT '评价图片(JSON数组)',
    `is_anonymous` TINYINT     NOT NULL DEFAULT 0      COMMENT '是否匿名: 0-否 1-是',
    `merchant_reply` TEXT      DEFAULT NULL            COMMENT '商家回复内容',
    `merchant_reply_time` DATETIME DEFAULT NULL        COMMENT '商家回复时间',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_product_user` (`order_id`, `product_id`, `user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_rating` (`rating`),
    CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ============================================================
-- 12. 管理员操作日志表 (admin_log)
-- ============================================================
DROP TABLE IF EXISTS `admin_log`;
CREATE TABLE `admin_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `admin_id`    BIGINT       NOT NULL                COMMENT '管理员ID',
    `action`      VARCHAR(64)  NOT NULL                COMMENT '操作类型: MERCHANT_AUDIT/PRODUCT_AUDIT/REFUND_ARBITRATE/USER_DISABLE等',
    `target_type` VARCHAR(32)  DEFAULT NULL            COMMENT '操作对象类型: USER/MERCHANT/PRODUCT/ORDER/REFUND',
    `target_id`   BIGINT       DEFAULT NULL            COMMENT '操作对象ID',
    `detail`      TEXT         DEFAULT NULL            COMMENT '操作详情(JSON)',
    `ip_address`  VARCHAR(64)  DEFAULT NULL            COMMENT '操作IP',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_action` (`action`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- ============================================================
-- 13. 支付记录表 (payment) - 模拟支付
-- ============================================================
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `payment_no`     VARCHAR(32)   NOT NULL                COMMENT '支付流水号(唯一)',
    `order_id`       BIGINT        NOT NULL                COMMENT '订单ID',
    `user_id`        BIGINT        NOT NULL                COMMENT '用户ID',
    `amount`         DECIMAL(12,2) NOT NULL                COMMENT '支付金额',
    `pay_method`     VARCHAR(32)   NOT NULL DEFAULT 'SIMULATED' COMMENT '支付方式: SIMULATED-模拟支付',
    `status`         TINYINT       NOT NULL DEFAULT 0      COMMENT '状态: 0-待支付 1-支付成功 2-支付失败 3-已退款',
    `pay_time`       DATETIME      DEFAULT NULL            COMMENT '支付时间',
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表(模拟支付)';

-- ============================================================
-- 14. 轮播图/广告位表 (banner)
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    `title`       VARCHAR(128) NOT NULL                COMMENT '标题',
    `image_url`   VARCHAR(255) NOT NULL                COMMENT '图片URL',
    `link_url`    VARCHAR(255) DEFAULT NULL            COMMENT '跳转链接',
    `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 0-隐藏 1-显示',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ============================================================
-- 15. 店铺设置表 (shop_config) - 商家店铺配置
-- ============================================================
DROP TABLE IF EXISTS `shop_config`;
CREATE TABLE `shop_config` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `merchant_id`   BIGINT       NOT NULL                COMMENT '商家ID',
    `config_key`    VARCHAR(64)  NOT NULL                COMMENT '配置键',
    `config_value`  VARCHAR(512) DEFAULT NULL            COMMENT '配置值',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_key` (`merchant_id`, `config_key`),
    KEY `idx_merchant_id` (`merchant_id`),
    CONSTRAINT `fk_config_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺设置表';

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

