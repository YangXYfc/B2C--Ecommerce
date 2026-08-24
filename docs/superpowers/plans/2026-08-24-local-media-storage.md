# Local Media Storage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make every seeded image available offline from the backend and make new product/store/banner images upload to local disk with portable relative paths.

**Architecture:** Versioned seed images live under Spring Boot static resources at `/media/**`; runtime uploads live outside the classpath under `backend/upload/` at `/upload/**`. MySQL stores only relative media paths, Web frontends proxy both prefixes, and the uni-app client resolves relative paths against `VITE_API_BASE_URL` for H5/App/Weixin portability.

**Tech Stack:** Spring Boot 3.5, Java 25, MyBatis, MySQL 8, Vue 3, Element Plus, uni-app, Vite, PowerShell verification, Playwright.

**Spec:** `docs/superpowers/specs/2026-08-24-local-media-storage-design.md`

## Global Constraints

- Preserve the existing red consumer-marketplace visual style.
- Seed assets must work without external network access after checkout.
- Runtime uploads accept only JPG/JPEG/PNG/WebP and at most 5 MB.
- Database values are `/media/...` or `/upload/...` paths, never environment-specific absolute URLs.
- `backend/upload/` is runtime state and must not enter Git.
- Do not introduce MinIO, cloud object storage, CDN, image cleanup, or a server-side transcoding pipeline.

---

### Task 1: Seed media inventory and offline assets

**Files:**
- Create: `scripts/verify-local-media.ps1`
- Create: `scripts/download-seed-media.ps1`
- Create: `backend/src/main/resources/static/media/SOURCES.md`
- Create: `backend/src/main/resources/static/media/{products,banners,shops,categories,common}/*.{jpg,webp}`

**Interfaces:**
- Consumes: existing Unsplash URLs from `database/data.sql` plus image-search results with reusable source URLs.
- Produces: stable `/media/<category>/<filename>` paths and a source manifest used by SQL and frontends.

- [ ] **Step 1: Write the failing media verification script**

Create assertions that require directories, `SOURCES.md`, the placeholder, nine product mains, four banners, three shop images and four category images. Also scan every required file for length greater than 1 KB.

```powershell
$required = @(
  'common/placeholder.webp',
  'products/product-01-phone.jpg',
  'products/product-09-foldable.jpg',
  'banners/banner-01-sale.jpg',
  'banners/banner-04-appliance.jpg',
  'shops/shop-01-digital.jpg',
  'shops/shop-03-pending.jpg',
  'categories/category-01-digital.jpg',
  'categories/category-04-food.jpg'
)
```

- [ ] **Step 2: Run the script and verify RED**

Run: `powershell -ExecutionPolicy Bypass -File scripts/verify-local-media.ps1`

Expected: FAIL naming the first missing file under `backend/src/main/resources/static/media`.

- [ ] **Step 3: Find and download the assets**

Use image search to find product-appropriate sources when the current SQL URL is invalid. Prefer the current valid Unsplash photo IDs for product and banner continuity. `download-seed-media.ps1` must use an explicit URL-to-file map, `Invoke-WebRequest`, and fail on non-2xx responses; no runtime code may invoke this script.

Record each file, source page/URL, subject and use in `SOURCES.md`. Reuse a product asset for related SKU/order snapshots instead of duplicating identical binaries.

- [ ] **Step 4: Verify GREEN**

Run: `powershell -ExecutionPolicy Bypass -File scripts/verify-local-media.ps1`

Expected: PASS and a count of all required non-empty assets.

- [ ] **Step 5: Commit**

```bash
git add scripts/verify-local-media.ps1 scripts/download-seed-media.ps1 backend/src/main/resources/static/media
git commit -m "feat: add offline seed media"
```

### Task 2: Portable seed paths and current-database migration

**Files:**
- Modify: `database/data.sql`
- Modify: `backend/src/main/resources/data-h2.sql`
- Create: `database/migrations/2026-08-24-local-media.sql`
- Modify: `scripts/verify-local-media.ps1`

**Interfaces:**
- Consumes: stable paths from Task 1.
- Produces: seed and migration data containing only `/media/...` for merchant logos, category icons, product main/sub images, SKU images, order snapshots, review images and banners.

- [ ] **Step 1: Add failing SQL assertions**

Extend `verify-local-media.ps1` to reject `images.unsplash.com` and `img.jd-demo.com` in both seed files and require all path values referenced by the migration to exist on disk.

