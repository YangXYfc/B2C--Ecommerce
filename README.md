# B2C 多商家电商平台

这是一个课程项目，实现平台统一监管、多个商家入驻和消费者购买的 B2C 商城。仓库包含 Spring Boot 后端、消费者 Web、消费者移动端（Android/微信小程序代码）、商家端和平台管理端。

## 快速开始

已经安装 Java、Node.js 和 MySQL，并且本机已有 `jd_ecommerce` 时，按下面的顺序操作：

1. 启动 MySQL 服务。
2. 用 VS Code 打开仓库根目录 `B2C-Ecommerce`。
3. 首次运行时选择 `终端` → `运行任务` → `安装全部前端依赖`。
4. 点击左侧“运行和调试”，选择 `全栈：后端 + 全部 Web 前端`，再点击绿色三角按钮。
5. 等后端终端显示 8080 启动成功后，打开 5173–5176 对应的页面；如果页面早于后端打开，刷新一次即可。

这个组合会启动后端和四个浏览器前端，不包含微信小程序编译。微信小程序需要单独运行 `启动微信小程序编译`。

## 项目组成

| 目录 | 用途 | VS Code 启动后的地址 |
| --- | --- | --- |
| `backend/` | Spring Boot + MyBatis 后端 | `http://localhost:8080` |
| `frontend-user-web/` | 消费者 Web | `http://localhost:5173` |
| `frontend-user-mobile/` | uni-app 消费者移动端，H5 用于预览 Android/小程序页面 | `http://localhost:5174` |
| `frontend-merchant/` | 商家工作台 | `http://localhost:5175` |
| `frontend-admin/` | 平台管理端 | `http://localhost:5176` |
| `database/` | MySQL 建表和种子数据 | 数据库 `jd_ecommerce` |

所有前端开发环境默认连接 `http://localhost:8080` 的真实后端，不需要另外修改接口地址。

## 一、运行环境

建议先安装：

- Windows 10/11
- VS Code
- Java 25，并正确配置 `JAVA_HOME`
- Node.js 20 或更高版本、npm
- MySQL 8.0 或更高版本
- 微信开发者工具（仅运行微信小程序时需要）
- HBuilderX（需要打包 Android App 时使用；平时可先用 H5 预览）

仓库自带 Maven Wrapper，不需要单独安装 Maven。VS Code 的启动按钮通过集成终端执行命令，因此只要 `java`、`node` 和 `npm` 可用就能启动；Java、Vue 和 ESLint 扩展主要用于代码提示和调试，建议安装但不是启动按钮的硬性依赖。

可以在 PowerShell 中检查环境：

```powershell
java -version
node -v
npm -v
mysql --version
```

## 二、数据库配置

开发环境使用以下 MySQL 配置：

| 配置项 | 默认值 |
| --- | --- |
| 地址 | `localhost:3306` |
| 数据库 | `jd_ecommerce` |
| 用户名 | `root` |
| 密码 | `123456` |

默认激活的 Spring Profile 是 `dev`，入口配置位于 `backend/src/main/resources/application.yml`，数据库配置位于 `backend/src/main/resources/application-dev.yml`。如果你的 MySQL 密码不是 `123456`，请先在 Windows 中设置用户环境变量，然后重新打开 VS Code：

```powershell
[Environment]::SetEnvironmentVariable('DB_PASSWORD', '你的MySQL密码', 'User')
```

也可以只为当前 PowerShell 窗口设置；这种方式需要从该窗口执行 `code .` 打开 VS Code，环境变量才能被启动任务继承：

```powershell
$env:DB_PASSWORD = '你的MySQL密码'
code .
```

如果主机、端口、用户名或数据库名不同，请直接修改 `application-dev.yml` 中的 datasource 配置。

### 首次初始化数据库

如果本机还没有 `jd_ecommerce`，使用具有建库和建表权限的账号登录 MySQL Workbench、Navicat 或 MySQL 命令行，按顺序执行：

