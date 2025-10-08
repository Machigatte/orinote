#!/bin/bash
set -e

# 传入版本号作为参数
VERSION="$1"
# 可选参数：第二个参数可以是 --prerelease，用于标记本次发布为预发布，优先级低于环境变量 DISPATCH_PRERELEASE
if [ "${2}" = "--prerelease" ] ; then
	# 如果调用方传入 --prerelease，设置 DISPATCH_PRERELEASE 为 true（调用处可通过 DISPATCH_PRERELEASE 覆盖）
	DISPATCH_PRERELEASE=true
fi

# 更新 gradle.properties 版本号
echo "version=${VERSION}" > gradle.properties

# 构建前端
# 通过环境变量控制：
#   FRONTEND_OWNER - 仓库拥有者
#   FRONTEND_REPO  - 仓库名
#   GITHUB_TOKEN   - Personal Access Token，用于授权（需要 repo:dispatch 权限）
#   DISPATCH_ON_PUSH - 若设为 'true' 则触发（默认不触发）
#   DISPATCH_EVENT_TYPE - 事件类型，默认 backend-release
#   DISPATCH_PRERELEASE - 是否标记为 prerelease，默认 false
#   DISPATCH_RELEASE_NOTES - 发布说明文本
if [ -z "${GITHUB_TOKEN}" ] || [ -z "${FRONTEND_OWNER}" ] || [ -z "${FRONTEND_REPO}" ] ; then
	echo "GITHUB_TOKEN/FRONTEND_OWNER/FRONTEND_REPO not set. Skipping dispatch."
else
	EVENT_TYPE=${DISPATCH_EVENT_TYPE:-backend-release}
	PRERELEASE=${DISPATCH_PRERELEASE:-false}
	RELEASE_NOTES=${DISPATCH_RELEASE_NOTES:-""}
	PAYLOAD="{\"event_type\":\"${EVENT_TYPE}\",\"client_payload\":{\"version\":\"${VERSION}\",\"prerelease\":${PRERELEASE},\"release_notes\":\"${RELEASE_NOTES}\"}}"
	echo "Triggering repository_dispatch to ${FRONTEND_OWNER}/${FRONTEND_REPO} with event ${EVENT_TYPE}"
	curl -X POST \
		-H "Accept: application/vnd.github.v3+json" \
		-H "Authorization: token ${GITHUB_TOKEN}" \
		"https://api.github.com/repos/${FRONTEND_OWNER}/${FRONTEND_REPO}/dispatches" \
		-d "$PAYLOAD"
fi

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