```powershell
foreach ($seed in 'database/data.sql','backend/src/main/resources/data-h2.sql') {
  $text = Get-Content -Raw $seed
  Assert-True ($text -notmatch 'images\.unsplash\.com|img\.jd-demo\.com') "$seed 仍含远程图片"
}
```

- [ ] **Step 2: Run and verify RED**

Run: `powershell -ExecutionPolicy Bypass -File scripts/verify-local-media.ps1`

Expected: FAIL because the seed SQL still contains remote image hosts.

- [ ] **Step 3: Replace seed image values and add idempotent migration**

Use explicit updates keyed by stable seed IDs. The migration must contain statements of this form for every media-bearing table:

```sql
UPDATE product
SET main_image = '/media/products/product-01-phone.jpg',
    sub_images = '["/media/products/product-02-phone.jpg","/media/products/product-09-foldable.jpg"]'
WHERE id = 1;

UPDATE product_sku ps
JOIN product p ON p.id = ps.product_id
SET ps.sku_image = p.main_image
WHERE ps.id BETWEEN 1 AND 22;
```

Use matching `/media` values directly in both seed files. Do not alter non-image business data.

- [ ] **Step 4: Verify SQL and execute migration against development MySQL**

Run the verification script, then:

```powershell
mysql -uroot -p123456 jd_ecommerce -e "source database/migrations/2026-08-24-local-media.sql"
mysql -uroot -p123456 --batch --skip-column-names -e "SELECT COUNT(*) FROM jd_ecommerce.product WHERE main_image LIKE '/media/%';"
```

Expected: verification PASS; product count is 9; no tables are dropped.

- [ ] **Step 5: Commit**

```bash
git add database/data.sql database/migrations/2026-08-24-local-media.sql backend/src/main/resources/data-h2.sql scripts/verify-local-media.ps1
git commit -m "feat: migrate seed images to local media"
```

### Task 3: Relative upload URLs and public media delivery

**Files:**
- Modify: `backend/src/test/java/com/team/ecommerce/api/FileUploadApiTest.java`
- Modify: `backend/src/main/java/com/team/ecommerce/common/controller/FileController.java`
- Modify: `backend/src/main/java/com/team/ecommerce/config/WebConfig.java`
- Modify: `.gitignore`

**Interfaces:**
- Produces: `POST /api/files/images -> { code: 200, data: { url: "/upload/yyyyMMdd/uuid.ext" } }`.
- Produces: public `GET /media/**` classpath assets and `GET /upload/**` runtime files.

- [ ] **Step 1: Write failing upload/static-resource tests**

Require `$.data.url` to start with `/upload/` and not contain `http://` or `https://`. Add a GET assertion for `/media/common/placeholder.webp` returning 200 and image content type.

```java
expectOk(doUpload(tokenOf(USER1), image("ok.png")))
    .andExpect(jsonPath("$.data.url", startsWith("/upload/")))
    .andExpect(jsonPath("$.data.url", not(containsString("http"))));
mockMvc.perform(get("/media/common/placeholder.webp"))
    .andExpect(status().isOk());
```

- [ ] **Step 2: Run and verify RED**

Run: `backend\mvnw.cmd -q -Dtest=FileUploadApiTest test`

Expected: FAIL because the controller currently returns an absolute URL.

- [ ] **Step 3: Return the relative URL and ignore runtime files**

Replace `ServletUriComponentsBuilder` usage with:

```java
String relativePath = fileService.save(file);
return Result.success(new UploadVO("/upload/" + relativePath), "上传成功");
```

Keep `/api/files/images` authenticated; ensure `/media/**` and `/upload/**` are public; add `backend/upload/` to `.gitignore`.

- [ ] **Step 4: Run tests**

Run: `backend\mvnw.cmd -q -Dtest=FileUploadApiTest,SecurityApiTest test`

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main backend/src/test .gitignore
git commit -m "feat: serve portable local media paths"
```

### Task 4: Web proxies and mobile media URL resolution

**Files:**
- Modify: `frontend-user-web/vite.config.js`
- Modify: `frontend-merchant/vite.config.ts`
- Modify: `frontend-admin/vite.config.ts`
- Modify: `frontend-user-mobile/vite.config.js`
- Create: `frontend-user-mobile/src/utils/media.js`
- Create: `frontend-user-mobile/tests/media.test.js`
- Modify: `frontend-user-mobile/src/api/normalizers.js`

**Interfaces:**
- Produces: `resolveMediaUrl(value: string, baseURL?: string): string`.
- Produces: `/media` and `/upload` Vite proxies targeting `http://localhost:8080` in every browser frontend.

