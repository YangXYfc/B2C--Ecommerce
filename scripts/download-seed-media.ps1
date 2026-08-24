$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$mediaRoot = Join-Path $repositoryRoot 'backend/src/main/resources/static/media'
$baseParameters = '?auto=format&fit=crop&q=82'

$assets = @(
    @{ Path = 'products/product-01-phone.jpg'; Url = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9$baseParameters&w=1200" },
    @{ Path = 'products/product-02-phone.jpg'; Url = "https://images.unsplash.com/photo-1598327105666-5b89351aff97$baseParameters&w=1200" },
    @{ Path = 'products/product-03-laptop.jpg'; Url = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853$baseParameters&w=1200" },
    @{ Path = 'products/product-04-charger.jpg'; Url = "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5$baseParameters&w=1200" },
    @{ Path = 'products/product-05-tshirt.jpg'; Url = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab$baseParameters&w=1200" },
    @{ Path = 'products/product-06-dress.jpg'; Url = "https://images.unsplash.com/photo-1595777457583-95e059d581b8$baseParameters&w=1200" },
    @{ Path = 'products/product-07-projector.jpg'; Url = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1$baseParameters&w=1200" },
    @{ Path = 'products/product-08-jacket.jpg'; Url = "https://images.unsplash.com/photo-1520975954732-35dd22299614$baseParameters&w=1200" },
    @{ Path = 'products/product-09-foldable.jpg'; Url = "https://images.unsplash.com/photo-1565849904461-04a58ad377e0$baseParameters&w=1200" },
    @{ Path = 'products/phone-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5$baseParameters&w=1200" },
    @{ Path = 'products/laptop-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8$baseParameters&w=1200" },
    @{ Path = 'products/laptop-detail-02.jpg'; Url = "https://images.unsplash.com/photo-1522199755839-a2bacb67c546$baseParameters&w=1200" },
    @{ Path = 'products/charger-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0$baseParameters&w=1200" },
    @{ Path = 'products/tshirt-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1503341504253-dff4815485f1$baseParameters&w=1200" },
    @{ Path = 'products/tshirt-detail-02.jpg'; Url = "https://images.unsplash.com/photo-1576566588028-4147f3842f27$baseParameters&w=1200" },
    @{ Path = 'products/tshirt-detail-03.jpg'; Url = "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f$baseParameters&w=1200" },
    @{ Path = 'products/dress-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1496747611176-843222e1e57c$baseParameters&w=1200" },
    @{ Path = 'products/dress-detail-02.jpg'; Url = "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446$baseParameters&w=1200" },
    @{ Path = 'products/projector-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1461151304267-38535e780c79$baseParameters&w=1200" },
    @{ Path = 'products/jacket-detail-01.jpg'; Url = "https://images.unsplash.com/photo-1551028719-00167b16eac5$baseParameters&w=1200" },
    @{ Path = 'products/jacket-detail-02.jpg'; Url = "https://images.unsplash.com/photo-1543076447-215ad9ba6923$baseParameters&w=1200" },
    @{ Path = 'banners/banner-01-sale.jpg'; Url = "https://images.unsplash.com/photo-1607083206869-4c7672e72a8a$baseParameters&w=1800&h=720" },
    @{ Path = 'banners/banner-02-digital.jpg'; Url = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3$baseParameters&w=1800&h=720" },
    @{ Path = 'banners/banner-03-fashion.jpg'; Url = "https://images.unsplash.com/photo-1491933382434-500287f9b54b$baseParameters&w=1800&h=720" },
    @{ Path = 'banners/banner-04-appliance.jpg'; Url = "https://images.unsplash.com/photo-1517430816045-df4b7de11d1d$baseParameters&w=1800&h=720" },
    @{ Path = 'shops/shop-01-digital.jpg'; Url = "https://images.unsplash.com/photo-1768137348738-ce11911f54f6$baseParameters&w=900&h=900" },
    @{ Path = 'shops/shop-02-fashion.jpg'; Url = "https://images.unsplash.com/photo-1709417596263-a7a965e73314$baseParameters&w=900&h=900" },
    @{ Path = 'shops/shop-03-pending.jpg'; Url = "https://images.unsplash.com/photo-1777988683342-a3af9ddf0cb2$baseParameters&w=900&h=900" },
    @{ Path = 'categories/category-01-digital.jpg'; Url = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9$baseParameters&w=700&h=700" },
    @{ Path = 'categories/category-02-appliance.jpg'; Url = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1$baseParameters&w=700&h=700" },
    @{ Path = 'categories/category-03-fashion.jpg'; Url = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab$baseParameters&w=700&h=700" },
    @{ Path = 'categories/category-04-food.jpg'; Url = "https://images.unsplash.com/photo-1542838132-92c53300491e$baseParameters&w=700&h=700" },
    @{ Path = 'common/placeholder.webp'; Url = 'https://images.unsplash.com/photo-1777988683342-a3af9ddf0cb2?auto=format&fit=crop&fm=webp&q=75&w=900&h=900' }
)

foreach ($asset in $assets) {
    $destination = Join-Path $mediaRoot $asset.Path
    $directory = Split-Path -Parent $destination
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
    Write-Host "Downloading $($asset.Path)"
    Invoke-WebRequest -Uri $asset.Url -OutFile $destination -UseBasicParsing

    if ((Get-Item -LiteralPath $destination).Length -le 1024) {
        throw "Downloaded asset is unexpectedly small: $destination"
    }
}

Write-Host "Downloaded $($assets.Count) local media assets."
