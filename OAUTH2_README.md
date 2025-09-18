# GitHub OAuth2 私有认证配置说明

## 概述

本项目实现了基于 GitHub OAuth2 的私有认证系统。只有预先在本地数据库中注册的 GitHub 用户才能登录系统。

## 功能特性

- ✅ GitHub OAuth2 认证
- ✅ 本地用户白名单控制
- ✅ 角色权限管理 (ROLE_USER, ROLE_ADMIN)
- ✅ 用户启用/禁用功能
- ✅ 管理员 API 接口
- ✅ 错误处理和用户友好提示

## 环境配置

### 1. GitHub OAuth App 设置

1. 访问 GitHub Settings > Developer settings > OAuth Apps
2. 创建新的 OAuth App，设置：
   - Application name: `Orinote`
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`

3. 记录 Client ID 和 Client Secret

### 2. 环境变量配置

```bash
export GITHUB_CLIENT_ID=your_github_client_id
export GITHUB_CLIENT_SECRET=your_github_client_secret
export DB_URL=jdbc:postgresql://localhost:5432/orinote
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

### 3. 数据库初始化

应用启动时会自动执行 Flyway 迁移，创建必要的表：
- `users` - 本地用户表
- `user_oauth_accounts` - OAuth 账号绑定表

## 使用说明

### 管理员操作

#### 1. 创建用户

```bash
curl -X POST http://localhost:8080/admin/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "role": "ROLE_USER",
    "enabled": true
  }'
```

#### 2. 绑定 GitHub 账号

首先获取 GitHub 用户 ID：访问 `https://api.github.com/users/{github_username}` 查看 `id` 字段。

```bash
curl -X POST http://localhost:8080/admin/users/{userId}/bind-github \
  -H "Content-Type: application/json" \
  -d '{
    "providerUserId": "12345678"
  }'
```

#### 3. 启用/禁用用户

```bash
# 禁用用户
curl -X PATCH http://localhost:8080/admin/users/{userId}/disable

# 启用用户
curl -X PATCH http://localhost:8080/admin/users/{userId}/enable
```

#### 4. 查看所有用户

```bash
curl http://localhost:8080/admin/users
```

### 用户操作

#### 获取当前用户信息

```bash
curl http://localhost:8080/api/me
```

## 登录流程

1. 访问 `http://localhost:8080/login`
2. 点击 "使用 GitHub 登录"
3. 系统检查：
   - GitHub 用户 ID 是否在 `user_oauth_accounts` 表中
   - 对应的本地用户是否存在且启用
4. 登录成功后跳转到主页面

## 错误处理

### 常见错误情况

- **unauthorized_github_user**: GitHub 账号未注册
- **account_disabled**: 账号已被禁用
- **user_not_found**: OAuth 绑定的本地用户不存在

### 错误页面

登录失败时会重定向到 `/login?error={errorCode}` 并显示相应的中文错误信息。

## API 文档

### 管理员 API

| 方法 | 路径 | 描述 | 权限要求 |
|------|------|------|----------|
| POST | `/admin/users` | 创建用户 | ROLE_ADMIN |
| GET | `/admin/users` | 获取所有用户 | ROLE_ADMIN |
| GET | `/admin/users/{id}` | 获取单个用户 | ROLE_ADMIN |
| PUT | `/admin/users/{id}` | 更新用户 | ROLE_ADMIN |
| PATCH | `/admin/users/{id}/disable` | 禁用用户 | ROLE_ADMIN |
| PATCH | `/admin/users/{id}/enable` | 启用用户 | ROLE_ADMIN |
| POST | `/admin/users/{id}/bind-github` | 绑定 GitHub | ROLE_ADMIN |
| DELETE | `/admin/users/{id}/github-binding` | 解绑 GitHub | ROLE_ADMIN |

### 用户 API

| 方法 | 路径 | 描述 | 权限要求 |
|------|------|------|----------|
| GET | `/api/me` | 获取当前用户信息 | 已认证 |

## 测试

```bash
# 运行所有测试
./gradlew test

# 构建项目
./gradlew build
```

## 安全注意事项

1. **GitHub Client Secret** 必须妥善保管，不得泄露
2. **数据库连接信息** 应通过环境变量配置
3. 只有具有 `ROLE_ADMIN` 角色的用户可以管理其他用户
4. 用户必须预先注册才能登录，系统不会自动创建用户

## 故障排除

### 1. OAuth2 认证失败

检查：
- GitHub OAuth App 配置是否正确
- Client ID/Secret 是否正确设置
- 回调 URL 是否匹配

### 2. 用户无法登录

检查：
- 用户是否在 `users` 表中存在
- GitHub 账号是否在 `user_oauth_accounts` 表中绑定
- 用户状态是否为启用 (`enabled = true`)

### 3. 权限问题

检查：
- 用户角色是否正确设置
- SecurityConfig 中的权限配置是否正确