#!/bin/bash
set -e

# 传入版本号作为参数
VERSION=$1
PRERELEASE=$2

# 检查是否提供了版本号
if [ -z "$VERSION" ]; then
  echo "Error: 版本号未提供"
  exit 1
fi

# 打印版本号和预发布标志
echo "准备发布版本: $VERSION"
if [ -n "$PRERELEASE" ]; then
  echo "这是一个预发布版本"
fi

# 更新 gradle.properties 版本号
echo "version=${VERSION}" > gradle.properties

# 构建后端
chmod +x gradlew
./gradlew build

# 构建 Docker 镜像
docker build -t orinote:${VERSION} .

# 镜像打 tag
docker tag orinote:${VERSION} chalkim/orinote:${VERSION}
if [ -z "$PRERELEASE" ]; then
  docker tag orinote:${VERSION} chalkim/orinote:latest
fi

# 推送镜像
docker push chalkim/orinote:${VERSION}
if [ -n "$PRERELEASE" ]; then
  docker push chalkim/orinote:latest
fi
