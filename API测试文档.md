# 仓储管理系统 API 测试文档

## 测试环境

- 基础URL: `http://localhost:8080/api`
- 默认账号：
  - 超级管理员：`admin` / `admin123`
  - 仓管员：`keeper` / `keeper123`
  - 管理员：`employee` / `emp123`

## 推荐测试工具

- Postman
- Apifox
- Swagger (后续可集成)

---

## 测试流程

### 第一步：用户登录

**接口**: `POST /api/user/login`

**请求参数**:
```
username=admin&password=admin123
```

**cURL命令**:
```bash
curl -X POST "http://localhost:8080/api/user/login?username=admin&password=admin123"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": 0
    }
  }
}
```

**重要**：将返回的token保存，后续所有请求都需要在请求头中携带：
```
Authorization: eyJhbGciOiJIUzUxMiJ9...
```

---

### 第二步：测试仓库管理

#### 1. 查询所有仓库

```bash
curl -X GET "http://localhost:8080/api/warehouse/list" \
  -H "Authorization: your_token_here"
```

#### 2. 添加仓库

```bash
curl -X POST "http://localhost:8080/api/warehouse/add" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "warehouseNumber": "WH003",
    "address": "深圳市南山区仓储中心C栋"
  }'
```

#### 3. 修改仓库

```bash
curl -X PUT "http://localhost:8080/api/warehouse/update" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "warehouseNumber": "WH001",
    "address": "北京市朝阳区仓储中心A栋（已更新）"
  }'
```

#### 4. 查询单个仓库

```bash
curl -X GET "http://localhost:8080/api/warehouse/get/1" \
  -H "Authorization: your_token_here"
```

#### 5. 分页查询仓库

```bash
curl -X GET "http://localhost:8080/api/warehouse/page?current=1&size=10" \
  -H "Authorization: your_token_here"
```

---

### 第三步：测试货架管理

#### 1. 查询所有货架

```bash
curl -X GET "http://localhost:8080/api/shelf/list" \
  -H "Authorization: your_token_here"
```

#### 2. 添加货架

```bash
curl -X POST "http://localhost:8080/api/shelf/add" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "shelfNumber": "A003",
    "floorCount": 5,
    "areaCategory": "图书区",
    "warehouseId": 1
  }'
```

#### 3. 根据仓库ID查询货架

```bash
curl -X GET "http://localhost:8080/api/shelf/warehouse/1" \
  -H "Authorization: your_token_here"
```

#### 4. 修改货架

```bash
curl -X PUT "http://localhost:8080/api/shelf/update" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "shelfNumber": "A001",
    "floorCount": 6,
    "areaCategory": "办公用品区",
    "warehouseId": 1
  }'
```

---

### 第四步：测试商品管理

#### 1. 查询所有商品

```bash
curl -X GET "http://localhost:8080/api/product/list" \
  -H "Authorization: your_token_here"
```

#### 2. 添加商品

```bash
curl -X POST "http://localhost:8080/api/product/add" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "TN2025010",
    "name": "机械键盘",
    "type": "电子产品",
    "stock": 80,
    "unitPrice": 299.00,
    "shelfId": 2,
    "floorNumber": 3
  }'
```

#### 3. 商品搜索（模糊查询）

```bash
# 按商品名称搜索
curl -X GET "http://localhost:8080/api/product/search?keyword=键盘" \
  -H "Authorization: your_token_here"

# 按运单ID搜索
curl -X GET "http://localhost:8080/api/product/search?keyword=TN2025" \
  -H "Authorization: your_token_here"
```

#### 4. 修改商品

```bash
curl -X PUT "http://localhost:8080/api/product/update" \
  -H "Authorization: your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "trackingNumber": "TN2025001",
    "name": "中性笔（0.5mm）",
    "type": "文具",
    "stock": 600,
    "unitPrice": 2.80,
    "shelfId": 1,
    "floorNumber": 1
  }'
```

#### 5. 删除商品

```bash
curl -X DELETE "http://localhost:8080/api/product/delete/1" \
  -H "Authorization: your_token_here"
```

---

### 第五步：测试操作日志

#### 1. 查询所有操作记录

```bash
curl -X GET "http://localhost:8080/api/log/list" \
  -H "Authorization: your_token_here"
```

#### 2. 分页查询操作记录

```bash
curl -X GET "http://localhost:8080/api/log/page?current=1&size=10" \
  -H "Authorization: your_token_here"
```

#### 3. 查询指定用户的操作记录

```bash
curl -X GET "http://localhost:8080/api/log/user/1" \
  -H "Authorization: your_token_here"
```

---

### 第六步：测试用户管理（仅超级管理员）

#### 1. 查询所有用户

```bash
curl -X GET "http://localhost:8080/api/user/list" \
  -H "Authorization: your_admin_token_here"
```

#### 2. 添加用户

