# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim as build

# 设置工作目录
WORKDIR /app

# 复制 Maven 的 pom.xml 文件，这样 Docker 可以在不重新下载依赖的情况下缓存构建步骤
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw
# 构建 Spring Boot 应用
RUN ./mvnw clean package -DskipTests

# 第二阶段：创建一个更小的运行时镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 暴露应用程序运行的端口
EXPOSE 8080

# 定义 DEEPSEEK_API_KEY 环境变量，在运行容器时需要设置
ENV DEEPSEEK_API_KEY=""
ENV DB_PASSWORD=""

# 运行应用程序
ENTRYPOINT ["java","-jar","app.jar"]