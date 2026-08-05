# B2C 多商家电商平台：任务分工与接口说明

# 五人分工

|**成员**|**主要任务**|**需要完成的页面或功能**|**最终交付**|**依赖谁**|
|---|---|---|---|---|
|A|消费者 H5 网页端|登录页、首页、商品列表/详情、购物车、结算、订单、个人中心、地址、退款申请|浏览器可完成“找商品→加购→下单→付款→看订单”|D 的账号/商品接口，E 的购物车/订单接口|
|B|消费者 App 和微信小程序|手机首页、商品列表/详情、购物车、结算、订单、个人中心、地址、退款申请|Android App 和小程序均可完成核心购物流程|D/E 的接口；与 A 保持消费者功能一致|
|C|商家后台和管理员后台<br>（前端）|商家：店铺、商品发布、库存、订单发货、退款处理、评价回复；管理员：商家审核、商品审核、退款仲裁、用户管理、统计、轮播图|两套 PC 后台，可演示商家经营和平台监管|D 的商家/商品接口，E 的订单/退款/统计接口|
|D|账号、商品、商家后端|注册登录、角色权限、地址、商家入驻、店铺、分类、商品规格、库存；管理员审核用户/商家/商品|可运行的 Java 接口、相关数据库表、接口说明|A/B/C 使用其接口；与 E 约定库存扣减|
|E|交易、售后、平台后端|购物车、下单、支付、订单、发货、评价、退款、仲裁；管理员统计、日志、轮播图；数据库汇总和测试|可运行的 Java 接口、完整数据库脚本、测试说明|A/B/C 使用其接口；与 D 协作库存和表结构|

## 五人共同验收的一条流程

1. 申请者在商家入驻申请页提交申请；C 用管理员后台审核通过。

2. C 用商家后台发布商品；管理员后台审核商品。

3. A 或 B 在消费者端找到商品、选择规格、加入购物车、提交订单并模拟支付。

4. C 在商家后台看到订单并发货。

5. A 或 B 确认收货、评价，或申请退款。

6. C 处理退款；有争议时 C 用管理员后台仲裁。

# 三个前端的功能与 API 清单

这一节给 A、B、C 直接使用。每一行的“需要接口”就是页面完成该功能时要调用的完整后端路径；括号里的 D/E 是后端负责人。开发初期可以用临时 API 函数返回相同字段的假数据，后端完成后再替换为真实请求。

## 1\.1 A：消费者 H5 网页端

|**页面/功能**|**用户要做什么**|**需要接口**|
|---|---|---|
|登录、注册、申请成为商家|注册账号、真实登录、提交商家入驻资料|POST   /api/auth/register、POST /api/auth/login、POST /api/auth/merchant\-apply（D）|
|首页|查看轮播图、分类和推荐商品|GET /api/banners（E）、GET   /api/categories（D）、GET /api/products（D）|
|搜索/分类商品页|搜关键词、按分类/价格排序、翻页|GET   /api/products（D；参数包含 keyword、categoryId、sort、page、size）|
|商品详情页|看图片、介绍、商品规格、库存、评价；选择规格|GET   /api/products/\{id\}（D）、GET /api/products/\{id\}/reviews（E）|
|加入购物车|在详情页选中具体规格后加入购物车|POST   /api/cart/items（E）|
|购物车|查看商品、改数量、勾选结算、删除商品|GET /api/cart、PUT   /api/cart/items/\{id\}、DELETE /api/cart/items/\{id\}、DELETE /api/cart/selected（E）|
|收货地址|查看、新增、编辑、删除、设默认地址|GET/POST   /api/addresses、PUT/DELETE /api/addresses/\{id\}、PUT /api/addresses/\{id\}/default（D）|
|结算和下单|选择地址和购物车商品，提交订单|POST /api/orders（E）|
|订单列表/详情|看待付款、待收货等订单和物流单号|GET /api/orders、GET   /api/orders/\{id\}（E）|
|支付和收货|模拟付款、取消未付款订单、确认收货|POST   /api/orders/\{id\}/pay、PUT /api/orders/\{id\}/cancel、PUT   /api/orders/\{id\}/confirm\-receipt（E）|
|评价|对已完成订单提交星级和文字评价|POST   /api/reviews（E）|
|退款/售后|申请退款、查看进度、填退货单号、申请平台介入|POST   /api/refunds、GET /api/refunds、GET /api/refunds/\{id\}、PUT   /api/refunds/\{id\}/return\-logistics、PUT /api/refunds/\{id\}/appeal（E）|
|个人中心|查看/修改资料、改密码、上传头像|GET   /api/auth/profile、PUT /api/users/profile、PUT /api/users/password、POST   /api/files/images（D）|

