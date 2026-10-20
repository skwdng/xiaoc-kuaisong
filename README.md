# 小c快送 CEXPRESS

校园外卖订餐平台，采用「微信小程序用户端 + Web 管理端 + Spring Boot 后端」三端架构，覆盖浏览点菜、购物车、下单支付、订单跟踪、商家接单调度、营业管理与数据统计的完整外卖业务闭环，并通过内网穿透实现真机公网访问。

## 🎬 演示视频

- 📱 [用户端演示（4.5MB）](demo/用户端演示.mp4)
- 🖥️ [管理端演示（12MB）](demo/管理端演示.mp4)

> 视频文件较大，**GitHub 上请点右上角 Download 下载后观看**（Gitee 仓库支持在线播放）。

## ✨ 功能特性

**用户端（微信小程序）**

- 微信授权一键登录，JWT 令牌 7 天有效，过期自动引导重新登录
- 按分类浏览菜品 / 套餐，查看菜品详情与规格口味
- 购物车增删改、地址簿增删改查与默认地址、订单备注
- 提交订单、在线支付、历史订单、订单状态实时跟踪、一键催单
- 店铺营业 / 打烊状态提示、网络异常兜底页

**管理端（Web）**

- 员工账号管理，菜品分类、菜品、套餐管理（起售 / 停售、批量操作）
- 订单调度：接单、拒单、派送、完成、取消，催单即时处理
- 客户下单 / 催单 WebSocket 实时弹窗 + 语音提醒
- 数据报表：营业额、用户量、订单量、销量 Top10，支持 Excel 导出
- 店铺营业状态切换、图片上传（阿里云 OSS / 本地存储）

**服务端**

- Maven 多模块工程（sky-common / sky-pojo / sky-server），标准三层架构
- 管理端 / 用户端双 JWT 无状态认证，独立拦截器与签名密钥
- Redis 缓存菜品、套餐、店铺营业状态；Spring AOP 自动填充公共字段
- WebSocket 长连接实时推送来单与催单消息
- Druid 连接池 keep-alive 保活、全局异常统一处理、Knife4j 接口文档
- 图片统一存储相对路径，由 Nginx / 小程序端动态拼接，穿透域名变更零影响

## 🧱 技术栈

| 分层 | 技术 |
|---|---|
| 后端框架 | Spring Boot 2.7.3、Spring MVC、Spring AOP |
| 安全认证 | JWT（jjwt 0.9.1），双端独立密钥与拦截器 |
| ORM | MyBatis、PageHelper 分页、MySQL 8.x（utf8mb4） |
| 连接池 | Druid（validation-query + keep-alive 保活） |
| 缓存 | Redis（Spring Data Redis，db10） |
| 实时通信 | Spring WebSocket（`/ws/{clientId}`） |
| 对象存储 | 阿里云 OSS SDK 3.10.2，兼容本地目录存储 |
| 接口文档 | Knife4j 4.4.0（OpenAPI 3） |
| 用户端 | 微信小程序（uni-app 编译产物，WXML / WXSS / JS） |
| 管理端 | Vue + Element UI，Nginx 静态部署（源码不在本仓库） |
| 工具 | Lombok、FastJSON、Apache POI、Maven |
| 部署 | 内置 Tomcat jar 包、Nginx 反代、cpolar 内网穿透 |

## 🏗️ 架构设计

```
微信小程序(mp-weixin)                 Web管理端(Vue静态页)
  │ HTTPS(cpolar公网穿透)              │ HTTP(Nginx :80)
  │  authentication: JWT              │  token: JWT
  ▼                                    ▼
           Nginx(/upload/ 等请求反代)
                   │
                   ▼
          Spring Boot 后端 :8080
   Controller(admin / user 双端分包)
      │  双JWT拦截器 → ThreadLock(当前操作人)
      │  AOP(公共字段自动填充) + 全局异常处理
      ▼
   Service ──► Redis(菜品/套餐/营业状态缓存)
      │  └ miss ─► MySQL
      ▼
   MyBatis(XML) ──► MySQL(sky_take_out, utf8mb4)

   下单 / 催单 ──► WebSocket ──► 管理端弹窗 + 语音实时提醒
   图片上传   ──► 阿里云OSS / 本地目录，返回 /upload/ 相对路径
```

## 📁 目录结构

