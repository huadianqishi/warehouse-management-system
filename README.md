# 仓储管理系统后端项目

## 项目介绍

基于Spring Boot的仓储管理系统后端API服务，提供完整的仓库、货架、商品管理功能，支持多角色权限管理。

**开发团队**：毛煜祺、聂智天、钟昌盛  
**指导教师**：陈权  
**开发时间**：2025年

## 技术栈

- Spring Boot 2.7.14
- MyBatis Plus 3.5.3.1
- MySQL 8.0+ (MySQL Connector 8.0.33)
- JWT (JSON Web Token) 0.9.1
- Druid 数据库连接池 1.2.18
- Lombok 1.18.30
- Hutool 工具库 5.8.20
- Spring Boot Validation
- JDK 1.8+
- Maven 3.6+

## 功能模块

### 1. 用户管理
- 用户登录/注册
- 多角色支持（超级管理员、管理员、仓管员）
- 用户信息管理

### 2. 仓库管理
- 仓库增删改查
- 自动统计货架数量和存量
- 删除前检查是否有货架

### 3. 货架管理
- 货架增删改查
- 货架区域分类
- 删除前检查是否有商品

### 4. 商品管理
- 商品增删改查
- 商品模糊搜索（支持商品名称和运单ID）
- 自动更新仓库存量

### 5. 操作记录
- 记录所有增删改操作
- 按用户查询操作历史
- 分页查询支持

## 权限说明

| 角色 | 代码 | 权限描述 |
|------|------|---------|
| 超级管理员 | 0 | 全部功能：管理用户、仓库、货架、商品 |
| 管理员/员工 | 1 | 只能查询商品和查看历史记录 |
| 仓管员 | 2 | 可以增删改查仓库、货架、商品 |

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库并执行初始化脚本：

```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

2. 修改配置文件 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse_db
    username: root
    password: your_password
```

### 3. 启动项目

```bash
# 进入项目目录
cd Idea

# 使用Maven编译
mvn clean package

# 运行项目
java -jar target/warehouse-management-system-1.0.0.jar

# 或者直接运行主类
mvn spring-boot:run
```

### 4. 访问接口

项目启动后，访问地址：http://localhost:8080/api

默认账号：
- 超级管理员：`admin` / `admin123`
- 仓管员：`keeper` / `keeper123`
- 管理员：`employee` / `emp123`

## API接口文档

### 认证说明

除登录接口外，所有接口都需要在请求头中携带Token：

```
Authorization: your_jwt_token
```

### 用户接口

#### 1. 用户登录
```
POST /api/user/login
参数：username=admin&password=admin123
返回：token和用户信息
```

#### 2. 添加用户（仅超级管理员）
```
POST /api/user/add
请求头：Authorization: token
请求体：
{
  "username": "testuser",
  "password": "123456",
  "realName": "测试用户",
  "gender": "男",
  "phone": "13800138000",
  "idCard": "110000199001011234",
  "role": 2
}
```

#### 3. 删除用户（仅超级管理员）
```
DELETE /api/user/delete/{id}
请求头：Authorization: token
```

#### 4. 修改用户信息（仅超级管理员）
```
PUT /api/user/update
请求头：Authorization: token
请求体：User对象
```

#### 5. 查询用户
```
GET /api/user/get/{id}          # 根据ID查询
GET /api/user/list              # 查询所有用户
GET /api/user/page?current=1&size=10  # 分页查询
GET /api/user/role/{role}       # 根据角色查询
```

### 仓库接口

#### 1. 添加仓库
```
POST /api/warehouse/add
请求头：Authorization: token
请求体：
{
  "warehouseNumber": "WH003",
  "address": "深圳市南山区仓储中心C栋"
}
```

#### 2. 删除仓库
```
DELETE /api/warehouse/delete/{id}
请求头：Authorization: token
```

#### 3. 修改仓库
```
PUT /api/warehouse/update
请求头：Authorization: token
请求体：Warehouse对象
```

#### 4. 查询仓库
```
GET /api/warehouse/get/{id}     # 根据ID查询
GET /api/warehouse/list         # 查询所有仓库
GET /api/warehouse/page?current=1&size=10  # 分页查询
```

### 货架接口

#### 1. 添加货架
```
POST /api/shelf/add
请求头：Authorization: token
请求体：
{
  "shelfNumber": "A003",
  "floorCount": 5,
  "areaCategory": "图书区",
  "warehouseId": 1
}
```

#### 2. 删除货架
```
DELETE /api/shelf/delete/{id}
请求头：Authorization: token
```

#### 3. 修改货架
```
PUT /api/shelf/update
请求头：Authorization: token
请求体：Shelf对象
```

