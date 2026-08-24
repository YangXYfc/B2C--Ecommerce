param(
    [string]$MySqlPath = 'D:\MySQL\bin\mysql.exe',
    [string]$Database = 'jd_ecommerce',
    [string]$User = 'root',
    [string]$Password = $env:DB_PASSWORD,
    [string]$BaseUrl = 'http://localhost:8080'
)

$ErrorActionPreference = 'Stop'
if ([string]::IsNullOrWhiteSpace($Password)) { $Password = '123456' }
if (-not (Test-Path -LiteralPath $MySqlPath -PathType Leaf)) {
    throw "MySQL client not found: $MySqlPath"
}

$query = @'
SELECT DISTINCT media_path
FROM (
    SELECT shop_logo AS media_path FROM merchant
    UNION ALL SELECT icon FROM category
    UNION ALL SELECT main_image FROM product
    UNION ALL SELECT sku_image FROM product_sku
    UNION ALL SELECT product_image FROM order_item
    UNION ALL SELECT image_url FROM banner
    UNION ALL
        SELECT product_media.media_path
        FROM product,
             JSON_TABLE(
                 COALESCE(NULLIF(sub_images, ''), '[]'),
                 '$[*]' COLUMNS (media_path VARCHAR(255) PATH '$')
             ) AS product_media
    UNION ALL
        SELECT review_media.media_path
        FROM review,
             JSON_TABLE(
                 COALESCE(NULLIF(images, ''), '[]'),
                 '$[*]' COLUMNS (media_path VARCHAR(255) PATH '$')
             ) AS review_media
) AS all_media
WHERE media_path LIKE '/media/%'
ORDER BY media_path;
'@

$paths = & $MySqlPath "-u$User" "--password=$Password" --batch --skip-column-names $Database -e $query
if ($LASTEXITCODE -ne 0) { throw 'Failed to query media paths from MySQL.' }
if (-not $paths) { throw "No /media paths found in database $Database." }

$verified = 0
foreach ($path in $paths) {
    $response = Invoke-WebRequest -Uri "$($BaseUrl.TrimEnd('/'))$path" -UseBasicParsing
    if ($response.StatusCode -ne 200) { throw "Media request failed: $path" }
    if ($response.Headers['Content-Type'] -notlike 'image/*') {
        throw "Media response is not an image: $path ($($response.Headers['Content-Type']))"
    }
    $verified++
}

Write-Host "Live media verification passed: $verified distinct database paths returned images."
