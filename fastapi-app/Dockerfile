FROM python:3.10-slim

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

# FastAPI 실행 (포트 8000)
CMD ["sh", "-c", "python init_nutrient_db.py && uvicorn nutrient_check_api:app --host 0.0.0.0 --port 8000"]
