# D 后端接口说明（API 契约）

> 开发者 D 负责：账号、商家、商品后端。本文档是 D 全部接口的契约，A/B/C 前端按此联调。
> 参考：`../backend/.claude/docs/B2C 多商家电商平台：任务分工与接口说明.md`、`../database/schema.sql`。

---

## 0. 通用约定

### 0.1 Base URL 与前缀

- 本地开发地址：`http://localhost:8080`
- 所有接口统一前缀：`/api`，例如 `POST /api/auth/register`
- 内容类型：`application/json;charset=UTF-8`（除文件上传为 `multipart/form-data`）

### 0.2 鉴权方式

- 除「注册、登录」外，所有接口都需要在请求头携带 JWT：

  ```
  Authorization: Bearer <token>
  ```

- 登录接口返回 `token`，前端保存并在每次请求时带上。
- 角色与权限：
  | 角色 | 值 | 可访问 |
  |---|---|---|
  | 普通用户 | `USER` | 个人资料、地址、商品浏览等消费者接口 |
  | 商家 | `MERCHANT` | `/api/merchant/**` 商家接口 |
  | 管理员 | `ADMIN` | `/api/admin/**` 平台审核接口 |

### 0.3 统一响应格式（Result）

所有接口返回相同外层结构，`data` 为具体业务数据：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

失败时 `data` 为 `null`，`message` 为错误原因。

### 0.4 状态码约定

| HTTP 状态码 | 含义 |
|---|---|
| 200 | 成功 |
| 400 | 参数错误 / 业务校验失败（如账号重复、库存为负） |
| 401 | 未登录 / token 无效或过期 |
| 403 | 无权限（角色不符，或操作他人资源） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 0.5 命名约定

- 数据库字段为 `snake_case`（`is_default`、`created_at`）。
- Java 与 JSON 字段为 `camelCase`（`isDefault`、`createdAt`），由 MyBatis
  `map-underscore-to-camel-case` 自动映射。
- 时间字段统一格式：`yyyy-MM-dd HH:mm:ss`。
- 分页参数统一：`page`（从 1 开始）、`size`（默认 10，最大 100）。

---

## 1. 账号与个人资料

### 1.1 POST /api/auth/register —— 注册

- **权限**：公开
- **功能**：创建消费者账号，用户名不可重复。

**请求 Body**

```json
{
  "username": "user4",
  "password": "123456",
  "phone": "13800000008",
  "email": "user4@jd-demo.com",
  "nickname": "赵六",
  "gender": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | string | 是 | 用户名，唯一 |
| password | string | 是 | 密码，BCrypt 加密存储，长度 6~32 |
| phone | string | 否 | 手机号，唯一 |
| email | string | 否 | 邮箱 |
| nickname | string | 否 | 昵称，缺省用 username |
| gender | int | 否 | 0-未知 1-男 2-女 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 8,
    "username": "user4",
    "nickname": "赵六",
    "phone": "13800000008",
    "role": "USER"
  }
}
```

**错误状态码**：400（用户名/手机号已存在、参数缺失）、500

### 1.2 POST /api/auth/login —— 登录

- **权限**：公开
- **功能**：核对用户名密码，返回 JWT 与用户信息。登录接口只在真实后端联调时启用。

**请求 Body**

```json
{
  "username": "user1",
  "password": "123456"
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 4,
      "username": "user1",
      "nickname": "张三",
      "avatar": null,
      "role": "USER",
      "status": 1
    }
  }
}
```

**错误状态码**：400（参数缺失）、401（用户名或密码错误）、403（账号被禁用 status=0）、500

### 1.3 POST /api/auth/merchant-apply —— 商家入驻申请

- **权限**：登录（仅 `USER` 角色可申请）
- **功能**：提交入驻资料，生成 `audit_status=0`（待审核）的商家记录。

**请求 Body**

