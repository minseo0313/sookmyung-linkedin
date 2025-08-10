package com.sookmyung.campus_match.util.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 승인된 사용자(학생 B)만 접근 가능한 메서드를 표시하는 어노테이션
 * - 학생 A(PENDING)는 해당 메서드에 접근할 수 없음
 * - AOP를 통해 권한 체크 수행
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresApproval {
    
    /**
     * 권한이 없을 때 반환할 에러 메시지
     */
    String message() default "승인된 사용자만 접근할 수 있습니다.";
}
