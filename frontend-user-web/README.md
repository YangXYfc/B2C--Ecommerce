# 消费者 H5 网页端 (frontend-user-web)

成员 A 负责的消费者端 H5 商城，基于 Vue 3 + Vite + Pinia + Element Plus 构建。

## 功能清单

- 登录 / 注册 / 商家入驻申请
- 首页（轮播图、分类、推荐商品）
- 商品搜索 / 分类筛选 / 排序 / 分页
- 商品详情（规格选择、库存、评价、加购）
- 购物车（改数量、勾选、删除、结算）
- 收货地址管理
- 下单结算
- 订单列表 / 详情（模拟支付、取消、确认收货）
- 商品评价
- 退款 / 售后（申请退款、填写退货物流、平台申诉）
- 个人中心（资料修改、密码修改、头像上传）

## 快速启动

```bash
cd frontend-user-web
npm install
npm run dev
```

浏览器访问 http://localhost:5173

## 测试账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| user1  | 123456 | 普通消费者（张三），有购物车和订单数据 |
| user2  | 123456 | 普通消费者（李四） |
| user3  | 123456 | 普通消费者（王五） |

## 临时 API 与后端联调

当前使用 **临时 Mock API**（`src/api/mock/`），数据基于项目文档附录中的演示数据，保存在浏览器 localStorage 中。

后端（D/E）接口完成后，只需修改 `src/api/index.js`：

```javascript
export const USE_MOCK = false  // 改为 false 即可切换真实后端
```

Vite 已配置代理，真实请求会转发到 `http://localhost:8080/api`。

## 目录结构

```
src/
├── api/           # API 入口 + Mock 临时函数
├── components/    # 公共组件（Header、Footer、ProductCard）
├── layouts/       # 页面布局
├── router/        # 路由配置
├── stores/        # Pinia 状态（用户、购物车）
├── utils/         # 工具函数
└── views/         # 页面
```

## 核心购物流程演示

1. 使用 `user1 / 123456` 登录
2. 首页浏览商品 → 进入商品详情 → 选择规格 → 加入购物车
3. 购物车勾选商品 → 去结算 → 选择地址 → 提交订单
4. 我的订单 → 模拟支付 → 等待商家发货（C 的商家后台操作）
5. 确认收货 → 发表评价
6. 或在订单详情申请退款 → 查看退款进度
