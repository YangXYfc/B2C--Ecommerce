# B Role Consumer Mobile Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a complete uni-app consumer shopping application for role B that runs as H5, WeChat Mini Program, and Android App source.

**Architecture:** Add an isolated `frontend-user-mobile` package using Vue 3, Pinia, and uni-app. Pages consume a single business API facade whose mock and real implementations return the same data shapes; shared stores own session and cart counters while page-specific data stays local.

**Tech Stack:** uni-app CLI, Vue 3, Pinia, Vite, Vitest, JavaScript, uni-app native components

**Spec:** `docs/superpowers/specs/2026-08-19-b-role-user-mobile-design.md`

## Global Constraints

- The app name is `悦选`; visible branding must not say `JD商城`.
- Use one uni-app codebase for H5, WeChat Mini Program, and Android App packaging.
- Reuse the existing D/E API contract; do not add backend endpoints or database changes.
- Default to mock mode and support real API mode through environment configuration.
- Use uni-app native components and project-owned components; do not add a large mobile UI framework.
- Preserve the selected concept at `docs/design/b-role-user-mobile-home-concept.png`.
- Implement tests before production logic and verify both H5 and WeChat builds.

---

### Task 1: Project Runtime and Tested Domain Utilities

**Files:**
- Create: `frontend-user-mobile/package.json`
- Create: `frontend-user-mobile/vite.config.js`
- Create: `frontend-user-mobile/src/main.js`
- Create: `frontend-user-mobile/src/App.vue`
- Create: `frontend-user-mobile/src/manifest.json`
- Create: `frontend-user-mobile/src/pages.json`
- Create: `frontend-user-mobile/src/styles/tokens.scss`
- Create: `frontend-user-mobile/src/styles/global.scss`
- Create: `frontend-user-mobile/src/utils/format.js`
- Create: `frontend-user-mobile/src/utils/order.js`
- Test: `frontend-user-mobile/tests/format.test.js`
- Test: `frontend-user-mobile/tests/order.test.js`

**Interfaces:**
- Produces: `formatPrice(value)`, `formatSales(value)`, `getOrderStatus(status)`, `getOrderActions(status)`

- [ ] Write tests asserting money formatting, sales-count formatting, and permitted actions for order states 0-5.
- [ ] Run `npm test -- --run` and confirm failure because utility modules do not exist.
- [ ] Add the minimal runtime and utility implementations.
- [ ] Run the focused tests and confirm they pass.
- [ ] Commit project runtime and utilities.

### Task 2: Unified API Facade, Mock Persistence, and Stores

**Files:**
- Create: `frontend-user-mobile/src/api/request.js`
- Create: `frontend-user-mobile/src/api/index.js`
- Create: `frontend-user-mobile/src/api/mock/data.js`
- Create: `frontend-user-mobile/src/api/mock/service.js`
- Create: `frontend-user-mobile/src/api/normalizers.js`
- Create: `frontend-user-mobile/src/stores/user.js`
- Create: `frontend-user-mobile/src/stores/cart.js`
- Create: `frontend-user-mobile/src/utils/storage.js`
- Test: `frontend-user-mobile/tests/normalizers.test.js`
- Test: `frontend-user-mobile/tests/mock-service.test.js`

**Interfaces:**
- Produces: `api` facade for auth, banners, categories, products, cart, addresses, orders, reviews, refunds, and profiles.
- Produces: `useUserStore()` and `useCartStore()`.

- [ ] Write tests for response unwrapping, pagination normalization, cart quantity mutation, login, order creation, payment, and refund creation.
- [ ] Run tests and verify they fail because the facade and mock service do not exist.
- [ ] Implement storage-backed mock data and the real request adapter.
- [ ] Implement stores using only the facade.
- [ ] Run all data-layer tests and confirm they pass.
- [ ] Commit API and state foundation.

### Task 3: App Shell and Concept-Faithful Home Screen

**Files:**
- Create: `frontend-user-mobile/src/components/BrandHeader.vue`
- Create: `frontend-user-mobile/src/components/ProductCard.vue`
- Create: `frontend-user-mobile/src/components/LoadingState.vue`
- Create: `frontend-user-mobile/src/components/EmptyState.vue`
- Create: `frontend-user-mobile/src/pages/home/index.vue`
- Create: `frontend-user-mobile/src/static/icons/*.png`
- Create: `frontend-user-mobile/src/static/images/home-hero.jpg`

**Interfaces:**
- Consumes: `api.getBanners()`, `api.getCategories()`, `api.getProducts()`.
- Produces: home navigation to search, category, and product detail.

- [ ] Add component tests for product-card price/click payload and home-section mapping.
- [ ] Run tests and verify the missing components fail.
- [ ] Implement design tokens, branded header, scene carousel, category rail, selected products, and four-item tab bar configuration.
- [ ] Run H5 preview and capture the 390×844 home viewport.
- [ ] Compare against the selected concept and fix first-viewport drift.
- [ ] Commit the app shell and home screen.

### Task 4: Catalog and Product Detail Flow

**Files:**
- Create: `frontend-user-mobile/src/pages/category/index.vue`
- Create: `frontend-user-mobile/src/pages/product/list.vue`
- Create: `frontend-user-mobile/src/pages/product/detail.vue`
- Create: `frontend-user-mobile/src/components/FilterBar.vue`
- Create: `frontend-user-mobile/src/components/SkuSelector.vue`
- Test: `frontend-user-mobile/tests/catalog.test.js`

