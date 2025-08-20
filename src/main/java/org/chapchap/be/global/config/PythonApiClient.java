package org.chapchap.be.global.config;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PythonApiClient {

    private final String baseUrl;

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
        String jsonInput = String.format("{\"nutrient\":\"%s\",\"value\":%s}", nutrient, value);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000); // 3s
            conn.setReadTimeout(5000);    // 5s
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();

            // 2xx면 정상 스트림, 아니면 에러 스트림
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            if (is == null) {
                // 에러 스트림이 null일 수도 있음
                throw new RuntimeException("FastAPI returned " + code + " with empty body");
            }

            try (java.util.Scanner sc = new java.util.Scanner(is, StandardCharsets.UTF_8)) {
                String body = sc.useDelimiter("\\A").hasNext() ? sc.next() : "";
                if (code >= 200 && code < 300) {
                    return body;
                } else {
                    throw new RuntimeException("FastAPI error " + code + ": " + body);
                }
            }
        } catch (Exception e) {
            // 여기서 null 리턴하지 말고 예외로 올려서 컨트롤러의 글로벌 에러 핸들링을 타게 하세요
            throw new RuntimeException("Failed to call FastAPI: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // 테스트용 main
    public static void main(String[] args) {
        PythonApiClient client = new PythonApiClient("http://fastapi:8000");
        String response = client.checkNutrient("단백질", 50.0);
        System.out.println("서버 응답: " + response);
    }
}