## 1\.2 B：消费者 App 和微信小程序

App 与小程序需要完成的购物功能和 A 的 H5 相同，只是页面更适合手机操作。B 不需要另要一套后端接口，直接复用同一套 API。

|**页面/功能**|**需要接口**|
|---|---|
|手机首页、分类、搜索、商品详情|GET /api/banners（E）、GET   /api/categories、GET /api/products、GET /api/products/\{id\}、GET   /api/products/\{id\}/reviews（D/E）|
|选择商品规格、加入购物车、管理购物车|POST   /api/cart/items、GET /api/cart、PUT /api/cart/items/\{id\}、DELETE   /api/cart/items/\{id\}、DELETE /api/cart/selected（E）|
|地址、结算、下单|GET/POST   /api/addresses、PUT/DELETE /api/addresses/\{id\}、PUT /api/addresses/\{id\}/default（D）；POST   /api/orders（E）|
|订单、模拟支付、取消、收货|GET /api/orders、GET   /api/orders/\{id\}、POST /api/orders/\{id\}/pay、PUT /api/orders/\{id\}/cancel、PUT   /api/orders/\{id\}/confirm\-receipt（E）|
|评价、退款、申诉|POST   /api/reviews、POST /api/refunds、GET /api/refunds、GET /api/refunds/\{id\}、PUT   /api/refunds/\{id\}/return\-logistics、PUT /api/refunds/\{id\}/appeal（E）|
|手机端个人中心|GET   /api/auth/profile、PUT /api/users/profile、PUT /api/users/password、POST   /api/files/images（D）|

## 1\.3 C：商家后台和管理员后台

|**后台页面/功能**|**操作人要做什么**|**需要接口**|
|---|---|---|
|商家店铺设置|查看/修改店铺名称、Logo、公告|GET   /api/merchant/shop、PUT /api/merchant/shop、POST /api/files/images（D）|
|商家看板|看销售额、订单数、待发货数量|GET   /api/merchant/dashboard（E）|
|商家商品管理|查看、新建、编辑、下架商品；修改每个商品规格的库存|GET   /api/merchant/products、GET /api/merchant/products/\{id\}、POST   /api/merchant/products、PUT /api/merchant/products/\{id\}、PUT   /api/merchant/products/\{id\}/off\-shelf、PUT /api/merchant/skus/\{id\}/stock、GET   /api/categories、POST /api/files/images（D）|
|商家订单和发货|看自己的订单详情，填写物流单号发货|GET   /api/merchant/orders、GET /api/merchant/orders/\{id\}、PUT   /api/merchant/orders/\{id\}/ship（E）|
|商家退款和评价|审核退款、确认收到退货、回复评价|GET   /api/merchant/refunds、PUT /api/merchant/refunds/\{id\}/audit、PUT   /api/merchant/refunds/\{id\}/confirm\-return、GET /api/merchant/reviews、PUT   /api/merchant/reviews/\{id\}/reply（E）|
|管理员统计看板|看用户、商家、订单、销售额图表|GET   /api/admin/dashboard（E）|
|管理员用户管理|查看用户、禁用/启用账号|GET   /api/admin/users、PUT /api/admin/users/\{id\}/status（D）|
|管理员商家审核|查看商家申请资料，通过或驳回|GET   /api/admin/merchants/pending、GET /api/admin/merchants/\{id\}、PUT   /api/admin/merchants/\{id\}/audit（D）|
|管理员商品审核|查看待审核商品详情，通过或驳回|GET   /api/admin/products/pending、GET /api/admin/products/\{id\}、PUT   /api/admin/products/\{id\}/audit（D）|
|管理员退款仲裁|查看争议退款，支持用户或驳回|GET   /api/admin/refunds、PUT /api/admin/refunds/\{id\}/arbitrate（E）|
|管理员轮播图和日志|新增/编辑/删除首页轮播图，查看关键操作记录|GET   /api/admin/banners、POST /api/admin/banners、PUT /api/admin/banners/\{id\}、DELETE   /api/admin/banners/\{id\}、POST /api/files/images（D）、GET /api/admin/logs（E）|



