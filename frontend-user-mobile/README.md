# 悦选消费者端（B 角色）

基于 uni-app、Vue 3 与 Pinia 的消费者移动端代码框架，同时支持 Android App 与微信小程序。当前版本覆盖开发文档中 B 角色的完整页面和接口骨架，默认使用本地 Mock 数据，可在 E 角色后端接口稳定后切换到真实 API。

## 已包含的业务范围

- 首页、分类、商品搜索/筛选/排序、商品详情与 SKU 选择
- 购物车、确认订单、订单列表、订单详情及基础操作入口
- 登录、注册、个人资料、密码修改、商家入驻入口
- 地址新增/编辑/默认地址管理
- 商品评价、退款申请、售后列表、退货物流与平台申诉入口
- Mock/API 双数据源、接口适配层、Pinia 用户与购物车状态

本模块刻意不实现 JWT 权限、真实支付、库存扣减、订单/退款状态机和真实 SQL 映射；这些由对应后端角色完成。页面通过 `src/api/index.js` 统一访问数据，后续接入时不需要改页面结构。

## 环境要求

- Node.js 20 或更高版本
- npm 10 或更高版本
- 微信开发者工具（运行微信小程序）
- HBuilderX（打包 Android App；H5 调试不需要）

## 安装与本地运行

```bash
cd frontend-user-mobile
npm install

# H5 开发预览
npm run dev:h5

# H5 生产构建
npm run build:h5

# 微信小程序开发构建
npm run dev:mp-weixin

# 微信小程序生产构建
npm run build:mp-weixin

# 单元测试
npm test -- --run
```

微信小程序构建完成后，在微信开发者工具中导入 `frontend-user-mobile/dist/build/mp-weixin`。

Android App 推荐使用 HBuilderX 打开 `frontend-user-mobile`，在“运行/发行”菜单中选择 Android 真机或云打包。首次运行需在 `src/manifest.json` 中配置自己的 DCloud AppID 与 Android 包名。

## 数据源切换

开发环境 `.env.development` 默认：

```dotenv
VITE_DATA_MODE=mock
VITE_API_BASE_URL=http://localhost:8080
```

联调时将 `VITE_DATA_MODE` 改为 `api`，并把 `VITE_API_BASE_URL` 指向实际后端。真实请求当前按开发文档中的 `/api/auth`、`/api/products`、`/api/cart`、`/api/orders`、`/api/refunds` 等路径封装在 `src/api/index.js`。

Mock 数据保存在本地存储命名空间 `yuexuan` 中。演示账号为 `user1`，密码为 `123456`。清空浏览器/小程序本地存储可恢复初始演示数据。

## 目录结构

```text
frontend-user-mobile/
├─ src/api/          # 真实 API 适配、Mock 服务与数据规范化
├─ src/components/   # 可复用商品、订单、状态、数量组件
├─ src/pages/        # B 角色 20 个页面路由
├─ src/static/       # 品牌与首页视觉资产
├─ src/stores/       # Pinia 状态
├─ src/utils/        # 价格、订单、退款、校验等纯函数
└─ tests/            # Vitest 单元测试
```

## 构建输出

- H5：`dist/build/h5`
- 微信小程序：`dist/build/mp-weixin`

生产环境文件中的 `https://api.example.com` 只是占位地址，部署前必须替换为实际 HTTPS API 域名；微信小程序还需要在后台配置 request/uploadFile 合法域名。
