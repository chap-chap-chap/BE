from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

# DB 연결
MYSQL_HOST = os.getenv("MYSQL_HOST", "mysql_chap_chap")  # 기본값: mysql_chap_chap
MYSQL_PORT = int(os.getenv("MYSQL_PORT", "3306"))
MYSQL_DB   = os.getenv("MYSQL_DATABASE")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PW   = os.getenv("MYSQL_PASSWORD")

if not all([MYSQL_DB, MYSQL_USER, MYSQL_PW]):
    raise RuntimeError("MySQL 환경변수(MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD)가 비어 있습니다.")

DB_URL = f"mysql+pymysql://{MYSQL_USER}:{MYSQL_PW}@{MYSQL_HOST}:{MYSQL_PORT}/{MYSQL_DB}?charset=utf8mb4"

# SQLAlchemy Engine - 죽은 커넥션 자동감지/재생성
engine = create_engine(
    DB_URL,
    pool_pre_ping=True,   # ping으로 죽은 커넥션 감지
    pool_recycle=300,     # 300초 지나면 커넥션 재생성
    pool_size=5,
    max_overflow=5,
    future=True,
)

class NutrientInput(BaseModel):
    nutrient: str
    value: float

@app.post("/check_nutrient")
def check_nutrient(data: NutrientInput):
    try:
        with engine.connect() as conn:
            row = conn.execute(
                text("SELECT * FROM nutrient_standard WHERE nutrient=:nut"),
                {"nut": data.nutrient}
            ).mappings().first()

        if not row:
            return {
                "ok": False,
                "nutrient": data.nutrient,
                "reason": "영양소 정보 없음"
            }

        # 값 비교
        value = data.value
        fediaf_min = row.get('FEDIAF_min')
        fediaf_max = row.get('FEDIAF_max')
        nias_min   = row.get('NIAS_min')

        if fediaf_max is not None and fediaf_min is not None:
            if value < fediaf_min:
                status = "낮음"
            elif value > fediaf_max:
                status = "높음"
            else:
                status = "정상"
        else:
            if nias_min is not None and value < nias_min:
                status = "낮음"
            else:
                status = "정상"

        return {
            "nutrient": data.nutrient,
            "input_value": value,
            "status": status,
            "FEDIAF_min": fediaf_min,
            "FEDIAF_max": fediaf_max,
            "NIAS_min": nias_min
        }
    except HTTPException:
        raise
    except Exception as e:
        # 자바 쪽에서도 500 원인을 바디로 볼 수 있게
        raise HTTPException(status_code=500, detail=f"DB error: {str(e)}")

@app.get("/health")
def health():
    # 헬스체크
    return {"ok": True}