```
CExpress/
├── mp-weixin/                  # 微信小程序用户端（导入微信开发者工具）
│   ├── pages/                  # 首页/点菜/购物车/订单/支付/地址/我的等页面
│   ├── common/vendor.js        # 请求封装、baseUrl、图片相对路径自动补全
│   ├── components/             # 导航栏、图标等公共组件
│   └── app.json
├── sky-take-out/               # Spring Boot 后端（Maven 多模块）
│   ├── sky-common/             # 常量/异常/属性/工具(JWT、OSS、微信支付)
│   ├── sky-pojo/               # entity / dto / vo
│   └── sky-server/
│       └── src/main/
│           ├── java/com/sky/
│           │   ├── controller/admin/  # 员工/分类/菜品/套餐/订单/报表/店铺
│           │   ├── controller/user/   # 登录/地址簿/购物车/下单/支付
│           │   ├── interceptor/       # 管理端、用户端 JWT 拦截器
│           │   ├── aspect/            # @AutoFill 公共字段自动填充
│           │   ├── websocket/         # 来单/催单实时推送
│           │   ├── mapper/ service/ handler/ config/
│           │   └── SkyApplication.java
│           └── resources/
│               ├── mapper/            # MyBatis XML
│               ├── application.yml
│               └── application-dev.example.yml  # 环境配置模板
├── demo/                       # 演示视频（用户端 / 管理端）
├── 启动小c快送后端.bat
└── 小c快送开机自启.bat
```

## 🚀 快速开始

### 环境要求

- JDK 8+、Maven 3.6+
- MySQL 8.0
- Redis
- 微信开发者工具

### 1. 初始化数据库

创建数据库 `sky_take_out`（字符集 `utf8mb4`，支持 emoji 存储），并建外卖业务表：`employee`、`category`、`dish`、`dish_flavor`、`setmeal`、`setmeal_dish`、`user`、`address_book`、`shopping_cart`、`orders`、`order_detail`。默认管理员账号 `admin` / `123456`。

### 2. 修改配置

复制 `sky-server/src/main/resources/application-dev.example.yml` 为同目录 `application-dev.yml`，按实际环境填写：

| 配置项 | 默认值 | 说明 |
|---|---|---|
| MySQL | localhost:3306/sky_take_out | 账号 root，密码填本机密码 |
| Redis | localhost:6379，database 10 | 填本机 Redis 密码 |
| 阿里云 OSS | oss-cn-beijing.aliyuncs.com | AccessKey 与 bucket（也可改用本地目录） |
| 微信小程序 | — | appid 与 secret |

### 3. 启动后端

```bash
cd sky-take-out
mvn -DskipTests package
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

Windows 也可直接双击根目录的 `启动小c快送后端.bat`。启动成功后：

- 健康检查：http://localhost:8080/user/shop/status
- 接口文档：http://localhost:8080/doc.html

### 4. 运行小程序

1. 微信开发者工具导入 `mp-weixin` 目录；
2. 详情 → 本地设置 → 勾选「不校验合法域名」；
3. 服务端地址在 `mp-weixin/common/vendor.js`（约 20592 行）配置：开发者工具默认走 `http://localhost:8080`，真机预览走 HTTPS 内网穿透域名，并在微信公众平台配置 request / uploadFile / downloadFile 合法域名。

### 5. 管理端

管理端为 Vue 静态页面，由 Nginx 部署在 80 端口，接口与 `/upload/` 请求反代到后端 8080。启动 Nginx 后访问 http://localhost ，使用 `admin` / `123456` 登录。

## 🔑 核心设计说明

**1. 双端 JWT 认证** 管理端与用户端使用独立密钥和拦截器：管理端令牌名为 `token`，用户端为 `authentication` 且有效期 7 天；拦截器校验通过后将当前操作人 id 存入 ThreadLocal，供业务层与 AOP 使用。

**2. Redis 缓存** 用户端菜品、套餐列表按分类缓存，店铺营业状态存入 Redis；菜品 / 套餐变更时主动清除缓存保证一致性；营业状态缓存丢失时接口以「营业中」兜底，避免空指针导致的 500 错误。

**3. WebSocket 实时推送** 管理端建立 `/ws/{clientId}` 长连接，用户下单、催单后服务端群发消息，管理端收到即弹窗并语音播报，无需轮询。

**4. 公共字段自动填充** 自定义 `@AutoFill` 注解配合 AOP 切面，在 insert / update 时自动填充创建人、创建时间、修改人、修改时间，业务代码零重复。

**5. 图片相对路径方案** 数据库只存 `/upload/xxx` 相对路径：小程序请求封装递归遍历响应数据，按当前 baseUrl 动态补全；管理端由 Nginx 反代补全。内网穿透域名更换时所有图片不受影响。

**6. 稳定性细节** Druid 配置 keep-alive 与空闲连接检测，防止 MySQL 掐断空闲连接导致偶发 500；业务表统一 utf8mb4 支持 emoji 昵称与备注；后端日志独立输出到 `backend-app.log` 便于排查。

## 📝 说明

- 当前为**模拟支付**（未配置微信商户号），下单即标记支付成功并进入待接单；`sky-common` 已保留 `WeChatPayUtil`（微信支付 APIv3），补齐商户号与证书后可切换真实支付。
- 管理端前端为独立工程，源码不在本仓库，功能演示见 `demo/管理端演示.mp4`。
- `application-dev.yml` 含数据库 / OSS / 微信密钥等敏感信息，仓库仅提供 `.example` 模板，请勿提交真实配置。
- cpolar 免费版域名随服务重启变化，更新后需同步修改 `vendor.js` 的 baseUrl 与小程序后台合法域名。