```json
{
  "shopName": "数码新店",
  "contactPhone": "13900000000",
  "description": "主营手机配件",
  "shopLogo": "https://img.jd-demo.com/shop/logo4.png"
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| shopName | string | 是 | 店铺名称 |
| contactPhone | string | 是 | 联系电话 |
| description | string | 否 | 店铺描述 |
| shopLogo | string | 否 | 店铺 Logo URL |

**成功响应 200**

```json
{
  "code": 200,
  "message": "申请已提交，等待平台审核",
  "data": {
    "merchantId": 4,
    "auditStatus": 0
  }
}
```

**错误状态码**：400（已是商家或已有待审核申请）、401、403、500

### 1.4 GET /api/auth/profile —— 当前登录者资料

- **权限**：登录
- **功能**：返回当前用户资料与角色，前端据此决定显示哪些菜单。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "username": "merchant1",
    "nickname": "数码旗舰店",
    "phone": "13800000002",
    "email": "merchant1@jd-demo.com",
    "avatar": null,
    "gender": 1,
    "role": "MERCHANT",
    "status": 1,
    "merchant": {
      "id": 1,
      "shopName": "数码旗舰店",
      "shopLogo": "https://img.jd-demo.com/shop/logo1.png",
      "auditStatus": 1
    }
  }
}
```

> `merchant` 字段仅在角色为 `MERCHANT` 时返回；角色为 `USER` 时返回 `merchant: null`。

**错误状态码**：401、500

### 1.5 PUT /api/users/profile —— 更新个人资料

- **权限**：登录
- **功能**：修改自己的昵称、头像、手机号。

**请求 Body**

```json
{
  "nickname": "张三丰",
  "avatar": "https://img.jd-demo.com/avatar/new.jpg",
  "phone": "13800000004"
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 4,
    "username": "user1",
    "nickname": "张三丰",
    "avatar": "https://img.jd-demo.com/avatar/new.jpg",
    "phone": "13800000004"
  }
}
```

**错误状态码**：400（手机号被占用等）、401、500

### 1.6 PUT /api/users/password —— 修改密码

- **权限**：登录
- **功能**：核对旧密码后保存新密码。

**请求 Body**

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

**错误状态码**：400（旧密码错误 / 新密码长度 6~32）、401、500

---

## 2. 收货地址

### 2.1 GET /api/addresses —— 地址列表

