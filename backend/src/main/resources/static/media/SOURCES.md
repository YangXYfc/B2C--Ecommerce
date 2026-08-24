# Seed media sources

All files in this directory are versioned development/demo assets. They are downloaded once by `scripts/download-seed-media.ps1`; the running application never downloads remote images.

The photographs are served from Unsplash and are free to use under the [Unsplash License](https://unsplash.com/license). Query parameters only resize and crop the original photograph for this demo.

## Existing project photographs

These Unsplash photo IDs already appeared in `database/data.sql` and were retained to preserve the existing visual style:

- Products and details: `1511707171634-5f897ff02aa9`, `1598327105666-5b89351aff97`, `1565849904461-04a58ad377e0`, `1510557880182-3d4d3cba35a5`, `1496181133206-80ce9b88a853`, `1517336714731-489689fd1ca8`, `1522199755839-a2bacb67c546`, `1609091839311-d5365f9ff1c5`, `1583863788434-e58a36330cf0`, `1521572163474-6864f9cf17ab`, `1503341504253-dff4815485f1`, `1576566588028-4147f3842f27`, `1489987707025-afc232f7ea0f`, `1595777457583-95e059d581b8`, `1496747611176-843222e1e57c`, `1515372039744-b8f02a3ae446`, `1593359677879-a4bb92f829d1`, `1461151304267-38535e780c79`, `1520975954732-35dd22299614`, `1551028719-00167b16eac5`, `1543076447-215ad9ba6923`.
- Banners: `1607083206869-4c7672e72a8a`, `1516321318423-f06f85e504b3`, `1491933382434-500287f9b54b`, `1517430816045-df4b7de11d1d`.

## Added photographs

- `shops/shop-01-digital.jpg`: electronics retail interior, [Unsplash photo](https://unsplash.com/photos/fRellSxhFi8), photo ID `1768137348738-ce11911f54f6`.
- `shops/shop-02-fashion.jpg`: clothing rack, [Unsplash photo](https://unsplash.com/photos/X4Ya3-wg2OU), photo ID `1709417596263-a7a965e73314`.
- `shops/shop-03-pending.jpg` and `common/placeholder.webp`: neutral shopping bags, [Unsplash photo](https://unsplash.com/photos/KqOeBSbC1Ns), photo ID `1777988683342-a3af9ddf0cb2`.
- `categories/category-04-food.jpg`: fresh groceries, [Unsplash photo](https://unsplash.com/photos/D6Tu_L3chLE), photo ID `1542838132-92c53300491e`.

The other category thumbnails reuse a matching versioned product photograph at a square crop. Product SKUs, order snapshots and review examples reuse the corresponding product media paths rather than duplicating files.

`common/brand-mark.svg` is an original project asset created for the 悦选 consumer app and is not downloaded from a third party.
