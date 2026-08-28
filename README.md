# Harbor Hotel Backend

酒店前台 MVP 的后端服务，面向前台员工提供登录、按房型预订、订单查询、多房办理入住、取消预订和今日看板能力。

## 技术栈

- Java 21、Spring Boot 3.5、Maven
- MySQL 8.4、MyBatis XML、HikariCP
- Spring Security Session + CSRF
- JUnit 5、ArchUnit；MySQL 集成测试使用 Failsafe

## 工程结构

```text
harbor-domain/          领域模型、领域规则、错误码和 Repository 接口
harbor-app/             Processor、Qurier、DTO、库存同步任务
harbor-infrastructure/  MyBatis Mapper、Repository 实现和认证适配器
harbor-api/             Controller、请求对象、VO、鉴权与统一异常处理
harbor-start/           Spring Boot 启动模块、配置、Mapper XML 和测试
```

写操作由 `XxxProcessor` 协调同库事务；查询由 `XxxQurier` 负责。`harbor-domain` 不依赖 Web 或数据库实现。

## 前置条件

- JDK 21
- Maven 3.9+
- MySQL 8.4，已创建业务库并初始化系统所需表、房型、房间和员工数据

本仓库不保存员工明文密码。启动前请使用实际初始化时设置的员工账号和密码。

## 本地启动

在仓库根目录执行构建：

```bash
mvn clean package
```

通过环境变量指定数据库后启动：

```bash
HOTEL_DB_URL='jdbc:mysql://127.0.0.1:3306/harbor_local_demo?useUnicode=true&characterEncoding=utf8&connectionTimeZone=Asia/Shanghai' \
HOTEL_DB_USERNAME=root \
HOTEL_DB_PASSWORD='' \
java -jar harbor-start/target/harbor-start-0.1.0-SNAPSHOT.jar --server.port=18080
```

服务默认监听 `http://127.0.0.1:18080`。开发环境前端默认会把 `/api` 代理到该地址。

### 配置项

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `18080` | HTTP 端口 |
| `HOTEL_DB_URL` | `jdbc:mysql://127.0.0.1:3306/harbor_hotel...` | MySQL 连接地址 |
| `HOTEL_DB_USERNAME` | `root` | 数据库账号 |
| `HOTEL_DB_PASSWORD` | 空 | 数据库密码 |
| `HOTEL_ORDER_NO_MACHINE_ID` | `1` | 订单号机器号，范围 0–999 |
| `HOTEL_COOKIE_SECURE` | `false` | HTTPS 环境设为 `true` |
| `XXL_JOB_ADMIN_ADDRESSES` | 空 | 配置 XXL-JOB 时的调度中心地址 |

## 主要接口

所有响应采用 `{ code, message, data, requestId, traceId }` 结构。错误码统一定义在 `harbor-domain` 的 `ErrorCode` 中。

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `GET` | `/api/auth/csrf` | 获取 CSRF Token |
| `POST` | `/api/auth/login` | 员工登录 |
| `GET` | `/api/auth/me` | 查询当前员工 |
| `POST` | `/api/auth/logout` | 退出登录 |
| `GET` | `/api/dashboard` | 今日看板 |
| `GET` | `/api/hotel-context` | 酒店日期与上下文 |
| `GET` | `/api/room-types/availability` | 可预订房型 |
| `POST` | `/api/booking_orders` | 创建预订，需 `Idempotency-Key` |
| `GET` | `/api/orders` | 分页查询订单 |
| `GET` | `/api/orders/{id}` | 查询订单详情 |
| `GET` | `/api/orders/{id}/available-rooms` | 查询可入住候选房间 |
| `POST` | `/api/booking_orders/{id}/check-in` | 多房办理入住，需 `Idempotency-Key` |
| `POST` | `/api/booking_orders/{id}/cancel` | 取消预订，需 `Idempotency-Key` |

除登录、CSRF 获取和退出外，接口要求已登录会话。所有写请求还需要携带从 `/api/auth/csrf` 返回的 CSRF 请求头。

## 验证

```bash
mvn test
```

在可访问的 MySQL 管理连接下执行集成测试：

```bash
HOTEL_TEST_ADMIN_URL='jdbc:mysql://127.0.0.1:3306/?connectionTimeZone=Asia/Shanghai' \
mvn verify -Pmysql-it
```

集成测试会创建和删除独立测试库；请勿将管理员连接指向生产数据库。

## 业务约束

- 预订按房型和入住日期锁定库存；入住时再分配具体物理房间。
- 入住区间为入住日 12:00（含）至离店日 12:00（不含）。
- 创建预订、入住和取消使用请求幂等键；重试必须复用原键和原参数。
- 库存按未来 7 天同步，预订、入住、取消在同一事务内更新库存、库存锁及订单状态。
