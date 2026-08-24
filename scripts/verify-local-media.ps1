$ErrorActionPreference = 'Stop'

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$mediaRoot = Join-Path $repositoryRoot 'backend/src/main/resources/static/media'

$requiredFiles = @(
    'SOURCES.md',
    'common/placeholder.webp',
    'products/product-01-phone.jpg',
    'products/product-02-phone.jpg',
    'products/product-03-laptop.jpg',
    'products/product-04-charger.jpg',
    'products/product-05-tshirt.jpg',
    'products/product-06-dress.jpg',
    'products/product-07-projector.jpg',
    'products/product-08-jacket.jpg',
    'products/product-09-foldable.jpg',
    'banners/banner-01-sale.jpg',
    'banners/banner-02-digital.jpg',
    'banners/banner-03-fashion.jpg',
    'banners/banner-04-appliance.jpg',
    'shops/shop-01-digital.jpg',
    'shops/shop-02-fashion.jpg',
    'shops/shop-03-pending.jpg',
    'categories/category-01-digital.jpg',
    'categories/category-02-appliance.jpg',
    'categories/category-03-fashion.jpg',
    'categories/category-04-food.jpg'
)

$verified = 0
foreach ($relativePath in $requiredFiles) {
    $absolutePath = Join-Path $mediaRoot $relativePath
    if (-not (Test-Path -LiteralPath $absolutePath -PathType Leaf)) {
        throw "Missing local media asset: $absolutePath"
    }

    $file = Get-Item -LiteralPath $absolutePath
    if ($relativePath -ne 'SOURCES.md' -and $file.Length -le 1024) {
        throw "Local media asset is smaller than 1 KB: $absolutePath"
    }
    $verified++
}

$seedFiles = @(
    'database/data.sql',
    'backend/src/main/resources/data-h2.sql',
    'database/migrations/2026-08-24-local-media.sql'
)
foreach ($seedFile in $seedFiles) {
    $seedPath = Join-Path $repositoryRoot $seedFile
    if (-not (Test-Path -LiteralPath $seedPath -PathType Leaf)) {
        throw "Missing local media SQL file: $seedFile"
    }
    $seedText = Get-Content -Raw -LiteralPath $seedPath
    if ($seedText -match 'images\.unsplash\.com|img\.jd-demo\.com') {
        throw "Remote image URL remains in seed data: $seedFile"
    }

    $mediaPaths = [regex]::Matches(
        $seedText,
        '/media/[A-Za-z0-9_./-]+\.(?:jpg|jpeg|png|webp)'
    ) | ForEach-Object { $_.Value } | Sort-Object -Unique
    foreach ($mediaPath in $mediaPaths) {
        $assetPath = Join-Path $mediaRoot $mediaPath.Substring('/media/'.Length)
        if (-not (Test-Path -LiteralPath $assetPath -PathType Leaf)) {
            throw "SQL references a missing local media asset: $mediaPath in $seedFile"
        }
    }
}

$migrationPath = Join-Path $repositoryRoot 'database/migrations/2026-08-24-local-media.sql'
$migrationText = Get-Content -Raw -LiteralPath $migrationPath
if ($migrationText -notmatch 'ps\.id\s*=\s*oi\.product_sku_id') {
    throw 'Order item migration must resolve product media through product_sku_id.'
}
if ($migrationText -match 'oi\.product_id') {
    throw 'Order item migration references the nonexistent order_item.product_id column.'
}

Write-Host "Local media verification passed: $verified files checked."