- **权限**：登录（消费者）
- **功能**：返回当前用户的全部收货地址。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "phone": "13800000004",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detail": "建国路88号现代城SOHO 1号楼1801室",
      "isDefault": 1
    },
    {
      "id": 2,
      "name": "张三",
      "phone": "13800000004",
      "province": "北京市",
      "city": "北京市",
      "district": "海淀区",
      "detail": "中关村大街1号海龙大厦1502室",
      "isDefault": 0
    }
  ]
}
```

**错误状态码**：401、500

### 2.2 POST /api/addresses —— 新增地址

- **权限**：登录
- **功能**：新建一条收货地址；若 `isDefault=1`，同用户其他地址自动置 0。

**请求 Body**

```json
{
  "name": "张三",
  "phone": "13800000004",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "建国路88号现代城SOHO 1号楼1801室",
  "isDefault": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| name | string | 是 | 收货人姓名 |
| phone | string | 是 | 收货人手机号 |
| province | string | 是 | 省 |
| city | string | 是 | 市 |
| district | string | 否 | 区/县 |
| detail | string | 是 | 详细地址 |
| isDefault | int | 否 | 0-否 1-是，默认 0 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 5,
    "name": "张三",
    "phone": "13800000004",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号现代城SOHO 1号楼1801室",
    "isDefault": 1
  }
}
```

**错误状态码**：400（必填缺失）、401、500

### 2.3 PUT /api/addresses/{id} —— 编辑地址

- **权限**：登录
- **功能**：修改自己的一条地址；不能修改他人的地址。

**请求 Body**

```json
{
  "name": "张三",
  "phone": "13800000004",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "建国路88号现代城SOHO 1号楼1802室",
  "isDefault": 0
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800000004",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号现代城SOHO 1号楼1802室",
    "isDefault": 0
  }
}
```

**错误状态码**：400、401、403（非本人）、404（地址不存在）、500

### 2.4 DELETE /api/addresses/{id} —— 删除地址

- **权限**：登录
- **功能**：删除自己的一条地址。

**成功响应 200**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误状态码**：401、403（非本人）、404（地址不存在）、500

### 2.5 PUT /api/addresses/{id}/default —— 设为默认地址

- **权限**：登录
- **功能**：把指定地址设为默认，同用户其他地址自动置 0。

**成功响应 200**

```json
{
  "code": 200,
  "message": "设置成功",
  "data": {
    "id": 2,
    "name": "张三",
    "phone": "13800000004",
    "province": "北京市",
    "city": "北京市",
    "district": "海淀区",
    "detail": "中关村大街1号海龙大厦1502室",
    "isDefault": 1
  }
}
```

**错误状态码**：401、403（非本人）、404、500

---

## 3. 商品分类

### 3.1 GET /api/categories —— 分类列表

- **权限**：公开
- **功能**：返回商品分类树；不带参数返回全部（顶级含 children），带 `parentId` 返回指定分类的子分类。

**请求 Query**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| parentId | long | 否 | 父分类 ID；省略时返回一级分类树 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "手机数码",
      "parentId": 0,
      "sort": 1,
      "icon": "https://img.jd-demo.com/cat/phone.png",
      "children": [
        {
          "id": 11,
          "name": "手机通讯",
          "parentId": 1,
          "sort": 1,
          "icon": null,
          "children": [
            {
              "id": 111,
              "name": "智能手机",
              "parentId": 11,
              "sort": 1,
              "icon": null
            }
          ]
        }
      ]
    }
  ]
}
```

**错误状态码**：500

---

## 4. 商品、规格与库存

### 4.1 GET /api/products —— 商品列表（搜索/分类/排序/分页）

- **权限**：公开
- **功能**：按关键词、分类、排序返回**已上架（status=1）**商品的分页列表。

**请求 Query**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| keyword | string | 否 | 关键词，模糊匹配商品名称 |
| categoryId | long | 否 | 分类 ID，包含其子分类下的商品 |
| sort | string | 否 | `default`(综合销量) / `priceAsc` / `priceDesc` / `sales`(销量) / `newest`(最新) |
| page | int | 否 | 页码，从 1 开始，默认 1 |
| size | int | 否 | 每页条数，默认 10，最大 100 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 8,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "name": "智选 Pro 5G 手机 12GB+256GB 钛空灰",
        "subtitle": "旗舰芯片 | 徕卡光学 | 120W快充",
        "mainImage": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",
        "price": 4999.00,
        "salesCount": 1520
      }
    ]
  }
}
```

**错误状态码**：400（size 超限）、500

### 4.2 GET /api/products/{id} —— 商品详情

- **权限**：公开
- **功能**：返回已上架商品的详情、全部可用 SKU（规格）、库存与价格。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "智选 Pro 5G 手机 12GB+256GB 钛空灰",
    "subtitle": "旗舰芯片 | 徕卡光学 | 120W快充",
    "mainImage": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",
    "subImages": [
      "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80",
      "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=900&q=80"
    ],
    "description": "搭载最新旗舰处理器，6.7英寸OLED屏幕...",
    "detailHtml": "<p>产品详情...</p>",
    "price": 4999.00,
    "salesCount": 1520,
    "merchantId": 1,
    "merchantName": "数码旗舰店",
    "categoryId": 111,
    "categoryName": "智能手机",
    "skus": [
      {
        "id": 1,
        "skuName": "智选Pro 5G 钛空灰 12GB+256GB",
        "price": 4999.00,
        "originalPrice": 5499.00,
        "stock": 500,
        "attributes": { "颜色": "钛空灰", "版本": "12GB+256GB" },
        "skuImage": "https://img.jd-demo.com/sku/s1.jpg"
      }
    ]
  }
}
```

