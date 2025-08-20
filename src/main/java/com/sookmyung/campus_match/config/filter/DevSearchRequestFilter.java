package com.sookmyung.campus_match.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Dev 환경에서 검색 요청의 한글 키워드 URL 인코딩 문제를 해결하는 Filter
 * WHY: dev 환경에서 한글 키워드가 URL 인코딩되지 않아 Tomcat에서 400 에러가 발생하는 문제 해결
 */
@Slf4j
@Component
@Profile("dev")
@Order(1) // 다른 필터보다 먼저 실행
public class DevSearchRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // 검색 API 요청인지 확인
        if (requestURI.startsWith("/api/search/")) {
            log.debug("Dev 환경 - 검색 요청 필터 적용: {}", requestURI);
            
            // 한글 키워드 URL 인코딩 처리
            HttpServletRequest wrappedRequest = new SearchRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            // 검색 API가 아니면 그대로 통과
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 검색 요청을 래핑하여 한글 키워드를 URL 인코딩하는 Wrapper
     */
    private static class SearchRequestWrapper extends HttpServletRequestWrapper {
        
        private final Map<String, String[]> parameterMap;
        
        public SearchRequestWrapper(HttpServletRequest request) {
            super(request);
            this.parameterMap = new HashMap<>();
            
            // 기존 파라미터를 복사하고 keyword 파라미터만 URL 인코딩 처리
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                
                if ("keyword".equals(paramName) && paramValues != null && paramValues.length > 0) {
                    // keyword 파라미터 URL 인코딩 처리
                    String[] encodedValues = new String[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        String value = paramValues[i];
                        if (value != null && !value.trim().isEmpty()) {
                            try {
                                // 한글이 포함된 경우 URL 인코딩
                                if (value.matches(".*[가-힣].*")) {
                                    encodedValues[i] = URLEncoder.encode(value, StandardCharsets.UTF_8);
                                    log.debug("Dev 환경 - 키워드 URL 인코딩: {} -> {}", value, encodedValues[i]);
                                } else {
                                    encodedValues[i] = value;
                                }
                            } catch (Exception e) {
                                log.warn("Dev 환경 - 키워드 URL 인코딩 실패: {}", value, e);
                                encodedValues[i] = value;
                            }
                        } else {
                            encodedValues[i] = value;
                        }
                    }
                    this.parameterMap.put(paramName, encodedValues);
                } else {
                    // keyword가 아닌 파라미터는 그대로 복사
                    this.parameterMap.put(paramName, paramValues);
                }
            }
        }
        
        @Override
        public String getParameter(String name) {
            String[] values = parameterMap.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        }
        
        @Override
        public String[] getParameterValues(String name) {
            return parameterMap.get(name);
        }
        
        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(parameterMap);
        }
        
        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(parameterMap.keySet());
        }
    }
}