1. `database/schema.sql`
2. `database/data.sql`

`schema.sql` 会创建 `jd_ecommerce`，同时删除并重建其中的项目表。**只要同名数据库中已有需要保留的数据，就不要执行这个脚本。**

MySQL 命令行可以在登录后使用 `SOURCE`，路径请替换成自己的仓库绝对路径：

```sql
SOURCE E:/你的路径/B2C-Ecommerce/database/schema.sql;
SOURCE E:/你的路径/B2C-Ecommerce/database/data.sql;
```

检查数据库：

```sql
SHOW DATABASES;
USE jd_ecommerce;
SHOW TABLES;
```

后端 API 集成测试配置使用独立数据库 `jd_ecommerce_test`。测试连接带有自动建库参数，但 MySQL 账号仍需拥有建库权限。

## 三、第一次运行：安装前端依赖

在 VS Code 顶部菜单选择：

`终端` → `运行任务` → `安装全部前端依赖`

VS Code 会依次在四个前端目录执行 `npm install`。通常只需在首次拉取项目或依赖变化后执行一次。

## 四、在 VS Code 中一键启动

必须用 VS Code 打开仓库根目录 `B2C-Ecommerce`，不要只打开某个前端子目录，否则看不到仓库中的启动按钮。

### 方法 A：终端 → 运行任务

点击 VS Code 顶部菜单：

`终端` → `运行任务`

可以选择：

| 任务 | 作用 |
| --- | --- |
| `启动后端` | 启动 Spring Boot，端口 8080 |
| `启动消费者 Web` | 启动消费者网页，端口 5173 |
| `启动消费者移动端 H5` | 启动 Android/微信小程序页面的 H5 预览，端口 5174 |
| `启动微信小程序编译` | 监听并编译微信小程序代码 |
| `启动商家端` | 启动商家工作台，端口 5175 |
| `启动管理端` | 启动平台管理端，端口 5176 |
| `一键启动全部 Web 服务` | 同时启动后端和四个可在浏览器查看的前端；不包含微信小程序编译 |
| `运行后端测试` | 执行 Maven 后端测试 |
| `构建全部前端` | 构建消费者 Web、H5、小程序、商家端和管理端 |

每个服务会使用独立的 VS Code 终端。停止某个服务时，切换到对应终端并按 `Ctrl+C`。

### 方法 B：左侧“运行和调试”按钮

1. 点击 VS Code 左侧的“运行和调试”图标，快捷键为 `Ctrl+Shift+D`。
2. 在顶部下拉框选择需要启动的配置。
3. 点击绿色三角按钮，或按 `F5`。

可单独选择后端、消费者 Web、消费者移动端 H5、微信小程序编译、商家端或管理端，也可以选择：

- `全栈：后端 + 消费者移动端`
- `全栈：后端 + 全部 Web 前端`

停止组合配置时，点击调试工具栏的红色停止按钮，相关终端会一起停止。

## 五、微信小程序和 Android App

### 微信小程序

在 VS Code 运行 `启动微信小程序编译` 后，用微信开发者工具导入：

```text
frontend-user-mobile/dist/dev/mp-weixin
```

如果目录尚未出现，等待 VS Code 终端显示编译完成。

开发者工具内可以使用测试 AppID。若本地调试被请求域名校验拦截，可在开发者工具项目设置中仅对本地开发关闭合法域名校验。真机上的 `localhost` 指向手机自身，无法访问电脑后端；真机联调时需要把 `frontend-user-mobile/.env.development` 中的 `VITE_API_BASE_URL` 改成电脑的局域网 IP，例如 `http://192.168.1.10:8080`，然后停止并重新运行小程序编译任务。手机和电脑必须处于同一网络，并且 Windows 防火墙需要允许访问 8080 端口。

局域网 IP 加 HTTP 仅适合课程项目的本地开发。微信真机预览或正式发布仍可能受到 HTTPS 和合法请求域名限制；遇到微信侧拦截时，应继续使用开发者工具联调，或配置可访问的 HTTPS 后端和微信公众平台合法域名。