# 项目简介

这是一个“平台统一监管、多个商家入驻、消费者购买”的 B2C 商城 Demo。

## 2\.1 项目总体框架

项目采用“三个前端组 \+ 一个 Java 后端 \+ 一个 MySQL 数据库”的模块化单体架构。所有正式 API 都在同一个 Spring Boot 后端中运行，但按业务分成 D 和 E 两个清晰模块，



![image\.png](图片和附件/image.png)

|**层次**|**包含什么**|**负责什么**|**不负责什么**|
|---|---|---|---|
|前端界面层|H5、App、小程序、商家后台、管理员后台|页面、表单、按钮、展示数据、调用   API、显示成功/失败提示|不直接读写数据库，不判断真实库存和权限|
|前端临时 API 层|开发期的临时函数|在 Java 接口未完成时返回假商品、假订单等数据，让页面先开发|不属于最终系统，不处理真实业务规则|
|统一 API 层|/api/\.\.\. 路径|接收前端请求，返回统一格式的结果|不直接把复杂规则写在页面中|
|D 后端模块|账号、商家、商品模块|登录权限、用户地址、商家入驻、商品规格、库存、用户/商家/商品审核|购物车、付款、退款状态机|
|E 后端模块|交易、售后、运营模块|购物车、订单拆分、模拟支付、发货、评价、退款、仲裁、统计、轮播、日志|商品基本信息和库存规则的最终维护|
|数据层|MySQL、schema\.sql、data\.sql|保存系统真实演示数据；保证订单、库存、退款等数据可追溯|不提供页面和按钮|

## 2\.2后端内部如何分层

每个 API 都按固定结构实现，便于 D/E 共同维护：

```Markdown
Controller：接收前端请求，例如“提交订单”
↓
Service：判断业务规则，例如“库存够不够、订单能不能付款”
↓
Mapper：把要查询/修改的数据交给数据库
↓
MySQL：保存用户、商品、订单、退款等数据
```



这就是“前后端分离”的最终框架：前端只负责界面和请求；后端负责规则与数据；数据库负责保存；临时 API 函数只用于开发前期，最终不会代替 Java 后端。

## 2\.3最终项目文件夹架构

下面是项目最终上传到 GitHub 的根目录结构。它采用一个仓库管理所有前端、后端、数据库和文档，方便五个人在同一个项目中协作。

