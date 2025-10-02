#!/usr/bin/env bash
set -euo pipefail

if command -v git >/dev/null 2>&1; then
  git pull || true
fi

if [ -f "backend/gradlew" ]; then
  (cd backend && ./gradlew clean bootJar)
else
  echo "⚠️ 未找到 gradlew，请确保已在 backend 目录构建出 JAR"
fi

docker compose up -d --build

docker image prune -f
echo "✅ 部署完成"