> 未上架/不存在商品返回 404。`attributes` 为 JSON 对象。

**错误状态码**：404（商品不存在或未上架）、500

### 4.3 POST /api/files/images —— 图片上传

- **权限**：登录（消费者传头像、商家传 Logo/商品图、管理员传轮播图）
- **功能**：接收图片文件，返回可访问的图片 URL，由调用方保存到业务资料中。

**请求（multipart/form-data）**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| file | File | 是 | 图片文件（jpg/png/webp，≤5MB） |

**成功响应 200**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:8080/upload/20260810/xxx.jpg"
  }
}
```

**错误状态码**：400（文件为空 / 类型不支持 / 超大小）、401、500

### 4.4 GET /api/merchant/products —— 商家商品列表

- **权限**：商家（MERCHANT）
- **功能**：返回该商家自己的商品及审核状态。

**请求 Query**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| status | int | 否 | 0-待审核 1-上架 2-下架 3-审核拒绝；省略返回全部 |
| page | int | 否 | 默认 1 |
| size | int | 否 | 默认 10，最大 100 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "name": "智选 Pro 5G 手机 12GB+256GB 钛空灰",
        "mainImage": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",
        "price": 4999.00,
        "status": 1,
        "auditRemark": null,
        "salesCount": 1520,
        "createdAt": "2026-07-01 10:00:00"
      }
    ]
  }
}
```

**错误状态码**：401、403（非商家）、500

### 4.5 GET /api/merchant/products/{id} —— 商家商品详情

