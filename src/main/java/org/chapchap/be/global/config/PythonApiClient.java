package org.chapchap.be.global.config;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PythonApiClient {

    private final String baseUrl;

    // 생성자: FastAPI 서버 주소
    public PythonApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 영양소 값 체크 API 호출
     *
     * @param nutrient 영양소 이름
     * @param value    입력 값
     * @return 서버 응답 JSON 문자열
     */
    public String checkNutrient(String nutrient, double value) {
        String endpoint = baseUrl + "/check_nutrient";
        String jsonInput = String.format("{\"nutrient\": \"%s\", \"value\": %f}", nutrient, value);

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 읽기
            try (java.util.Scanner scanner = new java.util.Scanner(conn.getInputStream(), "UTF-8")) {
                String response = scanner.useDelimiter("\\A").next();
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 테스트용 main
    public static void main(String[] args) {
        PythonApiClient client = new PythonApiClient("http://fastapi_chap_chap:8000");
        String response = client.checkNutrient("단백질", 50.0);
        System.out.println("서버 응답: " + response);
    }
}
