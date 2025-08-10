package com.sookmyung.campus_match.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * Page<T> 또는 Slice<T> 기반의 공통 페이징 응답 DTO.
 * - content: 데이터 리스트
 * - page / size: 요청 페이지/사이즈
 * - totalElements / totalPages: Page일 경우만 설정 (Slice는 null)
 * - hasNext / hasPrevious: 다음/이전 페이지 여부
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    /** 현재 페이지 번호 (0-base) */
    private int page;

    /** 페이지당 항목 수 */
    private int size;

    /** 전체 데이터 개수 (Page일 경우만 설정) */
    private Long totalElements;

    /** 전체 페이지 수 (Page일 경우만 설정) */
    private Integer totalPages;

    /** 다음 페이지 존재 여부 */
    private boolean hasNext;

    /** 이전 페이지 존재 여부 */
    private boolean hasPrevious;

    /** 실제 데이터 목록 */
    private List<T> content;

    // --- Factory methods ---

    /**
     * Page<T> 기반 응답 생성
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .content(page.getContent())
                .build();
    }

    /**
     * Slice<T> 기반 응답 생성 (totalElements/totalPages 없음)
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Slice<T> slice) {
        return PageResponse.<T>builder()
                .page(slice.getNumber())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .hasPrevious(slice.hasPrevious())
                .content(slice.getContent())
                .build();
    }
}
