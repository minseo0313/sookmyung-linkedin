package com.sookmyung.campus_match.domain.recommendation;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_embeddings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmbedding extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "embedding_vector", columnDefinition = "TEXT")
    private String embeddingVector;
    
    // TODO: DB가 VECTOR 타입을 지원한다면 columnDefinition = "VECTOR" 고려
}
