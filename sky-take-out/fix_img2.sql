UPDATE order_detail SET image = REPLACE(image, 'http://localhost:8080', 'https://cff91af.r30.cpolar.top') WHERE image LIKE 'http://localhost:8080%';
SELECT 'order_detail' AS t, COUNT(*) AS total, SUM(image LIKE 'https://cff91af%') AS fixed_urls FROM order_detail;