### Android App

`frontend-user-mobile` 是 uni-app 工程。日常开发可运行 `启动消费者移动端 H5` 在浏览器预览；需要生成 Android 安装包时，用 HBuilderX 打开 `frontend-user-mobile`，再选择“发行 → 原生 App 云打包”。

## 六、命令行启动方式

不使用 VS Code 按钮时，可分别打开 PowerShell 执行以下命令。

启动后端：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

启动消费者 Web：

```powershell
cd frontend-user-web
npm install
npm run dev -- --port 5173 --strictPort
```

启动消费者移动端 H5：

```powershell
cd frontend-user-mobile
npm install
npm run dev:h5 -- --port 5174 --strictPort
```

启动微信小程序编译：

```powershell
cd frontend-user-mobile
npm run dev:mp-weixin
```

启动商家端：

```powershell
cd frontend-merchant
npm install
npm run dev -- --port 5175 --strictPort
```

启动管理端：

```powershell
cd frontend-admin
npm install
npm run dev -- --port 5176 --strictPort
```

## 七、演示账号

所有演示账号的密码均为 `123456`。

| 账号 | 角色 | 对应前端 |
| --- | --- | --- |
| `user1`、`user2`、`user3` | 消费者 | 消费者 Web、移动端 H5、App、小程序 |
| `merchant1`、`merchant2` | 商家 | 商家端 |
| `merchant3` | 待审核商家 | 商家端 |
| `admin` | 平台管理员 | 管理端 |

## 八、测试与构建

在 VS Code 中可运行 `运行后端测试` 或 `构建全部前端`。命令行方式如下：

```powershell
# 后端测试
cd backend
.\mvnw.cmd test

# 消费者移动端单元测试
cd ..\frontend-user-mobile
npm test

# 消费者移动端 Playwright 测试（需要先启动后端和 H5）
$env:E2E_BASE_URL = 'http://127.0.0.1:5174'
npm run test:e2e
```

检查 VS Code 配置和 README 是否完整：

```powershell
.\scripts\verify-vscode-setup.ps1
```

## 九、常见问题

### 后端提示数据库连接失败

确认 MySQL 服务已启动、`jd_ecommerce` 已存在，并检查 `DB_PASSWORD`。不同安装方式的服务名可能不同，Windows 可执行：

```powershell
Get-Service | Where-Object { $_.Name -match 'mysql' -or $_.DisplayName -match 'mysql' }
```

### 前端提示 `npm` 不是命令

安装 Node.js 后完全退出并重新打开 VS Code，再执行 `node -v` 和 `npm -v`。

### 前端提示端口被占用

VS Code 启动配置启用了 `--strictPort`，发现端口占用时会直接报错。关闭之前启动的同名服务；如需换前端端口，应同时修改 `.vscode/tasks.json`、`.vscode/launch.json` 和 README。若修改后端 8080，还必须同步修改三个 Web 前端 `vite.config.*` 中的代理目标，以及 `frontend-user-mobile/.env.development` 中的 `VITE_API_BASE_URL`。

### 页面打开但接口请求失败

先确认后端终端已经显示端口 8080 启动成功，再访问：

```text
http://localhost:8080/api/categories
```

能看到 JSON 数据说明后端和数据库连接正常。

### VS Code 中看不到启动任务

确认当前打开的是仓库根目录，并检查 `.vscode/tasks.json` 和 `.vscode/launch.json` 是否存在。然后执行“开发人员：重新加载窗口”。

## 十、相关文档

- `B2C 多商家电商平台：任务分工与接口说明.md`：角色分工与接口契约
- `frontend-user-mobile/README.md`：消费者移动端详细说明
- `docs/e-role-backend-skeleton.md`：后端骨架交接说明
- `database/schema.sql`、`database/data.sql`：权威 MySQL 脚本