```Bash
jd-ecommerce/
├─ frontend-user-web/          # A：消费者 H5 网页端
│  ├─ src/                     # 页面、组件、临时 API 函数、真实 API 请求
│  ├─ public/                  # 网页静态图片、图标
│  ├─ package.json             # H5 依赖和启动命令
│  └─ README.md                # H5 启动说明
│
├─ frontend-user-mobile/       # B：消费者 App 和微信小程序（uni-app）
│  ├─ src/                     # 手机端页面、组件、临时 API 函数、真实 API 请求
│  ├─ static/                  # App/小程序静态资源
│  ├─ pages.json               # 手机端页面和导航配置
│  ├─ manifest.json            # App 打包配置
│  └─ package.json             # 手机端依赖和启动命令
│
├─ frontend-merchant/          # C：商家 PC 后台
│  ├─ src/                     # 店铺、商品、订单、退款、评价等页面
│  ├─ public/                  # 商家后台静态资源
│  └─ package.json
│
├─ frontend-admin/             # C：管理员 PC 后台
│  ├─ src/                     # 审核、用户、仲裁、统计、轮播图等页面
│  ├─ public/                  # 管理员后台静态资源
│  └─ package.json
│
├─ backend/                    # D/E：唯一的正式 Java 后端
│  ├─ pom.xml                  # Maven 依赖和 Java 25 配置
│  └─ src/main/
│     ├─ java/com/team/ecommerce/
│     │  ├─ common/            # D/E 共用：统一成功/失败返回、异常、分页
│     │  ├─ config/            # D/E 共用：跨域、接口文档、密码配置
│     │  ├─ security/          # D：登录、JWT、角色权限
│     │  ├─ auth/              # D：用户资料、地址、商家入驻
│     │  ├─ catalog/           # D：分类、商品、商品规格、库存、商品审核
│     │  ├─ merchant/          # D：店铺资料；E：商家订单/发货数据接口
│     │  ├─ trade/             # E：购物车、下单、订单、模拟支付、发货
│     │  ├─ aftersales/        # E：评价、退款、申诉、仲裁
│     │  └─ admin/             # D：用户/商家/商品审核；E：统计、日志、轮播图
│     └─ resources/
│        ├─ application.yml    # 后端通用配置
│        └─ application-dev.yml# 本地 MySQL 连接配置（不提交密码）
│
├─ database/                   # E 汇总，D 共同维护
│  ├─ schema.sql               # 从零创建所有表、索引和外键
│  └─ data.sql                 # 答辩演示账号、商品、订单、退款数据
│
├─ docs/                       # 五人共同维护的项目说明
│  ├─ api-contract.md          # 接口字段、请求和响应约定
│  ├─ database-design.md       # 表关系、订单/退款状态说明
│  ├─ test-cases.md            # 核心功能测试步骤
│  └─ deployment.md            # 启动、部署和答辩环境说明
│
├─ .github/                    # GitHub 自动检查和 PR 模板
│  ├─ workflows/
│  └─ pull_request_template.md
│
├─ .env.example                # 环境变量示例；不放真实密码或密钥
├─ docker-compose.yml           # 可选：MySQL、后端和前端的一键启动配置
└─ README.md                    # 新组员从零启动项目的总说明
```

这棵目录树的关键规则：

- A、B、C 只在各自的 frontend\-\* 目录开发页面和临时 API 函数；

- D、E 只在 backend/ 和 database/ 中实现真实规则和数据；

- 临时 API 函数保留在前端各自目录里，不单独建立 Mock Server 文件夹；

- schema\.sql 与 data\.sql 是唯一数据库初始化入口，所有成员必须从这两个文件得到相同的演示环境；

- docs/ 放共同约定，防止前端和后端各自理解不同的接口字段。

|**角色**|**使用的界面**|**可以做什么**|
|---|---|---|
|消费者|H5 网页、Android   App、微信小程序|浏览、加购、下单、模拟支付、收货、评价、退款|
|商家|PC 商家后台|入驻、发布商品、管理库存、发货、处理退款、回复评价|
|管理员|PC 管理员后台|审核商家/商品、禁用用户、仲裁退款、看统计、管理轮播图|

本期不做真实支付、真实物流、优惠券、秒杀、聊天、推荐算法和微服务。支付和物流均为模拟流程，重点展示角色权限、商品、交易和售后闭环。

# 技术选型与数据库

