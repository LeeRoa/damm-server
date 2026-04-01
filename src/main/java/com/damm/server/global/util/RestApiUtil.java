package com.damm.server.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RestApiUtil {

    private final WebClient.Builder webClientBuilder;

    /**
     * GET 요청
     */
    public <T> T get(String url, Map<String, String> headers, Map<String, String> queryParams, Class<T> responseType) {
        return execute(HttpMethod.GET, url, headers, queryParams, null, responseType);
    }

    /**
     * POST 요청
     */
    public <T> T post(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        return execute(HttpMethod.POST, url, headers, null, body, responseType);
    }

    /**
     * PUT 요청
     */
    public <T> T put(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        return execute(HttpMethod.PUT, url, headers, null, body, responseType);
    }

    /**
     * DELETE 요청
     */
    public <T> T delete(String url, Map<String, String> headers, Class<T> responseType) {
        return execute(HttpMethod.DELETE, url, headers, null, null, responseType);
    }

    /**
     * 공통 실행 메서드 (내부용)
     */
    private <T> T execute(HttpMethod method, String url, Map<String, String> headers,
                          Map<String, String> queryParams, Object body, Class<T> responseType) {

        return webClientBuilder.build()
                .method(method)
                .uri(uriBuilder -> {
                    // 1. path(url) 대신 fromHttpUrl(url) 사용 (전체 URL 인식)
                    var builder = UriComponentsBuilder.fromUriString(url);

                    // 2. 쿼리 파라미터 추가
                    if (queryParams != null) {
                        queryParams.forEach(builder::queryParam);
                    }

                    // 3. 반드시 .encode()를 호출하여 한글 주소를 안전하게 변환
                    return builder.build().encode().toUri();
                })
                .headers(httpHeaders -> {
                    if (headers != null) headers.forEach(httpHeaders::add);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("API 에러: " + errorBody))))
                .bodyToMono(responseType)
                .block();
    }
}