#### 4. 查询货架
```
GET /api/shelf/get/{id}         # 根据ID查询
GET /api/shelf/list             # 查询所有货架
GET /api/shelf/warehouse/{warehouseId}  # 根据仓库ID查询
GET /api/shelf/page?current=1&size=10&warehouseId=1  # 分页查询
```

### 商品接口

#### 1. 添加商品
```
POST /api/product/add
请求头：Authorization: token
请求体：
{
  "trackingNumber": "TN2025004",
  "name": "键盘",
  "type": "电子产品",
  "stock": 150,
  "unitPrice": 89.00,
  "shelfId": 2,
  "floorNumber": 2
}
```

#### 2. 删除商品
```
DELETE /api/product/delete/{id}
请求头：Authorization: token
```

#### 3. 修改商品
```
PUT /api/product/update
请求头：Authorization: token
请求体：Product对象
```

#### 4. 查询商品
```
GET /api/product/get/{id}       # 根据ID查询
GET /api/product/list           # 查询所有商品
GET /api/product/page?current=1&size=10  # 分页查询
GET /api/product/search?keyword=鼠标  # 模糊搜索
```

### 操作记录接口

```
GET /api/log/list               # 查询所有操作记录
GET /api/log/page?current=1&size=10  # 分页查询
GET /api/log/user/{userId}      # 根据用户ID查询
```

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**状态码说明**：
- 200：成功
- 400：请求参数错误
- 401：未登录或登录过期
- 500：服务器内部错误

## 项目结构

```
Idea/
├── src/
│   └── main/
│       ├── java/com/warehouse/
│       │   ├── WarehouseApplication.java    # 主启动类
│       │   ├── common/                      # 公共类
│       │   │   └── Result.java             # 统一响应结果
│       │   ├── config/                      # 配置类
│       │   │   ├── CorsConfig.java         # 跨域配置
│       │   │   └── WebMvcConfig.java       # MVC配置
│       │   ├── controller/                  # 控制器层
│       │   │   ├── UserController.java
│       │   │   ├── WarehouseController.java
│       │   │   ├── ShelfController.java
│       │   │   ├── ProductController.java
│       │   │   └── OperationLogController.java
│       │   ├── entity/                      # 实体类
│       │   │   ├── User.java
│       │   │   ├── Warehouse.java
│       │   │   ├── Shelf.java
│       │   │   ├── Product.java
│       │   │   ├── OperationLog.java
│       │   │   └── Permission.java
│       │   ├── exception/                   # 异常处理
│       │   │   ├── BusinessException.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── interceptor/                 # 拦截器
│       │   │   └── AuthInterceptor.java    # 认证拦截器
│       │   ├── mapper/                      # 数据访问层
│       │   │   ├── UserMapper.java
│       │   │   ├── WarehouseMapper.java
│       │   │   ├── ShelfMapper.java
│       │   │   ├── ProductMapper.java
│       │   │   ├── OperationLogMapper.java
│       │   │   └── PermissionMapper.java
│       │   ├── service/                     # 服务接口
│       │   │   ├── UserService.java
│       │   │   ├── WarehouseService.java
│       │   │   ├── ShelfService.java
│       │   │   ├── ProductService.java
│       │   │   └── OperationLogService.java
│       │   ├── service/impl/                # 服务实现类
│       │   │   ├── UserServiceImpl.java
│       │   │   ├── WarehouseServiceImpl.java
│       │   │   ├── ShelfServiceImpl.java
│       │   │   ├── ProductServiceImpl.java
│       │   │   └── OperationLogServiceImpl.java
│       │   └── util/                        # 工具类
│       │       └── JwtUtil.java            # JWT工具类
│       └── resources/
│           ├── application.yml              # 配置文件
│           └── sql/
│               └── init.sql                # 数据库初始化脚本
├── pom.xml                                  # Maven配置文件
└── README.md                               # 项目说明文档
```

## 注意事项

1. **密码加密**：所有用户密码使用BCrypt加密存储
2. **事务管理**：涉及多表操作的方法都使用了事务注解
3. **级联删除**：删除仓库/货架前会检查是否有关联数据
4. **自动统计**：仓库的货架数量和存量会自动更新
5. **操作日志**：所有增删改操作都会记录日志
6. **跨域支持**：已配置CORS，支持前端跨域请求

## 前端对接说明

前端开发时需要注意：

1. 登录后将返回的Token存储在localStorage或sessionStorage中
2. 每次请求都需要在请求头中携带Token
3. Token过期时间为24小时，过期后需要重新登录
4. 根据用户角色显示不同的功能菜单

## 联系方式

如有问题，请联系开发团队：
- 毛煜祺：2023433020116
- 聂智天：2023433070228
- 钟昌盛：2023433020118

指导教师：陈权

