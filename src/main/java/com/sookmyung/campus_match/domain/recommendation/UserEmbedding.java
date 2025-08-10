package com.sookmyung.campus_match.domain.recommendation;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 임베딩 데이터 엔티티
 * - AI 추천 알고리즘에서 사용되는 벡터 값 저장
 * - 관심사, 자기소개, 작성한 글 등의 정보를 기반으로 생성됨
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_embeddings")
public class UserEmbedding extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // 해당 임베딩의 소유 사용자

    @Lob
    @Column(nullable = false)
    private String embeddingVector; // 벡터 값 (JSON 또는 공백 구분 문자열 형태)
}
