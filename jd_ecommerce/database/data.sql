-- ============================================================
-- 京东风格电商平台 - 初始测试数据 (data.sql)
-- 数据库: jd_ecommerce
-- ============================================================

USE `jd_ecommerce`;

-- ============================================================
-- 1. 用户数据
-- ============================================================
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `nickname`, `gender`, `status`, `role`) VALUES
-- 密码均为 123456 的 BCrypt 加密
(1, 'admin',     '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000001', 'admin@jd-demo.com',     '系统管理员', 1, 1, 'ADMIN'),
(2, 'merchant1', '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000002', 'merchant1@jd-demo.com', '数码旗舰店', 1, 1, 'MERCHANT'),
(3, 'merchant2', '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000003', 'merchant2@jd-demo.com', '服饰优选店', 2, 1, 'MERCHANT'),
(4, 'user1',     '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000004', 'user1@jd-demo.com',     '张三',       1, 1, 'USER'),
(5, 'user2',     '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000005', 'user2@jd-demo.com',     '李四',       2, 1, 'USER'),
(6, 'user3',     '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000006', 'user3@jd-demo.com',     '王五',       1, 1, 'USER'),
(7, 'merchant3', '$2a$10$N.zmdr9k7uOCQb37B9tJdO5mBpK.fk1a.jf1s8g/3UvK9pJzQf1a', '13800000007', 'merchant3@jd-demo.com', '待审核商家', 1, 1, 'MERCHANT');

-- ============================================================
-- 2. 商家数据
-- ============================================================
INSERT INTO `merchant` (`id`, `user_id`, `shop_name`, `shop_logo`, `description`, `contact_phone`, `status`, `audit_status`, `audit_remark`) VALUES
(1, 2, '数码旗舰店',     'https://img.jd-demo.com/shop/logo1.png', '主营手机、电脑、数码配件，正品保障', '13800000002', 1, 1, '审核通过'),
(2, 3, '服饰优选店',     'https://img.jd-demo.com/shop/logo2.png', '潮流服饰，品质生活',                 '13800000003', 1, 1, '审核通过'),
(3, 7, '待审核商家',     'https://img.jd-demo.com/shop/logo3.png', '新入驻商家，等待审核',               '13800000007', 0, 0, NULL);

