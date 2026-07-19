API 명세서(Render) : https://smoking-area-21yb.onrender.com/swagger-ui/index.html

## 로컬 실행 (Render DB 사용)

1. `.env.local.example`을 복사해 `.env.local` 생성 후 값 입력
2. 실행
   ```bash
   bash scripts/run-local.sh
   ```
3. 확인
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## ngrok으로 외부 공유

백엔드가 8080에서 실행 중일 때:

```bash
ngrok http 8080
```

발급된 URL로 접속:
- `https://<your-ngrok>.ngrok-free.dev/swagger-ui/index.html`
- `https://<your-ngrok>.ngrok-free.dev/admin/index.html`

## 관리자 페이지 사용 주의

관리자 페이지의 `관리자 계정 ID`는 **카카오 ID가 아니라 users 테이블의 id**입니다.
현재 DB 기준으로는 `1` 또는 `2`를 사용하면 됩니다.
