# 사료 영양소 분석 API 안내 문서

## 1️. API 

- **기능**: 입력된 사료 데이터를 분석하여 영양소 정보 제공
- **백엔드**: FastAPI (Python)
- **Java 호출**: `PythonApiClient.java`에서 API 호출 가능

## 2️. 엔드포인트

| 메서드  | URL               | 설명                      |
| ---- | ----------------- | ----------------------- |
| POST | `/check_nutrient` | 입력된 영양소 데이터를 분석하고 상태 반환 |

### Request ex (JSON)

```json
{
  "nutrient": "단백질",
  "value": 25.0
}
```

### Response ex (JSON)

```json
{
  "nutrient": "단백질",
  "input_value": 25.0,
  "status": "정상",
  "FEDIAF_min": 18.0,
  "FEDIAF_max": 30.0,
  "NIAS_min": 20.0
}
```

## 3️. Java에서 호출 예시 (`PythonApiClient.java`)

```java
// 기존 작성한 Java API 클라이언트 코드 그대로 사용
```

## 4️. 

1. FastAPI 서버가 먼저 실행되어야 함 (`uvicorn api:app --reload`)
2. 요청/응답 JSON 구조 그대로 사용
3. Swagger UI는 참고용, Java 호출 시 사용하지 않아도 됨