-- ============================================================
-- 3. 商品分类数据
-- ============================================================
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`, `icon`, `status`) VALUES
-- 一级分类
(1,  '手机数码', 0, 1, 'https://img.jd-demo.com/cat/phone.png', 1),
(2,  '家用电器', 0, 2, 'https://img.jd-demo.com/cat/appliance.png', 1),
(3,  '服饰鞋包', 0, 3, 'https://img.jd-demo.com/cat/clothing.png', 1),
(4,  '食品生鲜', 0, 4, 'https://img.jd-demo.com/cat/food.png', 1),
-- 二级分类: 手机数码
(11, '手机通讯', 1, 1, NULL, 1),
(12, '电脑办公', 1, 2, NULL, 1),
(13, '数码配件', 1, 3, NULL, 1),
-- 二级分类: 家用电器
(21, '大家电',   2, 1, NULL, 1),
(22, '厨电',     2, 2, NULL, 1),
-- 二级分类: 服饰鞋包
(31, '男装',     3, 1, NULL, 1),
(32, '女装',     3, 2, NULL, 1),
(33, '鞋靴',     3, 3, NULL, 1),
-- 三级分类
(111, '智能手机', 11, 1, NULL, 1),
(112, '老人机',   11, 2, NULL, 1),
(121, '笔记本',   12, 1, NULL, 1),
(122, '台式机',   12, 2, NULL, 1),
(311, 'T恤',      31, 1, NULL, 1),
(312, '夹克',     31, 2, NULL, 1),
(321, '连衣裙',   32, 1, NULL, 1);

-- ============================================================
-- 4. 商品数据
-- ============================================================
INSERT INTO `product` (`id`, `merchant_id`, `category_id`, `name`, `subtitle`, `main_image`, `sub_images`, `description`, `detail_html`, `price`, `status`, `sales_count`) VALUES
(1, 1, 111, '智选 Pro 5G 手机 12GB+256GB 钛空灰', '旗舰芯片 | 徕卡光学 | 120W快充', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?auto=format&fit=crop&w=900&q=80"]', '搭载最新旗舰处理器，6.7英寸OLED屏幕，5000mAh大电池，支持120W有线快充和50W无线快充。徕卡四摄系统，拍照体验出众。', '<p>产品详情...</p>', 4999.00, 1, 1520),
(2, 1, 111, '畅享 Note 5G 手机 8GB+128GB 幻夜黑', '大屏长续航 | 5000mAh', 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=900&q=80"]', '6.8英寸大屏，5000mAh超大电池，支持33W快充，后置6400万像素三摄。', '<p>产品详情...</p>', 1599.00, 1, 3200),
(3, 1, 121, '轻薄本 Air 14 锐龙版 16GB+512GB', '14英寸2.8K屏 | 锐龙7 7840H', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1522199755839-a2bacb67c546?auto=format&fit=crop&w=900&q=80"]', '14英寸2.8K OLED屏幕，AMD锐龙7 7840H处理器，16GB LPDDR5内存，512GB NVMe SSD，1.2kg轻薄机身。', '<p>产品详情...</p>', 4299.00, 1, 860),
(4, 1, 13,  '65W GaN氮化镓充电器 三口快充', '小巧便携 | 兼容多设备', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80"]', '65W GaN氮化镓快充，支持PD/PPS/QC等多种协议，双USB-C+USB-A三口设计，折叠插脚便携出行。', '<p>产品详情...</p>', 129.00, 1, 5800),
(5, 2, 311, '纯棉短袖T恤 男款 100%新疆棉', '透气舒适 | 多色可选', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1503341504253-dff4815485f1?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1576566588028-4147f3842f27?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?auto=format&fit=crop&w=900&q=80"]', '100%新疆长绒棉，260g重磅面料，精梳工艺，领口加固不易变形。', '<p>产品详情...</p>', 59.00, 1, 8900),
(6, 2, 321, '法式碎花连衣裙 夏季新款', '显瘦版型 | 优雅气质', 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?auto=format&fit=crop&w=900&q=80"]', '法式方领设计，高腰A字版型显瘦，雪纺面料飘逸舒适，适合日常和约会穿着。', '<p>产品详情...</p>', 159.00, 1, 2300),
(7, 1, 21,  '4K激光投影电视 100英寸', '影院级巨幕 | 护眼无屏闪', 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1461151304267-38535e780c79?auto=format&fit=crop&w=900&q=80"]', '4K分辨率激光投影，100英寸超大画面，3500ANSI流明，MEMC运动补偿，哈曼卡顿音响。', '<p>产品详情...</p>', 8999.00, 1, 120),
(8, 2, 312, '春秋夹克外套 男款防风', '休闲百搭 | 轻薄防风', 'https://images.unsplash.com/photo-1520975954732-35dd22299614?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80","https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80"]', '防风面料，可拆卸帽子，多口袋设计实用，春秋过渡季节首选。', '<p>产品详情...</p>', 199.00, 0, 0),
(9, 1, 111, '折叠屏手机 Flip 5G 12GB+512GB', '内外双屏 | 悬停自拍', 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=900&q=80', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80"]', '8.2英寸内屏+3.5英寸外屏，骁龙8 Gen3，悬停自拍，5000万主摄。', '<p>产品详情...</p>', 7999.00, 3, 0);

-- ============================================================
-- 5. 商品SKU数据
-- ============================================================
INSERT INTO `product_sku` (`id`, `product_id`, `sku_name`, `price`, `original_price`, `stock`, `attributes`, `sku_image`, `status`) VALUES
-- 商品1: 智选 Pro 5G
(1, 1, '智选Pro 5G 钛空灰 12GB+256GB', 4999.00, 5499.00, 500, '{"颜色":"钛空灰","版本":"12GB+256GB"}', 'https://img.jd-demo.com/sku/s1.jpg', 1),
(2, 1, '智选Pro 5G 雪山白 12GB+256GB', 4999.00, 5499.00, 300, '{"颜色":"雪山白","版本":"12GB+256GB"}', 'https://img.jd-demo.com/sku/s2.jpg', 1),
(3, 1, '智选Pro 5G 钛空灰 16GB+512GB', 5499.00, 5999.00, 200, '{"颜色":"钛空灰","版本":"16GB+512GB"}', 'https://img.jd-demo.com/sku/s1.jpg', 1),
(4, 1, '智选Pro 5G 雪山白 16GB+512GB', 5499.00, 5999.00, 150, '{"颜色":"雪山白","版本":"16GB+512GB"}', 'https://img.jd-demo.com/sku/s2.jpg', 1),
-- 商品2: 畅享 Note 5G
(5, 2, '畅享Note 5G 幻夜黑 8GB+128GB', 1599.00, 1799.00, 800, '{"颜色":"幻夜黑","版本":"8GB+128GB"}', 'https://img.jd-demo.com/sku/s5.jpg', 1),
(6, 2, '畅享Note 5G 晨曦金 8GB+128GB', 1599.00, 1799.00, 600, '{"颜色":"晨曦金","版本":"8GB+128GB"}', 'https://img.jd-demo.com/sku/s6.jpg', 1),
(7, 2, '畅享Note 5G 幻夜黑 8GB+256GB', 1799.00, 1999.00, 400, '{"颜色":"幻夜黑","version":"8GB+256GB"}', 'https://img.jd-demo.com/sku/s5.jpg', 1),
-- 商品3: 轻薄本 Air 14
(8, 3, 'Air14 锐龙版 16GB+512GB 银色', 4299.00, 4999.00, 200, '{"颜色":"银色","配置":"16GB+512GB"}', 'https://img.jd-demo.com/sku/s8.jpg', 1),
(9, 3, 'Air14 锐龙版 32GB+1TB 银色',   5299.00, 5999.00, 100, '{"颜色":"银色","配置":"32GB+1TB"}',   'https://img.jd-demo.com/sku/s8.jpg', 1),
-- 商品4: 65W充电器
(10, 4, '65W GaN充电器 白色', 129.00, 159.00, 2000, '{"颜色":"白色"}', 'https://img.jd-demo.com/sku/s10.jpg', 1),
-- 商品5: 纯棉T恤
(11, 5, 'T恤 白色 L',   59.00, 89.00, 500, '{"颜色":"白色","尺码":"L"}',   'https://img.jd-demo.com/sku/s11.jpg', 1),
(12, 5, 'T恤 白色 XL',  59.00, 89.00, 500, '{"颜色":"白色","尺码":"XL"}',  'https://img.jd-demo.com/sku/s12.jpg', 1),
(13, 5, 'T恤 黑色 L',   59.00, 89.00, 500, '{"颜色":"黑色","尺码":"L"}',   'https://img.jd-demo.com/sku/s13.jpg', 1),
(14, 5, 'T恤 黑色 XL',  59.00, 89.00, 500, '{"颜色":"黑色","尺码":"XL"}',  'https://img.jd-demo.com/sku/s14.jpg', 1),
(15, 5, 'T恤 灰色 M',   59.00, 89.00, 300, '{"颜色":"灰色","尺码":"M"}',   'https://img.jd-demo.com/sku/s15.jpg', 1),
-- 商品6: 碎花连衣裙
(16, 6, '碎花连衣裙 S',  159.00, 259.00, 200, '{"颜色":"碎花","尺码":"S"}',  'https://img.jd-demo.com/sku/s16.jpg', 1),
(17, 6, '碎花连衣裙 M',  159.00, 259.00, 300, '{"颜色":"碎花","尺码":"M"}',  'https://img.jd-demo.com/sku/s17.jpg', 1),
(18, 6, '碎花连衣裙 L',  159.00, 259.00, 200, '{"颜色":"碎花","尺码":"L"}',  'https://img.jd-demo.com/sku/s18.jpg', 1),
-- 商品7: 激光投影电视
(19, 7, '激光投影电视 100英寸', 8999.00, 10999.00, 50, '{"规格":"100英寸"}', 'https://img.jd-demo.com/sku/s19.jpg', 1),
-- 商品8: 夹克(待审核)
(20, 8, '夹克 黑色 M', 199.00, 299.00, 100, '{"颜色":"黑色","尺码":"M"}', 'https://img.jd-demo.com/sku/s20.jpg', 1),
(21, 8, '夹克 黑色 L', 199.00, 299.00, 100, '{"颜色":"黑色","尺码":"L"}', 'https://img.jd-demo.com/sku/s21.jpg', 1),
-- 商品9: 折叠屏手机(审核拒绝)
(22, 9, '折叠屏 Flip 5G 钛空灰 12GB+512GB', 7999.00, 8999.00, 100, '{"颜色":"钛空灰","版本":"12GB+512GB"}', 'https://img.jd-demo.com/sku/s22.jpg', 1);

-- ============================================================
-- 6. 收货地址数据
-- ============================================================
INSERT INTO `address` (`id`, `user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(1, 4, '张三', '13800000004', '北京市', '北京市', '朝阳区', '建国路88号现代城SOHO 1号楼1801室', 1),
(2, 4, '张三', '13800000004', '北京市', '北京市', '海淀区', '中关村大街1号海龙大厦1502室',     0),
(3, 5, '李四', '13800000005', '上海市', '上海市', '浦东新区', '世纪大道100号环球金融中心56层', 1),
(4, 6, '王五', '13800000006', '广东省', '深圳市', '南山区',   '科技园南区T3栋501室',           1);