|**部分**|**采用技术**|**白话解释**|
|---|---|---|
|后端|Java 25 LTS、Spring   Boot 3\.5\.x|处理登录、商品、订单、退款等真正业务规则的程序|
|数据库|MySQL 8|保存用户、商品、订单、退款等真实演示数据|
|数据访问|MyBatis|让 Java 程序读取和写入   MySQL 的工具|
|H5、商家、管理员端|Vue 3、Vite、Pinia、Element   Plus|制作浏览器里的网页界面|
|App、小程序|uni\-app（Vue 3）|一套手机端页面同时发布   Android App 和微信小程序|
|接口说明|OpenAPI /   Knife4j|写清楚页面向后端要什么、后端会回什么的说明书|
|图表|ECharts|在商家和管理员后台显示统计图|

数据库保留两个脚本名称：

- schema\.sql：创建所有表、索引和外键；

- data\.sql：放入需要的管理员、商家、消费者、商品、订单、退款等演示数据。

# 4\. 前后端怎样同时开发

## 4\.1 前端先做界面，接口先用临时函数

前端的主要工作确实是做界面：页面布局、按钮、表单、商品展示、购物车、订单状态、加载和报错提示。后端还没有完成时，前端不必搭建独立 Mock Server。

前端只要在自己的项目中写临时 API 函数，先返回准备好的假数据即可。例如“获取商品列表”暂时返回 10 件演示商品；“加入购物车”暂时返回“加入成功”。这样页面能先完整跑起来。

|**阶段**|**前端做什么**|**后端做什么**|
|---|---|---|
|后端未完成|页面调用临时 API 函数，显示假商品、假订单和假退款进度|D/E 按第 5 节接口清单写真实 Java 接口和数据库|
|后端完成接口|把临时 API 函数内部的假数据替换成真实请求|返回和接口清单一致的真实数据|
|联调|用真实接口测试页面；修复字段和状态显示问题|检查权限、库存、订单和退款规则|

## 4\.2 一次点击是怎样从界面走到后端的

用户在商品详情页点“加入购物车”

↓

前端页面调用“加入购物车”的临时 API 函数

↓

开发前期：临时函数直接返回“加入成功”

后端完成后：同一个函数改为请求 POST /api/cart/items

↓

Java Backend 检查库存并写入 MySQL

↓

前端页面提示结果，更新购物车数量

这样做的关键是：页面不需要重写；只替换“临时 API 函数里面怎样拿数据”。

# 5\. 后端 API 总清单：谁在什么页面用、系统做什么

说明：下面所有路径均为完整路径，已经包含统一前缀 /api。

## 5\.1 账号、个人资料和地址（D）

|**接口**|**谁在什么页面使用**|**系统做什么**|
|---|---|---|
|POST   /api/auth/register|消费者在注册页点注册|创建消费者账号，避免账号重复|
|POST   /api/auth/login|三类角色在登录页点登录|核对密码，返回登录身份；只在真实后端联调时启用|
|POST   /api/auth/merchant\-apply|消费者在申请商家页提交资料|创建待审核商家申请|
|GET   /api/auth/profile|用户进入“我的”或后台首页|返回当前登录者资料和角色，决定显示什么菜单|
|PUT   /api/users/profile|用户在个人资料页点保存|更新自己的昵称、头像、手机号|
|PUT   /api/users/password|用户在修改密码页确认|核对旧密码并保存新密码|
|GET   /api/addresses|地址管理页或结算页|返回当前消费者全部地址|
|POST   /api/addresses|新增地址页点保存|新建一条收货地址|
|PUT   /api/addresses/\{id\}|地址页点编辑后保存|修改自己的某一条地址|
|DELETE   /api/addresses/\{id\}|地址页点删除|删除自己的地址|
|PUT   /api/addresses/\{id\}/default|地址页点设为默认|指定结算时优先使用的地址|

## 5\.2 商品、分类、规格和库存（D）

