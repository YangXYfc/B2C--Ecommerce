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

Write-Host "Local media verification passed: $verified files checked."
