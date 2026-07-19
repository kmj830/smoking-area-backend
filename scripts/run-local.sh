#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

if [[ ! -f ".env.local" ]]; then
  echo "[ERROR] .env.local 파일이 없습니다. .env.local.example을 복사해서 값을 채워주세요."
  exit 1
fi

set -a
source .env.local
set +a

./gradlew bootRun --no-daemon