|**接口**|**谁在什么页面使用**|**系统做什么**|
|---|---|---|
|GET   /api/categories|消费者首页分类、商家发布商品页|返回商品分类，例如“数码 → 耳机”|
|GET   /api/products|消费者首页、分类页、搜索页|根据关键词、分类、排序返回一页商品|
|GET   /api/products/\{id\}|消费者点开某件商品|返回商品详情、可选商品规格、价格和库存|
|POST   /api/files/images|消费者改头像、商家上传 Logo/商品图、管理员配置轮播图时|接收图片并返回图片地址，供页面把该地址保存到头像、商品或轮播图资料中|
|GET   /api/merchant/products|商家后台我的商品页|返回该商家自己的商品和审核状态|
|GET   /api/merchant/products/\{id\}|商家在商品列表点编辑|返回这件商品的完整资料和全部商品规格，供编辑页面回填内容|
|POST   /api/merchant/products|商家发布商品页点发布|新建商品和多个商品规格，进入待审核|
|PUT   /api/merchant/products/\{id\}|商家编辑商品后保存|更新自己的商品和规格|
|PUT   /api/merchant/products/\{id\}/off\-shelf|商家商品页点下架|停止销售该商品|
|PUT   /api/merchant/skus/\{id\}/stock|商家修改某个规格库存后保存|更新该商品规格库存，不允许负数；路径中的 skus 是代码名称|
|GET   /api/admin/products/pending|管理员待审核商品页|返回等待审核的商品|
|GET   /api/admin/products/\{id\}|管理员在待审核商品列表点查看详情|返回未上架商品的图片、描述、商品规格、商家和审核状态；不会暴露给消费者|
|PUT   /api/admin/products/\{id\}/audit|管理员点通过/驳回|修改商品审核状态；通过后才可销售|

## 5\.3 购物车、订单和模拟支付（E）

|**接口**|**谁在什么页面使用**|**系统做什么**|
|---|---|---|
|GET /api/cart|消费者打开购物车或结算|返回购物车，并重新检查价格和库存|
|POST   /api/cart/items|商品详情页点加入购物车|将选中的商品规格放入购物车；已有则增加数量|
|PUT   /api/cart/items/\{id\}|购物车页改数量或勾选状态|修改这一项的数量或是否结算|
|DELETE   /api/cart/items/\{id\}|购物车某一行点删除|删除这一个购物车商品|
|DELETE   /api/cart/selected|购物车点删除已选|批量删除勾选商品|
|POST /api/orders|结算页点提交订单|检查库存和价格，保存快照，按商家拆单并扣库存|
|GET /api/orders|我的订单页|返回自己的订单列表和状态|
|GET   /api/orders/\{id\}|订单列表点查看详情|返回订单商品、金额、地址、物流和状态|
|PUT   /api/orders/\{id\}/cancel|待付款订单点取消|取消未付款订单并恢复库存|
|POST   /api/orders/\{id\}/pay|待付款订单点模拟支付|生成支付记录，订单变为待发货|
|PUT   /api/orders/\{id\}/confirm\-receipt|收到货后点确认收货|订单完成，开放评价入口|

## 5\.4 商家店铺、发货和评价

|**接口**|**谁在什么页面使用**|**系统做什么**|**后端负责人**|
|---|---|---|---|
|GET   /api/merchant/shop|商家店铺设置页|返回自己的店铺名称、Logo、公告|D|
|PUT   /api/merchant/shop|商家修改店铺资料后保存|更新自己的店铺资料|D|
|GET   /api/merchant/dashboard|商家后台首页|返回自己的订单数、销售额、待发货数量|E|
|GET   /api/merchant/orders|商家订单管理页|返回购买该商家商品的订单|E|
|GET   /api/merchant/orders/\{id\}|商家点订单详情|返回收件人、商品、数量和状态|E|
|PUT   /api/merchant/orders/\{id\}/ship|商家填物流单号后点发货|保存物流信息，订单变已发货|E|
|POST   /api/reviews|消费者在已完成订单点评价|验证购买/收货后创建评价|E|
|GET   /api/products/\{id\}/reviews|商品详情的评价区域|返回该商品评价供消费者参考|E|
|GET   /api/merchant/reviews|商家商品评价页|返回消费者给自己商品的评价|E|
|PUT   /api/merchant/reviews/\{id\}/reply|商家对某条评价点回复|保存商家的公开回复|E|

