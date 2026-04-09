# 智慧医疗平台 (Med-Platform)

## 项目结构

```
med-platform/
├── smart-medical-server/   # 后端 (Spring Boot)
├── med-platform/           # 前端 (Vue 3)
└── docker-compose.yml      # Docker 部署配置
```

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 + Java 21 + MyBatis-Plus + Redis + JWT |
| 前端 | Vue 3 + TypeScript + Vite + Element Plus |
| 数据库 | MySQL |
| 部署 | Docker Compose |

## 环境要求

- JDK 21+
- Node.js 20.19+ 或 22.12+
- MySQL 8.0+
- Redis 7.0+

## 快速启动

### 后端

```bash
cd smart-medical-server
./mvnw spring-boot:run
```

服务地址：`http://localhost:8080`

### 前端

```bash
cd med-platform
npm install
npm run dev
```

访问地址：`http://localhost:5173`

### Docker 部署

```bash
docker-compose up -d
```

## 开发规范

1. **分支管理**：从 `main` 创建功能分支，完成后提交 PR
2. **提交信息**：使用中文，格式 `[类型] 描述`，如 `[feat] 新增预约功能`
3. **代码风格**：后端遵循阿里巴巴规范，前端使用 ESLint + Prettier

## 常用命令

```bash
# 前端构建
npm run build

# 后端打包
./mvnw clean package -DskipTests

# 代码检查
npm run lint
```