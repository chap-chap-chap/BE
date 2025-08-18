import pandas as pd
import pymysql
from dotenv import load_dotenv
import os


# MySQL 연결

load_dotenv()  # .env 파일 읽기

conn = pymysql.connect(
    host=os.getenv('MYSQL_HOST', 'localhost'),
    user=os.getenv('MYSQL_USER'),
    password=os.getenv('MYSQL_PASSWORD'),
    db=os.getenv('MYSQL_DATABASE'),
    charset='utf8mb4'
)
cursor = conn.cursor()

cursor.execute("DROP TABLE IF EXISTS nutrient_standard") 

# 테이블 생성
cursor.execute('''
CREATE TABLE IF NOT EXISTS nutrient_standard (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nutrient VARCHAR(100),
    unit VARCHAR(20),
    FEDIAF_min FLOAT,
    FEDIAF_max FLOAT,
    AAFCO FLOAT,
    NIAS_min FLOAT
)
''')

# CSV 읽기
df = pd.read_csv('all_nutri_data.csv')

# NaN 제거 (선택)

df = df.replace({pd.NA: None, pd.NaT: None, float('nan'): None})


# 데이터 삽입
for _, row in df.iterrows():
    cursor.execute('''
    INSERT INTO nutrient_standard (nutrient, unit, AAFCO, NIAS_min, FEDIAF_min, FEDIAF_max)
    VALUES (%s, %s, %s, %s, %s, %s)
    ''', (row['영양소'], row['단위'], row['AAFCO'], row['NIAS최소'], row['FEDIAF_min'], row['FEDIAF_max']))

conn.commit()
cursor.close()
conn.close()

print("DB 초기화 및 데이터 삽입 완료!")