- **权限**：商家（MERCHANT）
- **功能**：返回自己某件商品的完整资料和全部 SKU，供编辑页回填。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "智选 Pro 5G 手机 12GB+256GB 钛空灰",
    "subtitle": "旗舰芯片 | 徕卡光学 | 120W快充",
    "mainImage": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",
    "subImages": ["https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80"],
    "description": "搭载最新旗舰处理器...",
    "detailHtml": "<p>产品详情...</p>",
    "price": 4999.00,
    "status": 1,
    "auditRemark": null,
    "salesCount": 1520,
    "categoryId": 111,
    "skus": [
      {
        "id": 1,
        "skuName": "智选Pro 5G 钛空灰 12GB+256GB",
        "price": 4999.00,
        "originalPrice": 5499.00,
        "stock": 500,
        "attributes": { "颜色": "钛空灰", "版本": "12GB+256GB" },
        "skuImage": "https://img.jd-demo.com/sku/s1.jpg"
      }
    ]
  }
}
```

**错误状态码**：401、403（非商家或非本人商品）、404、500

### 4.6 POST /api/merchant/products —— 发布商品

- **权限**：商家（MERCHANT）
- **功能**：新建商品和多个 SKU，创建后 `status=0`（待审核），审核通过才可销售。

**请求 Body**

```json
{
  "categoryId": 111,
  "name": "智选 Pro 5G 手机 12GB+256GB 钛空灰",
  "subtitle": "旗舰芯片 | 徕卡光学 | 120W快充",
  "mainImage": "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",
  "subImages": ["https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80"],
  "description": "搭载最新旗舰处理器...",
  "detailHtml": "<p>产品详情...</p>",
  "skus": [
    {
      "skuName": "智选Pro 5G 钛空灰 12GB+256GB",
      "price": 4999.00,
      "originalPrice": 5499.00,
      "stock": 500,
      "attributes": { "颜色": "钛空灰", "版本": "12GB+256GB" },
      "skuImage": "https://img.jd-demo.com/sku/s1.jpg"
    }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| categoryId | long | 是 | 商品分类 |
| name | string | 是 | 商品名称 |
| subtitle | string | 否 | 副标题 |
| mainImage | string | 是 | 主图 URL |
| subImages | string[] | 否 | 子图 URL 列表 |
| description / detailHtml | string | 否 | 描述 / 富文本详情 |
| skus | object[] | 是 | 至少一个 SKU |
| skus[].skuName | string | 是 | SKU 名称 |
| skus[].price | number | 是 | 售价，>0 |
| skus[].originalPrice | number | 否 | 原价（划线价） |
| skus[].stock | int | 是 | 初始库存，≥0 |
| skus[].attributes | object | 否 | 规格属性，如 `{"颜色":"红"}` |
| skus[].skuImage | string | 否 | SKU 图片 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "发布成功，等待审核",
  "data": {
    "productId": 10
  }
}
```

**错误状态码**：400（必填缺失 / SKU 为空 / 价格或库存非法）、401、403、500

### 4.7 PUT /api/merchant/products/{id} —— 编辑商品

- **权限**：商家（MERCHANT），只能编辑自己的商品
- **功能**：更新商品基本信息和 SKU（可整体替换 SKU 列表）；修改后重新进入 `status=0` 待审核。

**请求 Body**：与 4.6 POST 相同结构。

**成功响应 200**

```json
{
  "code": 200,
  "message": "修改成功，等待审核",
  "data": {
    "productId": 10,
    "status": 0
  }
}
```

**错误状态码**：400、401、403（非本人商品）、404、500

### 4.8 PUT /api/merchant/products/{id}/off-shelf —— 下架商品

- **权限**：商家（MERCHANT）
- **功能**：停止销售，`status=2`（下架），消费者端不再展示。

**请求**：无 Body。

**成功响应 200**

```json
{
  "code": 200,
  "message": "已下架",
  "data": {
    "productId": 10,
    "status": 2
  }
}
```

**错误状态码**：401、403、404、500

### 4.9 PUT /api/merchant/skus/{id}/stock —— 修改 SKU 库存

- **权限**：商家（MERCHANT），只能改自己商品的 SKU
- **功能**：更新某 SKU 库存，不允许负数。路径中的 `skus` 是代码名称。

**请求 Body**

```json
{
  "stock": 600
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "库存更新成功",
  "data": {
    "skuId": 1,
    "stock": 600
  }
}
```

**错误状态码**：400（库存不能为负数）、401、403、404（SKU 不存在）、500

---

## 5. 商家店铺

### 5.1 GET /api/merchant/shop —— 店铺资料

- **权限**：商家（MERCHANT）
- **功能**：返回自己的店铺名称、Logo、公告等资料。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "shopName": "数码旗舰店",
    "shopLogo": "https://img.jd-demo.com/shop/logo1.png",
    "description": "主营手机、电脑、数码配件，正品保障",
    "contactPhone": "13800000002",
    "auditStatus": 1
  }
}
```

**错误状态码**：401、403（非商家）、500

### 5.2 PUT /api/merchant/shop —— 更新店铺资料

- **权限**：商家（MERCHANT）
- **功能**：修改自己的店铺名称、Logo、描述、联系电话。

**请求 Body**

```json
{
  "shopName": "数码旗舰店",
  "shopLogo": "https://img.jd-demo.com/shop/logo1.png",
  "description": "主营手机、电脑、数码配件，正品保障",
  "contactPhone": "13800000002"
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "shopName": "数码旗舰店",
    "shopLogo": "https://img.jd-demo.com/shop/logo1.png",
    "description": "主营手机、电脑、数码配件，正品保障",
    "contactPhone": "13800000002",
    "auditStatus": 1
  }
}
```

**错误状态码**：400、401、403、500

---

## 6. 平台审核 —— 用户管理

### 6.1 GET /api/admin/users —— 用户列表

- **权限**：管理员（ADMIN）
- **功能**：返回平台用户列表（不返回密码）。

**请求 Query**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| keyword | string | 否 | 按用户名/昵称/手机号模糊搜索 |
| role | string | 否 | USER / MERCHANT / ADMIN |
| status | int | 否 | 0-禁用 1-正常 |
| page | int | 否 | 默认 1 |
| size | int | 否 | 默认 10，最大 100 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 7,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 4,
        "username": "user1",
        "nickname": "张三",
        "phone": "13800000004",
        "email": "user1@jd-demo.com",
        "role": "USER",
        "status": 1,
        "createdAt": "2026-06-20 10:00:00"
      }
    ]
  }
}
```

**错误状态码**：401、403（非管理员）、500

### 6.2 PUT /api/admin/users/{id}/status —— 禁用/启用用户

- **权限**：管理员（ADMIN）
- **功能**：修改用户账号状态（禁用后无法登录）。

**请求 Body**

```json
{
  "status": 0
}
```

**成功响应 200**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 4,
    "status": 0
  }
}
```

