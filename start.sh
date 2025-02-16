#!/bin/bash

# 设置镜像名称和容器名称
IMAGE_NAME="map:oxygen"
CONTAINER_NAME="map"

# 定义宿主机上的日志目录
HOST_LOG_DIR="/home/catfish/project/map/logs"
HOST_CONFIG_DIR="/home/catfish/project/map/configs"

# 创建宿主机上的日志目录
mkdir -p ${HOST_LOG_DIR}
mkdir -p ${HOST_CONFIG_DIR}

# 运行容器
docker run -d \
    --name ${CONTAINER_NAME} \
    -p 18080:8080 \
    -v ${HOST_LOG_DIR}:/app/logs \
    -v ${HOST_CONFIG_DIR}:/app/configs \
    -e SPRING_CONFIG_LOCATION=file:/app/configs/application.yml \
    -e JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom" \
    ${IMAGE_NAME}

echo "Started container ${CONTAINER_NAME} with log directory mounted at ${HOST_LOG_DIR}"