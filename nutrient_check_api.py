from fastapi import FastAPI
from pydantic import BaseModel
import pymysql
import os
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

# DB 연결
conn = pymysql.connect(
    host='localhost',
    user=os.getenv('MYSQL_USER'),
    password=os.getenv('MYSQL_PASSWORD'),
    db=os.getenv('MYSQL_DATABASE'),
    charset='utf8mb4',
    cursorclass=pymysql.cursors.DictCursor
)

class NutrientInput(BaseModel):
    nutrient: str
    value: float

@app.post("/check_nutrient")
def check_nutrient(data: NutrientInput):
    with conn.cursor() as cursor:
        cursor.execute("SELECT * FROM nutrient_standard WHERE nutrient=%s", (data.nutrient,))
        row = cursor.fetchone()
        if not row:
            return {"result": "영양소 정보 없음"}

        # 값 비교
        value = data.value
        fediaf_min = row.get('FEDIAF_min')
        fediaf_max = row.get('FEDIAF_max')
        nias_min = row.get('NIAS_min')

        if fediaf_max is not None:
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


