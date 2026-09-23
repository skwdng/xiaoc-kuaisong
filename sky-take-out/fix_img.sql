UPDATE dish SET image = REPLACE(image, 'http://localhost:8080', 'https://cff91af.r30.cpolar.top') WHERE image LIKE 'http://localhost:8080%';
UPDATE setmeal SET image = REPLACE(image, 'http://localhost:8080', 'https://cff91af.r30.cpolar.top') WHERE image LIKE 'http://localhost:8080%';
UPDATE user SET avatar = REPLACE(avatar, 'http://localhost:8080', 'https://cff91af.r30.cpolar.top') WHERE avatar LIKE 'http://localhost:8080%';
SELECT 'dish' AS t, COUNT(*) AS total, SUM(image LIKE 'https://cff91af%') AS fixed_urls FROM dish
UNION ALL SELECT 'setmeal', COUNT(*), SUM(image LIKE 'https://cff91af%') FROM setmeal
UNION ALL SELECT 'user_avatar', COUNT(*), SUM(avatar LIKE 'https://cff91af%') FROM user;