**Interfaces:**
- Consumes: category, product-list, product-detail, SKU, and review APIs.
- Produces: selected SKU and add-to-cart/checkout navigation.

- [ ] Write tests for keyword filtering, price sorting, SKU selection, and quantity bounds.
- [ ] Run tests and verify expected missing-module failures.
- [ ] Implement category browsing, search/sort list, detail gallery, SKU selection, reviews, add-to-cart, and buy-now actions.
- [ ] Run catalog tests and verify mobile navigation manually.
- [ ] Commit catalog flow.

### Task 5: Cart and Checkout Flow

**Files:**
- Create: `frontend-user-mobile/src/pages/cart/index.vue`
- Create: `frontend-user-mobile/src/pages/checkout/index.vue`
- Create: `frontend-user-mobile/src/components/QuantityStepper.vue`
- Create: `frontend-user-mobile/src/components/AddressSummary.vue`
- Create: `frontend-user-mobile/src/utils/cart.js`
- Test: `frontend-user-mobile/tests/cart.test.js`

**Interfaces:**
- Produces: `calculateSelectedTotal(items)` and `groupItemsByMerchant(items)`.
- Consumes: cart, address, and create-order APIs.

- [ ] Write tests for selected totals, merchant grouping, quantity validation, and checkout guards.
- [ ] Run tests and confirm they fail before implementation.
- [ ] Implement cart selection/update/delete and checkout address/order submission.
- [ ] Run tests and manually verify cart-to-order flow.
- [ ] Commit cart and checkout.

### Task 6: Orders, Reviews, and Refunds

**Files:**
- Create: `frontend-user-mobile/src/pages/order/list.vue`
- Create: `frontend-user-mobile/src/pages/order/detail.vue`
- Create: `frontend-user-mobile/src/pages/review/create.vue`
- Create: `frontend-user-mobile/src/pages/refund/list.vue`
- Create: `frontend-user-mobile/src/pages/refund/detail.vue`
- Create: `frontend-user-mobile/src/pages/refund/create.vue`
- Create: `frontend-user-mobile/src/components/OrderStatus.vue`
- Create: `frontend-user-mobile/src/utils/refund.js`
- Test: `frontend-user-mobile/tests/refund.test.js`

**Interfaces:**
- Consumes: order list/detail/pay/cancel/confirm, review create, and refund lifecycle APIs.
- Produces: `getRefundStatus(status)` and `getRefundActions(status)`.

- [ ] Write tests for order and refund action availability.
- [ ] Run tests and verify expected failure.
- [ ] Implement order list/detail actions, review form, refund creation/list/detail, logistics, and appeal forms.
- [ ] Run tests and manually verify the paid-order-to-refund path.
- [ ] Commit order and aftersales flow.

### Task 7: Authentication, Profile, and Address Management

**Files:**
- Create: `frontend-user-mobile/src/pages/auth/login.vue`
- Create: `frontend-user-mobile/src/pages/auth/register.vue`
- Create: `frontend-user-mobile/src/pages/profile/index.vue`
- Create: `frontend-user-mobile/src/pages/profile/edit.vue`
- Create: `frontend-user-mobile/src/pages/profile/password.vue`
- Create: `frontend-user-mobile/src/pages/profile/merchant-apply.vue`
- Create: `frontend-user-mobile/src/pages/address/list.vue`
- Create: `frontend-user-mobile/src/pages/address/edit.vue`
- Create: `frontend-user-mobile/src/utils/validation.js`
- Test: `frontend-user-mobile/tests/validation.test.js`

**Interfaces:**
- Consumes: auth, profile, password, merchant-apply, upload, and address APIs.
- Produces: `validateLogin`, `validateRegister`, and `validateAddress`.

- [ ] Write failing tests for account and address validation.
- [ ] Implement the minimal validation functions and make tests pass.
- [ ] Implement account, profile, merchant application, and address pages.
- [ ] Verify protected-route redirects and login restoration.
- [ ] Commit account and address flow.

### Task 8: Documentation, Platform Builds, and Design QA

**Files:**
- Create: `frontend-user-mobile/README.md`
- Create: `frontend-user-mobile/.env.development`
- Create: `frontend-user-mobile/.env.production`
- Create: `frontend-user-mobile/design-qa.md`
- Modify: `README.md`

**Interfaces:**
- Produces: reproducible H5, WeChat, and Android setup instructions.

- [ ] Document install, H5, WeChat DevTools, HBuilderX Android, mock/API switching, backend URL, demo account, and troubleshooting.
- [ ] Run `npm test -- --run` and confirm zero failures.
- [ ] Run `npm run build:h5` and confirm exit 0.
- [ ] Run `npm run build:mp-weixin` and confirm exit 0.
- [ ] Start H5, capture the home screen at 390×844, and exercise search → detail → cart → checkout.
- [ ] Open source and rendered images together, record fidelity findings, fix P0/P1/P2 issues, and save `design-qa.md` with `final result: passed`.
- [ ] Run `git diff --check`, review the requirement checklist, and commit the completed B-role application.
