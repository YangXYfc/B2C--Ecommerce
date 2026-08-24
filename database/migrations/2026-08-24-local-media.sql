-- Migrate the development/demo database to versioned local media paths.
-- Safe to execute repeatedly: every statement sets a stable final value.

USE jd_ecommerce;

UPDATE merchant SET shop_logo = '/media/shops/shop-01-digital.jpg' WHERE id = 1;
UPDATE merchant SET shop_logo = '/media/shops/shop-02-fashion.jpg' WHERE id = 2;
UPDATE merchant SET shop_logo = '/media/shops/shop-03-pending.jpg' WHERE id = 3;

UPDATE category SET icon = '/media/categories/category-01-digital.jpg' WHERE id = 1;
UPDATE category SET icon = '/media/categories/category-02-appliance.jpg' WHERE id = 2;
UPDATE category SET icon = '/media/categories/category-03-fashion.jpg' WHERE id = 3;
UPDATE category SET icon = '/media/categories/category-04-food.jpg' WHERE id = 4;

UPDATE product
SET main_image = '/media/products/product-01-phone.jpg',
    sub_images = '["/media/products/product-02-phone.jpg","/media/products/product-09-foldable.jpg","/media/products/phone-detail-01.jpg"]'
WHERE id = 1;
UPDATE product
SET main_image = '/media/products/product-02-phone.jpg',
    sub_images = '["/media/products/product-01-phone.jpg","/media/products/product-09-foldable.jpg"]'
WHERE id = 2;
UPDATE product
SET main_image = '/media/products/product-03-laptop.jpg',
    sub_images = '["/media/products/laptop-detail-01.jpg","/media/products/laptop-detail-02.jpg"]'
WHERE id = 3;
UPDATE product
SET main_image = '/media/products/product-04-charger.jpg',
    sub_images = '["/media/products/charger-detail-01.jpg"]'
WHERE id = 4;
UPDATE product
SET main_image = '/media/products/product-05-tshirt.jpg',
    sub_images = '["/media/products/tshirt-detail-01.jpg","/media/products/tshirt-detail-02.jpg","/media/products/tshirt-detail-03.jpg"]'
WHERE id = 5;
UPDATE product
SET main_image = '/media/products/product-06-dress.jpg',
    sub_images = '["/media/products/dress-detail-01.jpg","/media/products/dress-detail-02.jpg"]'
WHERE id = 6;
UPDATE product
SET main_image = '/media/products/product-07-projector.jpg',
    sub_images = '["/media/products/projector-detail-01.jpg"]'
WHERE id = 7;
UPDATE product
SET main_image = '/media/products/product-08-jacket.jpg',
    sub_images = '["/media/products/jacket-detail-01.jpg","/media/products/jacket-detail-02.jpg"]'
WHERE id = 8;
UPDATE product
SET main_image = '/media/products/product-09-foldable.jpg',
    sub_images = '["/media/products/product-01-phone.jpg"]'
WHERE id = 9;

UPDATE product_sku ps
JOIN product p ON p.id = ps.product_id
SET ps.sku_image = p.main_image
WHERE ps.id BETWEEN 1 AND 22;

UPDATE order_item oi
JOIN product_sku ps ON ps.id = oi.product_sku_id
JOIN product p ON p.id = ps.product_id
SET oi.product_image = p.main_image
WHERE oi.id BETWEEN 1 AND 6;

UPDATE review
SET images = '["/media/products/laptop-detail-01.jpg"]'
WHERE id = 1;

UPDATE banner SET image_url = '/media/banners/banner-01-sale.jpg' WHERE id = 1;
UPDATE banner SET image_url = '/media/banners/banner-02-digital.jpg' WHERE id = 2;
UPDATE banner SET image_url = '/media/banners/banner-03-fashion.jpg' WHERE id = 3;
UPDATE banner SET image_url = '/media/banners/banner-04-appliance.jpg' WHERE id = 4;