```bash
curl -X POST "http://localhost:8080/api/user/add" \
  -H "Authorization: your_admin_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testkeeper",
    "password": "123456",
    "realName": "测试仓管员",
    "gender": "男",
    "phone": "13900139999",
    "idCard": "110000199801011234",
    "role": 2
  }'
```

#### 3. 根据角色查询用户

```bash
# 查询所有仓管员 (role=2)
curl -X GET "http://localhost:8080/api/user/role/2" \
  -H "Authorization: your_token_here"

# 查询所有管理员 (role=1)
curl -X GET "http://localhost:8080/api/user/role/1" \
  -H "Authorization: your_token_here"
```

#### 4. 修改用户信息

```bash
curl -X PUT "http://localhost:8080/api/user/update" \
  -H "Authorization: your_admin_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 2,
    "username": "keeper",
    "realName": "张三（高级仓管员）",
    "gender": "男",
    "phone": "13900139001",
    "idCard": "110000199502021234",
    "role": 2
  }'
```

#### 5. 删除用户

```bash
curl -X DELETE "http://localhost:8080/api/user/delete/4" \
  -H "Authorization: your_admin_token_here"
```

---

## 权限测试场景

### 场景1：仓管员测试

1. 使用仓管员账号登录：`keeper` / `keeper123`
2. 测试可以执行的操作：
   - ✅ 增删改查仓库
   - ✅ 增删改查货架
   - ✅ 增删改查商品
   - ✅ 查看操作记录
3. 测试不能执行的操作：
   - ❌ 添加/删除用户（应返回权限错误）

### 场景2：管理员/员工测试

1. 使用管理员账号登录：`employee` / `emp123`
2. 测试可以执行的操作：
   - ✅ 查询商品
   - ✅ 搜索商品
   - ✅ 查看操作记录
3. 测试不能执行的操作：
   - ❌ 增删改仓库/货架/商品（需要在代码中添加权限检查）
   - ❌ 管理用户

### 场景3：删除规则测试

#### 测试1：删除有货架的仓库
```bash
# 应该返回错误：该仓库下还有货架，无法删除
curl -X DELETE "http://localhost:8080/api/warehouse/delete/1" \
  -H "Authorization: your_token_here"
```

#### 测试2：删除有商品的货架
```bash
# 应该返回错误：该货架上还有商品，无法删除
curl -X DELETE "http://localhost:8080/api/shelf/delete/1" \
  -H "Authorization: your_token_here"
```

#### 测试3：正确的删除顺序
```bash
# 1. 先删除商品
curl -X DELETE "http://localhost:8080/api/product/delete/1" \
  -H "Authorization: your_token_here"

# 2. 再删除货架
curl -X DELETE "http://localhost:8080/api/shelf/delete/1" \
  -H "Authorization: your_token_here"

# 3. 最后删除仓库
curl -X DELETE "http://localhost:8080/api/warehouse/delete/1" \
  -H "Authorization: your_token_here"
```

---

## 常见问题

### 1. Token过期
**现象**：返回401错误，提示"登录已过期"  
**解决**：重新调用登录接口获取新Token

### 2. 权限不足
**现象**：返回500错误，提示"无权限"  
**解决**：使用具有相应权限的账号登录

### 3. 参数错误
**现象**：返回400错误  
**解决**：检查请求参数是否正确，Content-Type是否为application/json

### 4. 外键约束错误
**现象**：删除失败，提示有关联数据  
**解决**：先删除子表数据，再删除父表数据

---

## Postman导入配置

可以将以下JSON保存为`warehouse-api.postman_collection.json`导入Postman：

```json
{
  "info": {
    "name": "仓储管理系统API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    },
    {
      "key": "token",
      "value": ""
    }
  ]
}
```

将登录后获取的token设置到环境变量`{{token}}`中，后续请求会自动使用。

---

## 测试建议

1. 按照上述顺序依次测试各模块功能
2. 测试完整的业务流程：创建仓库→添加货架→录入商品→查询→修改→删除
3. 测试权限控制：使用不同角色的账号测试相同操作
4. 测试边界情况：空数据、重复数据、不存在的ID
5. 查看操作日志，验证所有操作都被正确记录

---

## 测试完成检查清单

- [ ] 用户登录功能正常
- [ ] Token认证机制工作正常
- [ ] 仓库增删改查功能正常
- [ ] 货架增删改查功能正常
- [ ] 商品增删改查功能正常
- [ ] 商品搜索功能正常（支持模糊查询）
- [ ] 操作日志记录正常
- [ ] 用户管理功能正常（超级管理员）
- [ ] 权限控制正常（不同角色）
- [ ] 删除规则正常（级联检查）
- [ ] 仓库货架数量自动更新
- [ ] 仓库存量自动计算
- [ ] 分页查询功能正常
- [ ] 异常处理正常（返回友好的错误提示）

