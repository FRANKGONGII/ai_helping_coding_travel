# 使用官方 Node.js 镜像作为构建环境
FROM node:16 as build-stage

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制所有前端文件
COPY . .

# 构建 Vue.js 应用
# VUE_APP_BAIDU_MAP_AK 环境变量需要在构建时传入
ARG VUE_APP_BAIDU_MAP_AK
ENV VUE_APP_BAIDU_MAP_AK=$VUE_APP_BAIDU_MAP_AK
RUN npm run build

# 第二阶段：使用 Nginx 提供静态文件
FROM nginx:stable-alpine as production-stage

# 复制构建好的静态文件到 Nginx 默认的静态文件目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 复制自定义的 Nginx 配置（如果需要）
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 Nginx 默认的 80 端口
EXPOSE 80

# 启动 Nginx 服务器
CMD ["nginx", "-g", "daemon off;"]