**错误状态码**：400（status 非法）、401、403、404、500

---

## 7. 平台审核 —— 商家审核

### 7.1 GET /api/admin/merchants/pending —— 待审核商家

- **权限**：管理员（ADMIN）
- **功能**：返回 `audit_status=0`（待审核）的商家申请列表。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 3,
      "shopName": "待审核商家",
      "description": "新入驻商家，等待审核",
      "contactPhone": "13800000007",
      "applicant": {
        "username": "merchant3",
        "nickname": "待审核商家"
      },
      "createdAt": "2026-07-01 09:00:00"
    }
  ]
}
```

**错误状态码**：401、403、500

### 7.2 GET /api/admin/merchants/{id} —— 商家申请详情

- **权限**：管理员（ADMIN）
- **功能**：返回店铺申请资料、证明图片（`shopLogo`）和审核状态，供审核前查看。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3,
    "userId": 7,
    "shopName": "待审核商家",
    "shopLogo": "https://img.jd-demo.com/shop/logo3.png",
    "description": "新入驻商家，等待审核",
    "contactPhone": "13800000007",
    "auditStatus": 0,
    "auditRemark": null,
    "applicant": {
      "id": 7,
      "username": "merchant3",
      "nickname": "待审核商家",
      "phone": "13800000007"
    }
  }
}
```

**错误状态码**：401、403、404、500

### 7.3 PUT /api/admin/merchants/{id}/audit —— 审核商家

- **权限**：管理员（ADMIN）
- **功能**：通过或驳回商家入驻申请。通过后 `audit_status=1`、`status=1`，并把申请人的用户角色改为 `MERCHANT`；驳回则 `audit_status=2`。

**请求 Body**

```json
{
  "approve": true,
  "remark": "资料齐全，审核通过"
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| approve | boolean | 是 | true-通过 false-驳回 |
| remark | string | 否 | 审核备注 |

**成功响应 200**

```json
{
  "code": 200,
  "message": "审核完成",
  "data": {
    "merchantId": 3,
    "auditStatus": 1,
    "remark": "资料齐全，审核通过"
  }
}
```

**错误状态码**：400（approve 缺失）、401、403、404、500

---

## 8. 平台审核 —— 商品审核

### 8.1 GET /api/admin/products/pending —— 待审核商品

- **权限**：管理员（ADMIN）
- **功能**：返回 `status=0`（待审核）的商品列表。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 8,
      "name": "春秋夹克外套 男款防风",
      "mainImage": "https://images.unsplash.com/photo-1520975954732-35dd22299614?auto=format&fit=crop&w=900&q=80",
      "price": 199.00,
      "status": 0,
      "merchantId": 2,
      "shopName": "服饰优选店",
      "categoryId": 312,
      "createdAt": "2026-07-02 14:00:00"
    }
  ]
}
```

**错误状态码**：401、403、500

### 8.2 GET /api/admin/products/{id} —— 待审核商品详情

- **权限**：管理员（ADMIN）
- **功能**：返回未上架商品的图片、描述、SKU、商家和审核状态；此接口不会暴露给消费者。