-- ============================================================
-- 7. 购物车数据
-- ============================================================
INSERT INTO `cart` (`id`, `user_id`, `product_sku_id`, `quantity`, `selected`) VALUES
(1, 4, 1,  1, 1),
(2, 4, 11, 2, 1),
(3, 4, 10, 1, 0),
(4, 5, 16, 1, 1),
(5, 5, 5,  1, 1);

-- ============================================================
-- 8. 订单数据
-- ============================================================
INSERT INTO `orders` (`id`, `order_no`, `user_id`, `merchant_id`, `total_amount`, `pay_amount`, `status`, `address_snapshot`, `remark`, `logistics_company`, `logistics_no`, `ship_time`, `receive_time`, `pay_time`, `cancel_time`, `cancel_reason`) VALUES
-- 订单1: 已完成全流程(待评价)
(1, 'ORD20260707000001', 4, 1, 4999.00, 4999.00, 3, '{"name":"张三","phone":"13800000004","province":"北京市","city":"北京市","district":"朝阳区","detail":"建国路88号现代城SOHO 1号楼1801室"}', '请尽快发货', '顺丰速运', 'SF1234567890', '2026-07-05 10:00:00', '2026-07-06 14:00:00', '2026-07-04 09:00:00', NULL, NULL),
-- 订单2: 待发货
(2, 'ORD20260707000002', 4, 1, 129.00, 129.00, 1, '{"name":"张三","phone":"13800000004","province":"北京市","city":"北京市","district":"朝阳区","detail":"建国路88号现代城SOHO 1号楼1801室"}', NULL, NULL, NULL, NULL, NULL, '2026-07-06 15:00:00', NULL, NULL),
-- 订单3: 已发货
(3, 'ORD20260707000003', 5, 2, 159.00, 159.00, 2, '{"name":"李四","phone":"13800000005","province":"上海市","city":"上海市","district":"浦东新区","detail":"世纪大道100号环球金融中心56层"}', NULL, '中通快递', 'ZT9876543210', '2026-07-06 16:00:00', NULL, '2026-07-05 11:00:00', NULL, NULL),
-- 订单4: 待支付
(4, 'ORD20260707000004', 6, 1, 1599.00, NULL, 0, '{"name":"王五","phone":"13800000006","province":"广东省","city":"深圳市","district":"南山区","detail":"科技园南区T3栋501室"}', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 订单5: 已取消
(5, 'ORD20260707000005', 4, 2, 59.00, NULL, 5, '{"name":"张三","phone":"13800000004","province":"北京市","city":"北京市","district":"朝阳区","detail":"建国路88号现代城SOHO 1号楼1801室"}', NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-03 12:00:00', '不想买了'),
-- 订单6: 已评价
(6, 'ORD20260707000006', 5, 1, 4299.00, 4299.00, 4, '{"name":"李四","phone":"13800000005","province":"上海市","city":"上海市","district":"浦东新区","detail":"世纪大道100号环球金融中心56层"}', NULL, '京东物流', 'JD5678901234', '2026-07-03 09:00:00', '2026-07-04 15:00:00', '2026-07-02 10:00:00', NULL, NULL);

-- ============================================================
-- 9. 订单明细数据
-- ============================================================
INSERT INTO `order_item` (`id`, `order_id`, `product_sku_id`, `product_name`, `sku_name`, `product_image`, `quantity`, `unit_price`, `subtotal`) VALUES
(1,  1, 1,  '智选 Pro 5G 手机 12GB+256GB 钛空灰', '智选Pro 5G 钛空灰 12GB+256GB', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80', 1, 4999.00, 4999.00),
(2,  2, 10, '65W GaN氮化镓充电器 三口快充',      '65W GaN充电器 白色',           'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80', 1, 129.00,  129.00),
(3,  3, 16, '法式碎花连衣裙 夏季新款',            '碎花连衣裙 M',                 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=900&q=80', 1, 159.00,  159.00),
(4,  4, 5,  '畅享 Note 5G 手机 8GB+128GB 幻夜黑', '畅享Note 5G 幻夜黑 8GB+128GB', 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80', 1, 1599.00, 1599.00),
(5,  5, 11, '纯棉短袖T恤 男款 100%新疆棉',        'T恤 白色 L',                   'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 1, 59.00,   59.00),
(6,  6, 8,  '轻薄本 Air 14 锐龙版 16GB+512GB',   'Air14 锐龙版 16GB+512GB 银色', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80', 1, 4299.00, 4299.00);

-- ============================================================
-- 10. 退款数据
-- ============================================================
INSERT INTO `refund` (`id`, `refund_no`, `order_id`, `user_id`, `merchant_id`, `reason`, `description`, `amount`, `status`, `merchant_audit_time`, `merchant_remark`, `return_logistics_company`, `return_logistics_no`, `return_ship_time`, `merchant_confirm_time`, `appeal_time`, `appeal_reason`, `admin_id`, `admin_handle_time`, `admin_remark`, `completed_time`, `timeout_hours`) VALUES
-- 退款1: 退款完成(正常流程)
(1, 'RFD20260706000001', 1, 4, 1, '商品质量问题', '手机屏幕有坏点，要求退款', 4999.00, 3, '2026-07-06 10:00:00', '同意退款，请寄回商品', '顺丰速运', 'SF1112223334', '2026-07-06 12:00:00', '2026-07-07 09:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-07 09:30:00', 48),
-- 退款2: 待商家审核
(2, 'RFD20260707000002', 2, 4, 1, '不想要了', '买重复了，申请退款', 129.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 48),
-- 退款3: 商家拒绝 → 用户申诉 → 管理员支持退款
(3, 'RFD20260707000003', 3, 5, 2, '尺码不符', '收到的M码实际偏小，与描述不符', 159.00, 6, '2026-07-06 18:00:00', '尺码符合标准，拒绝退款', NULL, NULL, NULL, NULL, '2026-07-07 08:00:00', '商家尺码表不清晰，支持用户退款', 1, '2026-07-07 10:00:00', '商家尺码描述不清晰，支持用户退款', NULL, 48),
-- 退款4: 商家拒绝 → 用户申诉 → 管理员拒绝
(4, 'RFD20260707000004', 6, 5, 1, '性能不达标', '笔记本续航远低于宣传', 4299.00, 7, '2026-07-05 14:00:00', '产品符合宣传参数，拒绝退款', NULL, NULL, NULL, NULL, '2026-07-06 09:00:00', '商品已拆封使用且参数符合描述', 1, '2026-07-06 16:00:00', '经核实商品参数与宣传一致，不支持退款', NULL, 48);

-- ============================================================
-- 11. 评价数据
-- ============================================================
INSERT INTO `review` (`id`, `order_id`, `product_id`, `user_id`, `content`, `rating`, `images`, `is_anonymous`, `merchant_reply`, `merchant_reply_time`) VALUES
(1, 6, 3, 5, '笔记本很轻薄，屏幕素质非常好，2.8K OLED显示效果惊艳。续航日常使用7小时左右，整体满意。', 5, '["https://img.jd-demo.com/review/r1.jpg"]', 0, '感谢您的支持！', '2026-07-05 10:00:00');

-- ============================================================
-- 12. 管理员操作日志数据
-- ============================================================
INSERT INTO `admin_log` (`id`, `admin_id`, `action`, `target_type`, `target_id`, `detail`, `ip_address`) VALUES
(1, 1, 'MERCHANT_AUDIT',    'MERCHANT', 1, '{"action":"approve","remark":"审核通过"}', '192.168.1.100'),
(2, 1, 'MERCHANT_AUDIT',    'MERCHANT', 2, '{"action":"approve","remark":"审核通过"}', '192.168.1.100'),
(3, 1, 'PRODUCT_AUDIT',     'PRODUCT',  8, '{"action":"pending","remark":"待审核"}',   '192.168.1.100'),
(4, 1, 'PRODUCT_AUDIT',     'PRODUCT',  9, '{"action":"reject","remark":"信息不完整"}', '192.168.1.100'),
(5, 1, 'REFUND_ARBITRATE',  'REFUND',   3, '{"action":"approve","remark":"商家尺码描述不清晰，支持用户退款"}', '192.168.1.100'),
(6, 1, 'REFUND_ARBITRATE',  'REFUND',   4, '{"action":"reject","remark":"商品参数与宣传一致，不支持退款"}',     '192.168.1.100');

-- ============================================================
-- 13. 支付记录数据
-- ============================================================
INSERT INTO `payment` (`id`, `payment_no`, `order_id`, `user_id`, `amount`, `pay_method`, `status`, `pay_time`) VALUES
(1, 'PAY20260704000001', 1, 4, 4999.00, 'SIMULATED', 1, '2026-07-04 09:00:00'),
(2, 'PAY20260706000002', 2, 4, 129.00,  'SIMULATED', 1, '2026-07-06 15:00:00'),
(3, 'PAY20260705000003', 3, 5, 159.00,  'SIMULATED', 1, '2026-07-05 11:00:00'),
(4, 'PAY20260702000004', 6, 5, 4299.00, 'SIMULATED', 1, '2026-07-02 10:00:00');

-- ============================================================
-- 14. 轮播图数据
-- ============================================================
INSERT INTO `banner` (`id`, `title`, `image_url`, `link_url`, `sort`, `status`) VALUES
(1, '618大促 全场低至5折',    'https://images.unsplash.com/photo-1607083206869-4c7672e72a8a?auto=format&fit=crop&w=1600&q=80', '/promotion/618',  1, 1),
(2, '手机数码 新品首发',      'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1600&q=80', '/category/111',   2, 1),
(3, '服饰换新 夏日清凉',      'https://images.unsplash.com/photo-1491933382434-500287f9b54b?auto=format&fit=crop&w=1600&q=80', '/category/3',     3, 1),
(4, '家电焕新 以旧换新',      'https://images.unsplash.com/photo-1517430816045-df4b7de11d1d?auto=format&fit=crop&w=1600&q=80', '/category/2',     4, 1);

-- ============================================================
-- 15. 店铺设置数据
-- ============================================================
INSERT INTO `shop_config` (`id`, `merchant_id`, `config_key`, `config_value`) VALUES
(1, 1, 'customer_service_phone', '400-100-1001'),
(2, 1, 'return_policy',          '7天无理由退换货'),
(3, 1, 'free_shipping_threshold', '99'),
(4, 2, 'customer_service_phone', '400-200-2002'),
(5, 2, 'return_policy',          '15天无理由退换货'),
(6, 2, 'free_shipping_threshold', '59');

-- ============================================================
-- 初始数据加载完成
-- 测试账号:
--   admin / 123456      - 管理员
--   merchant1 / 123456  - 商家(数码旗舰店)
--   merchant2 / 123456  - 商家(服饰优选店)
--   user1 / 123456      - 普通用户(张三)
--   user2 / 123456      - 普通用户(李四)
--   user3 / 123456      - 普通用户(王五)
--   merchant3 / 123456  - 待审核商家
-- ============================================================

