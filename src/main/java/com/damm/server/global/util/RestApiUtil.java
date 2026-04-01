package com.damm.server.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * 프로젝트 전역에서 외부 REST API와의 통신을 전담하는 공통 유틸리티 클래스다.
 * WebClient를 기반으로 하며, 공공데이터와 같은 까다로운 API의 인코딩 문제와
 * 제네릭 타입 파싱 문제를 모두 해결하도록 설계한다.
 */
@Component
@RequiredArgsConstructor
public class RestApiUtil {

    /**
     * WebClient 인스턴스를 생성하기 위한 빌더 객체다.
     */
    private final WebClient.Builder webClientBuilder;

    // ==========================================
    // GET 요청 메서드 그룹
    // ==========================================

    public <T> T get(String url, Map<String, String> headers, Map<String, String> queryParams, Class<T> responseType) {
        return execute(HttpMethod.GET, createUri(url, queryParams), headers, null, responseType);
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, String> queryParams, ParameterizedTypeReference<T> typeReference) {
        return execute(HttpMethod.GET, createUri(url, queryParams), headers, null, typeReference);
    }

    /**
     * 이미 조립이 완료된 URI 객체를 사용하는 GET 메서드다. (공공데이터 샘플 방식 지원)
     */
    public <T> T get(URI uri, Map<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return execute(HttpMethod.GET, uri, headers, null, typeReference);
    }

    // ==========================================
    // POST 요청 메서드 그룹
    // ==========================================

    public <T> T post(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        return execute(HttpMethod.POST, createUri(url, null), headers, body, responseType);
    }

    public <T> T post(String url, Map<String, String> headers, Object body, ParameterizedTypeReference<T> typeReference) {
        return execute(HttpMethod.POST, createUri(url, null), headers, body, typeReference);
    }

    // ==========================================
    // PUT 요청 메서드 그룹
    // ==========================================

    public <T> T put(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        return execute(HttpMethod.PUT, createUri(url, null), headers, body, responseType);
    }

    public <T> T put(String url, Map<String, String> headers, Object body, ParameterizedTypeReference<T> typeReference) {
        return execute(HttpMethod.PUT, createUri(url, null), headers, body, typeReference);
    }

    // ==========================================
    // DELETE 요청 메서드 그룹
    // ==========================================

    public <T> T delete(String url, Map<String, String> headers, Class<T> responseType) {
        return execute(HttpMethod.DELETE, createUri(url, null), headers, null, responseType);
    }

    public <T> T delete(String url, Map<String, String> headers, ParameterizedTypeReference<T> typeReference) {
        return execute(HttpMethod.DELETE, createUri(url, null), headers, null, typeReference);
    }

    // ==========================================
    // 내부 공통 실행 로직 (Private)
    // ==========================================

    /**
     * 단일 클래스 응답 처리를 위한 내부 실행 메서드다.
     */
    private <T> T execute(HttpMethod method, URI uri, Map<String, String> headers, Object body, Class<T> responseType) {
        return buildWebClient(method, uri, headers, body)
                .bodyToMono(responseType)
                .block();
    }

    /**
     * 제네릭 타입 응답 처리를 위한 내부 실행 메서드다.
     */
    private <T> T execute(HttpMethod method, URI uri, Map<String, String> headers, Object body, ParameterizedTypeReference<T> typeReference) {
        return buildWebClient(method, uri, headers, body)
                .bodyToMono(typeReference)
                .block();
    }

    /**
     * 공공데이터 API의 이중 인코딩 문제를 방어하기 위해 URI를 생성한다.
     */
    private URI createUri(String url, Map<String, String> queryParams) {
        var builder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        // .encode()를 생략하거나 build(true)를 사용하여 인코딩 원본을 최대한 보존한다.
        return builder.build().toUri();
    }

    /**
     * WebClient 인스턴스를 구성하고 요청 스펙을 반환한다.
     * 인코딩 모드를 NONE으로 설정하여 전달받은 URI를 변형하지 않는다.
     */
    private WebClient.ResponseSpec buildWebClient(HttpMethod method, URI uri, Map<String, String> headers, Object body) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        WebClient webClient = webClientBuilder
                .uriBuilderFactory(factory)
                .build();

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(uri)
                .headers(httpHeaders -> {
                    if (headers != null) headers.forEach(httpHeaders::add);
                });

        if (body != null) {
            requestSpec.bodyValue(body);
        }

        return requestSpec.retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("API 외부 통신 에러: " + errorBody))));
    }
}