**成功响应 200**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 8,
    "name": "春秋夹克外套 男款防风",
    "subtitle": "休闲百搭 | 轻薄防风",
    "mainImage": "https://images.unsplash.com/photo-1520975954732-35dd22299614?auto=format&fit=crop&w=900&q=80",
    "subImages": ["https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80"],
    "description": "防风面料，可拆卸帽子...",
    "detailHtml": "<p>产品详情...</p>",
    "price": 199.00,
    "status": 0,
    "auditRemark": null,
    "merchantId": 2,
    "shopName": "服饰优选店",
    "categoryId": 312,
    "skus": [
      {
        "id": 20,
        "skuName": "夹克 黑色 M",
        "price": 199.00,
        "originalPrice": 299.00,
        "stock": 100,
        "attributes": { "颜色": "黑色", "尺码": "M" },
        "skuImage": "https://img.jd-demo.com/sku/s20.jpg"
      }
    ]
  }
}
```

**错误状态码**：401、403、404、500

### 8.3 PUT /api/admin/products/{id}/audit —— 审核商品

- **权限**：管理员（ADMIN）
- **功能**：通过或驳回商品。通过后 `status=1`（上架可售）；驳回则 `status=3`（审核拒绝）。

**请求 Body**

```json
{
  "approve": true,
  "remark": "商品信息完整，审核通过"
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| approve | boolean | 是 | true-通过 false-驳回 |
| remark | string | 否 | 审核备注，存入 `audit_remark` |

**成功响应 200**

```json
{
  "code": 200,
  "message": "审核完成",
  "data": {
    "productId": 8,
    "status": 1,
    "remark": "商品信息完整，审核通过"
  }
}
```

**错误状态码**：400、401、403、404、500

---

## 附录：D 接口清单汇总

| 序号 | 方法 | 路径 | 权限 | 模块 |
|---|---|---|---|---|
| 1 | POST | /api/auth/register | 公开 | auth |
| 2 | POST | /api/auth/login | 公开 | auth |
| 3 | POST | /api/auth/merchant-apply | USER | auth |
| 4 | GET | /api/auth/profile | 登录 | auth |
| 5 | PUT | /api/users/profile | 登录 | auth |
| 6 | PUT | /api/users/password | 登录 | auth |
| 7 | GET | /api/addresses | 登录 | auth |
| 8 | POST | /api/addresses | 登录 | auth |
| 9 | PUT | /api/addresses/{id} | 登录 | auth |
| 10 | DELETE | /api/addresses/{id} | 登录 | auth |
| 11 | PUT | /api/addresses/{id}/default | 登录 | auth |
| 12 | GET | /api/categories | 公开 | catalog |
| 13 | GET | /api/products | 公开 | catalog |
| 14 | GET | /api/products/{id} | 公开 | catalog |
| 15 | POST | /api/files/images | 登录 | common |
| 16 | GET | /api/merchant/products | MERCHANT | catalog |
| 17 | GET | /api/merchant/products/{id} | MERCHANT | catalog |
| 18 | POST | /api/merchant/products | MERCHANT | catalog |
| 19 | PUT | /api/merchant/products/{id} | MERCHANT | catalog |
| 20 | PUT | /api/merchant/products/{id}/off-shelf | MERCHANT | catalog |
| 21 | PUT | /api/merchant/skus/{id}/stock | MERCHANT | catalog |
| 22 | GET | /api/merchant/shop | MERCHANT | merchant |
| 23 | PUT | /api/merchant/shop | MERCHANT | merchant |
| 24 | GET | /api/admin/users | ADMIN | admin |
| 25 | PUT | /api/admin/users/{id}/status | ADMIN | admin |
| 26 | GET | /api/admin/merchants/pending | ADMIN | admin |
| 27 | GET | /api/admin/merchants/{id} | ADMIN | admin |
| 28 | PUT | /api/admin/merchants/{id}/audit | ADMIN | admin |
| 29 | GET | /api/admin/products/pending | ADMIN | admin |
| 30 | GET | /api/admin/products/{id} | ADMIN | admin |
| 31 | PUT | /api/admin/products/{id}/audit | ADMIN | admin |
