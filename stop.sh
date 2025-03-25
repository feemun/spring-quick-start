#!/bin/bash

# 设置容器名称
CONTAINER_NAME="map"

echo "Stopping and removing container ${CONTAINER_NAME}..."

# 检查容器是否正在运行
if docker ps --format '{{.Names}}' | grep -w "${CONTAINER_NAME}" > /dev/null; then
    # 容器正在运行，先停止它
    docker stop ${CONTAINER_NAME}
    echo "${CONTAINER_NAME} stopped."
elif docker ps -a --format '{{.Names}}' | grep -w "${CONTAINER_NAME}" > /dev/null; then
    # 容器存在但未运行
    echo "${CONTAINER_NAME} is not running."
else
    # 容器不存在
    echo "Container ${CONTAINER_NAME} does not exist."
    exit 0
fi

# 根据需求决定是否删除容器
read -p "Do you want to remove the container ${CONTAINER_NAME}? [y/N] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    docker rm ${CONTAINER_NAME}
    echo "Container ${CONTAINER_NAME} removed."
else
    echo "Container ${CONTAINER_NAME} was not removed."
fi