#!/bin/bash
set -e

# 传入版本号作为环境变量
VERSION="$1"

# 更新 gradle.properties 版本号
echo "version=${VERSION}" > gradle.properties

# 构建前端
git submodule update --init --recursive
cd src/main/frontend
npm install
npm run build
cd ../../..
mv src/main/frontend/out/* src/main/resources/static/

# 构建后端
chmod +x gradlew
./gradlew build

# 构建 Docker 镜像
docker build -t orinote:${VERSION} .

# 镜像打 tag
docker tag orinote:${VERSION} chalkim/orinote:${VERSION}
docker tag orinote:${VERSION} chalkim/orinote:latest

# 推送镜像
docker push chalkim/orinote:${VERSION}
docker push chalkim/orinote:latest
