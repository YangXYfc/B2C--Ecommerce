# Five-Role Test Reports Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Execute evidence-backed tests for roles A–E and deliver five standalone Markdown test reports under `docs/testing/`.

**Architecture:** Use the existing automated test suites and production builds as the baseline, then add live HTTP/UI verification for the role-specific critical paths that existing suites do not cover. Each report uses one shared structure but contains only the scope, cases, evidence, limitations, and conclusion applicable to its role.

**Tech Stack:** Spring Boot/Maven/JUnit, MySQL, Vue 3/Vite, uni-app, Node test runner, Vitest, Playwright, PowerShell, Markdown.

**Spec:** `悦选生活多商家电商平台：任务分工与接口说明.md` and the user-approved report format in this task.

## Global Constraints

- Create exactly five final role reports; do not add a sixth summary report.
- Record only tests actually executed; use `阻塞` or `未测试` when execution evidence is unavailable.
- Keep database names and runtime configuration unchanged.
- Use the shared statuses `通过`, `失败`, `阻塞`, `未测试`, and `不适用`.
- Preserve existing application behavior; this task writes test documentation and evidence, not new business features.
- Do not commit, merge, or push until the user selects a branch integration option.

---

### Task 1: Map the Five Roles to Executable Evidence

**Files:**
- Read: `悦选生活多商家电商平台：任务分工与接口说明.md`
- Read: `backend/src/test/java/**/*.java`
- Read: `frontend-user-web/tests/*.test.js`
- Read: `frontend-user-mobile/tests/**/*.js`
- Read: `frontend-user-mobile/tests/e2e/*.spec.js`
- Create: `docs/testing/A-消费者Web测试报告.md`
- Create: `docs/testing/B-消费者移动端测试报告.md`
- Create: `docs/testing/C-商家与平台管理前端测试报告.md`
- Create: `docs/testing/D-账号商品商家后端测试报告.md`
- Create: `docs/testing/E-交易售后平台后端测试报告.md`

**Interfaces:**
- Consumes: A–E responsibility and API tables from the role document.
- Produces: A role-to-suite matrix used by all later tasks.

- [x] **Step 1: Inventory existing automated tests**

Run:

```powershell
Get-ChildItem backend/src/test,frontend-user-web/tests,frontend-user-mobile/tests -Recurse -File
```

Expected: JUnit, Node, Vitest, and Playwright test files are listed.

- [x] **Step 2: Map suites to roles**

Classify consumer Web evidence as A, mobile evidence as B, merchant/admin UI evidence as C, account/catalog/merchant backend evidence as D, and cart/order/refund/operations backend evidence as E.

- [x] **Step 3: Create five report skeletons**

Each file must contain: test objective, scope, exclusions, environment, accounts/data, functional cases, API integration, negative/boundary cases, cross-role flow, automated results, build results, known issues, summary matrix, and conclusion.

### Task 2: Execute Baseline Automated Tests and Builds

**Files:**
- Evidence source: `backend/target/surefire-reports/`
- Evidence source: terminal output from four frontend builds and three automated suites.

**Interfaces:**
- Consumes: existing test/build commands.
- Produces: exact pass/fail counts and build results for all five reports.

- [x] **Step 1: Run the backend suite**

Run: `backend\mvnw.cmd test`

Expected: Maven reports `BUILD SUCCESS` with zero failures and errors.

- [x] **Step 2: Run A-role unit tests**

Run in `frontend-user-web`: `npm test`

Expected: all Node test cases pass.

- [x] **Step 3: Run B-role unit tests**

Run in `frontend-user-mobile`: `npm test -- --run`

Expected: all Vitest files and cases pass.

- [x] **Step 4: Build all browser clients**

Run `npm run build` in `frontend-user-web`, `frontend-merchant`, and `frontend-admin`; run `npm run build:h5` and `npm run build:mp-weixin` in `frontend-user-mobile`.

Expected: all five builds complete successfully; non-fatal bundle-size warnings are recorded as warnings.

### Task 3: Execute Critical Live Cross-Role Flows

**Files:**
- Read: `.vscode/launch.json`
- Read: `frontend-user-mobile/playwright.config.js`
- Read: `frontend-user-mobile/tests/e2e/*.spec.js`
- Evidence source: live backend and frontend responses.

**Interfaces:**
- Consumes: backend on port 8080 and frontends on ports 5173–5176.
- Produces: live UI/API evidence for A, B, and C plus end-to-end D/E integration evidence.

- [x] **Step 1: Start the backend and required clients**

Start Spring Boot, consumer Web, mobile H5, merchant center, and platform management with the repository's documented commands and fixed development ports.

- [x] **Step 2: Run existing Playwright flows**

Run in `frontend-user-mobile`: `npm run test:e2e`

Expected: the consumer shopping flow and merchant image upload flow both pass.

- [x] **Step 3: Verify service entry pages and branding**

Request ports 5173–5176 and confirm successful HTML responses containing the expected application entry points; verify the rendered role-facing brand is `悦选生活` through browser tests or built HTML/JavaScript evidence.

- [x] **Step 4: Verify critical API role boundaries**

Use the backend integration test evidence for USER, MERCHANT, and ADMIN route access and record expected 2xx/4xx behavior in D/E reports.

- [x] **Step 5: Stop temporary services**

Terminate only the service processes started by this task and confirm their sessions exit.

### Task 4: Write the Five Evidence-Backed Reports

**Files:**
- Create: `docs/testing/A-消费者Web测试报告.md`
- Create: `docs/testing/B-消费者移动端测试报告.md`
- Create: `docs/testing/C-商家与平台管理前端测试报告.md`
- Create: `docs/testing/D-账号商品商家后端测试报告.md`
- Create: `docs/testing/E-交易售后平台后端测试报告.md`

**Interfaces:**
- Consumes: results from Tasks 1–3.
- Produces: five standalone deliverable reports.

- [x] **Step 1: Write A and B reports**

Include page/function coverage, real API alignment, build targets, consumer critical flow, offline media verification, and mobile-platform limitations.

- [x] **Step 2: Write C report**

Separate merchant-center and platform-management cases inside one role report; include image upload, role login boundaries, type checking, and builds.

- [x] **Step 3: Write D and E reports**

List JUnit class coverage by domain, database/profile used, API/security evidence, negative cases, and exact automated totals. Avoid claiming manual cases that were not executed.

- [x] **Step 4: Calculate every summary table**

For each report, ensure `用例数 = 通过 + 失败 + 阻塞 + 未测试`, and calculate the pass rate as `通过 / 已执行用例 × 100%`, excluding `未测试` and `不适用`.

### Task 5: Verify Documentation Integrity

**Files:**
- Verify: `docs/testing/*.md`

**Interfaces:**
- Consumes: five completed reports.
- Produces: verified final documentation with no placeholders or unsupported claims.

- [x] **Step 1: Verify the file count and required headings**

Run a PowerShell check that exactly five Markdown files exist and each includes all required sections and a conclusion.

- [x] **Step 2: Scan for placeholders and inconsistent totals**

Search for `TODO`, `TBD`, blank status cells, and mismatched summary arithmetic; correct every finding.

- [x] **Step 3: Run Git whitespace validation**

Run: `git diff --check`

Expected: no whitespace errors.

- [x] **Step 4: Review final workspace changes**

Run: `git status --short`

Expected: the five test reports and this plan are present alongside the previously approved brand-renaming changes; no runtime test artifacts or uploaded test images remain.