- [ ] **Step 1: Write failing mobile resolver tests**

```js
expect(resolveMediaUrl('/media/products/a.jpg', 'http://localhost:8080')).toBe('http://localhost:8080/media/products/a.jpg')
expect(resolveMediaUrl('/upload/20260824/a.webp', 'http://server')).toBe('http://server/upload/20260824/a.webp')
expect(resolveMediaUrl('https://example.com/a.jpg', 'http://server')).toBe('https://example.com/a.jpg')
expect(resolveMediaUrl('', 'http://server')).toContain('/media/common/placeholder.webp')
```

- [ ] **Step 2: Run and verify RED**

Run: `npm test -- media.test.js` from `frontend-user-mobile`.

Expected: FAIL because `src/utils/media.js` does not exist.

- [ ] **Step 3: Implement resolver and proxies**

```js
const fallback = '/media/common/placeholder.webp'
export function resolveMediaUrl(value, baseURL = import.meta.env.VITE_API_BASE_URL || '') {
  const path = value || fallback
  if (/^(https?:|data:|blob:)/.test(path)) return path
  return `${baseURL.replace(/\/$/, '')}/${path.replace(/^\//, '')}`
}
```

Apply it to product main/sub/SKU images, banners, categories, cart/order images, shop logos, avatars and review images in mobile API normalization. Add proxy entries alongside `/api`:

```js
'/media': { target: 'http://localhost:8080', changeOrigin: true },
'/upload': { target: 'http://localhost:8080', changeOrigin: true },
```

- [ ] **Step 4: Run tests and builds**

Run mobile unit tests plus H5/Weixin builds. Run Web builds for all three Vite apps.

Expected: all commands exit 0.

- [ ] **Step 5: Commit**

```bash
git add frontend-*/vite.config.* frontend-user-mobile/src frontend-user-mobile/tests
git commit -m "feat: resolve local media across clients"
```

### Task 5: Reusable authenticated upload controls

**Files:**
- Create: `frontend-merchant/src/api/file.ts`
- Create: `frontend-merchant/src/components/ImageUploader.vue`
- Create: `frontend-admin/src/api/file.ts`
- Create: `frontend-admin/src/components/ImageUploader.vue`
- Modify: `frontend-merchant/src/views/merchant/ProductForm.vue`
- Modify: `frontend-merchant/src/views/merchant/ShopSettings.vue`
- Modify: `frontend-admin/src/views/admin/BannerManage.vue`

**Interfaces:**
- Produces in each app: `uploadImage(file: File): Promise<string>` returning the relative path.
- Produces component contract: `v-model: string`, optional `multiple: boolean`, `limit: number`; emits the saved relative path(s).

- [ ] **Step 1: Add failing source-contract checks**

Extend `scripts/verify-local-media.ps1` with explicit checks that merchant/admin upload API files and reusable uploader components exist, call `/files/images`, append the `file` form key, and replace URL text inputs in the three target forms. This avoids introducing a second TypeScript test runner solely for source wiring; the application builds remain the executable type/integration checks.

- [ ] **Step 2: Run and verify RED**

Run the PowerShell verifier.

Expected: FAIL because `api/file.ts` and `ImageUploader.vue` do not exist.

- [ ] **Step 3: Implement upload APIs and components**

```ts
export async function uploadImage(file: File): Promise<string> {
  const body = new FormData()
  body.append('file', file)
  const response: any = await post('/files/images', body, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data.url
}
```

The component must validate `file.type.startsWith('image/')` and `file.size <= 5 * 1024 * 1024`, render an `<img>` preview, emit only after a successful upload, and keep the previous value on failure.

- [ ] **Step 4: Integrate forms**

`ProductForm.vue` must model `mainImage`, `subImages: string[]`, and every `skuImage`; `ShopSettings.vue` must upload `shopLogo`; `BannerManage.vue` must upload `imageUrl`. Preserve edit-form values returned by the backend.

- [ ] **Step 5: Run tests and builds**

Run the PowerShell verifier and `npm run build` in both apps.

Expected: PASS and builds exit 0.

- [ ] **Step 6: Commit**

```bash
git add frontend-merchant frontend-admin
git commit -m "feat: upload merchant and banner images"
```

### Task 6: Offline mock data, fallbacks and consumer upload compatibility

**Files:**
- Modify: `frontend-user-web/src/api/mock/data.js`
- Modify: `frontend-user-mobile/src/api/mock/data.js`
- Modify: `frontend-merchant/src/api/mock/*.ts`
- Modify: `frontend-admin/src/api/mock/*.ts`
- Modify: `frontend-user-web/src/api/adapters.js`
- Modify: `frontend-user-web/tests/api-adapters.test.js`
- Modify: `frontend-user-mobile/src/pages/profile/merchant-apply.vue`
- Modify: `README.md`

**Interfaces:**
- Consumes: `/media` and `/upload` conventions from Tasks 1–4.
- Produces: no remote runtime image references in mock/seed UI data; consumer upload flows continue accepting relative upload results.

- [ ] **Step 1: Add failing repository media scan**

Extend `verify-local-media.ps1` to scan runtime mock data and reject `img.jd-demo.com`/`images.unsplash.com`. Add adapter assertions that missing product images become `/media/common/placeholder.webp`.

- [ ] **Step 2: Run and verify RED**

Run the PowerShell verifier and consumer adapter tests.

Expected: FAIL listing remaining remote mock URLs.

- [ ] **Step 3: Replace mock URLs and normalize fallbacks**

Map mock products/banners/shops/categories to the same `/media` paths as MySQL. Ensure user-web proxy-served paths stay relative; ensure mobile normalization calls `resolveMediaUrl`. Preserve `data:`/`blob:` preview URLs.

- [ ] **Step 4: Update documentation**

Document seed media location, runtime upload location, upload limits, database relative-path convention, how to rerun the download script, and the need to back up `backend/upload/` when moving machines.

- [ ] **Step 5: Run verification and frontend tests**

Run `verify-local-media.ps1`, user-web tests, mobile tests, and all four frontend builds.

Expected: all pass; runtime source scan finds zero external seed/mock image hosts.

- [ ] **Step 6: Commit**

```bash
git add frontend-* README.md scripts/verify-local-media.ps1
git commit -m "fix: make client media fully offline"
```

### Task 7: Real database and end-to-end verification

**Files:**
- Modify: `frontend-user-mobile/tests/e2e/mobile-flow.spec.js`
- Create: `frontend-user-mobile/tests/e2e/merchant-image-upload.spec.js`
- Modify: `frontend-user-mobile/playwright.config.js` only if required to run the merchant-targeted spec.

**Interfaces:**
- Consumes: live `jd_ecommerce`, backend port 8080, consumer H5 port 5174, merchant port 5175.
- Produces: evidence that seed media and newly uploaded media both work end to end.

- [ ] **Step 1: Add failing E2E assertions**

Consumer flow must assert first product image `src` contains `/media/` and its natural width is positive. Merchant flow must login as `merchant1`, upload a generated test PNG through the UI, assert preview `src` contains `/upload/`, and verify the submitted product payload contains that relative path.

- [ ] **Step 2: Run targeted E2E and verify RED before final integration**

Start backend, consumer H5 and merchant frontend using the documented ports. Run both specs from the existing mobile Playwright installation, setting `MERCHANT_E2E_BASE_URL=http://127.0.0.1:5175` for the merchant-targeted spec.

Expected: the merchant test fails until the upload control integration is complete or the consumer test fails until migrated media is served.

- [ ] **Step 3: Fix only integration defects revealed by E2E**

Do not broaden scope. Keep uploaded test files tracked for cleanup and remove them in test teardown.

- [ ] **Step 4: Run full verification**

Run:

```powershell
.\scripts\verify-local-media.ps1
cd backend; .\mvnw.cmd clean test
cd ..\frontend-user-mobile; npm test; npm run build:h5; npm run build:mp-weixin
cd ..\frontend-user-web; npm test; npm run build
cd ..\frontend-merchant; npm test; npm run build
cd ..\frontend-admin; npm test; npm run build
```

Then query every distinct `/media` path referenced by MySQL through HTTP and require status 200. Run Playwright with outbound image domains blocked to prove no seed page depends on external images.

Expected: backend 0 failures/errors; all frontend tests/builds exit 0; all referenced local media returns 200; Playwright passes with external image requests blocked.

- [ ] **Step 5: Final commit**

```bash
git add .
git commit -m "test: verify offline media workflows"
```
