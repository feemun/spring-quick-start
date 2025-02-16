# 使用官方的OpenJDK基础镜像，基于slim变体以减少镜像大小
FROM openjdk:17-jdk-slim

# 设置工作目录，有助于组织容器内的文件结构
WORKDIR /app

# 创建conf目录
RUN mkdir -p /app/configs

# 创建非root用户和组，并设置其为主
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# 将jar包复制到容器的工作目录中
COPY ./admin/target/admin-oxygen.jar /app/admin-oxygen.jar

# 更改所有者为非root用户
RUN chown -R appuser:appgroup /app

# 切换到非root用户运行
USER appuser

# 暴露应用运行所需的端口
EXPOSE 8080

# 运行Java应用，添加-Djava.security.egd参数加速启动时的加密操作
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dlogging.path=/app/log", "-jar", "/app/admin-oxygen.jar"]

# 维护者信息
LABEL maintainer="catfish"