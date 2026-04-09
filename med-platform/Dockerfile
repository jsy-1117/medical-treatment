# ========== 构建阶段 ==========
FROM node:20-alpine AS builder
WORKDIR /app

# 先复制 package.json 安装依赖
COPY package*.json ./
RUN npm ci

# 复制源码并构建
COPY . .
RUN npm run build-only

# ========== Nginx 服务阶段 ==========
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]