## 5\.5 退款、退货、申诉和仲裁（E）

|**接口**|**谁在什么页面使用**|**系统做什么**|
|---|---|---|
|POST   /api/refunds|消费者在订单详情点申请退款/退货|检查资格，创建待商家处理的退款申请|
|GET /api/refunds|消费者退款/售后页|返回自己的退款申请和处理进度|
|GET   /api/refunds/\{id\}|消费者、相关商家或管理员点退款详情|返回原因、金额、状态、物流和申诉信息|
|PUT   /api/refunds/\{id\}/return\-logistics|商家同意后，消费者填寄回单号|保存退货物流，等待商家收货|
|PUT   /api/refunds/\{id\}/appeal|商家拒绝后，消费者点申请平台介入|把退款交给管理员仲裁|
|GET   /api/merchant/refunds|商家退款管理页|返回与该商家订单有关的退款|
|PUT   /api/merchant/refunds/\{id\}/audit|商家点同意或拒绝|更新退款状态；同意后用户可寄回|
|PUT   /api/merchant/refunds/\{id\}/confirm\-return|商家收到退货后确认|完成模拟退款，结束售后|
|GET   /api/admin/refunds|管理员待仲裁退款页|返回需要平台介入的退款|
|PUT   /api/admin/refunds/\{id\}/arbitrate|管理员点支持用户或驳回|保存最终仲裁结果和操作记录|

## 5\.6 平台审核、运营和日志

|**接口**|**谁在什么页面使用**|**系统做什么**|**后端负责人**|
|---|---|---|---|
|GET   /api/admin/dashboard|管理员后台首页|返回用户、商家、订单、销售额统计|E|
|GET   /api/admin/users|用户管理页|返回平台用户列表|D|
|PUT   /api/admin/users/\{id\}/status|管理员点禁用/启用用户|修改用户账号状态|D|
|GET   /api/admin/merchants/pending|商家入驻审核页|返回待审核商家|D|
|GET   /api/admin/merchants/\{id\}|管理员在商家申请列表点查看详情|返回店铺申请资料、证明图片和审核状态，供审核前查看|D|
|PUT   /api/admin/merchants/\{id\}/audit|管理员点通过/驳回商家|修改商家审核状态；通过后可开店|D|
|GET   /api/admin/logs|管理员操作日志页|返回审核和仲裁等关键操作记录|E|
|GET /api/banners|消费者首页|返回已启用的首页轮播图|E|
|GET   /api/admin/banners|管理员轮播图管理页|返回所有轮播图|E|
|POST   /api/admin/banners|管理员新增轮播图后提交|新建一张轮播图配置|E|
|PUT   /api/admin/banners/\{id\}|管理员编辑轮播图后保存|修改轮播图内容、排序或启用状态|E|
|DELETE   /api/admin/banners/\{id\}|管理员点删除轮播图|移除该轮播图|E|

# GitHub 协作规则

使用一个 GitHub 仓库。每个人从 main 拉自己的功能分支，完成后提交 Pull Request，由至少一位组员检查后合并。不要直接向 main 推送。

```Markdown
main
├─ feature/user-web             # A
├─ feature/user-mobile          # B
├─ feature/merchant-admin-web   # C
├─ feature/auth-catalog         # D
└─ feature/trade-platform       # E
```

接口或数据库字段要变更时，D/E 先更新接口说明；A/B/C 确认页面需要的数据。临时 API 函数和真实后端的接口名称、字段、成功/失败格式必须一致。真实后端完成后，A/B/C 只替换临时函数内部的假数据为真实请求；如果页面不需要重写，就说明前后端分离成功。

每个 Pull Request 写清楚：完成了什么、影响了哪些页面/接口、怎样测试。表结构变更必须更新 schema\.sql；答辩数据变更必须更新 data\.sql。



# 附录sql：

```SQL
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

```

```SQL
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

```

