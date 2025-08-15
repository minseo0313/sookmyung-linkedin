package com.sookmyung.campus_match.domain.user;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import com.sookmyung.campus_match.domain.common.enums.InterestType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest extends BaseEntity {

    @Column(name = "interest_name", nullable = false, length = 255)
    private String interestName;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_type", nullable = false)
    private InterestType interestType;

    @Column(name = "interest_name_en", length = 255)
    private String interestNameEn;

    @Column(name = "category", length = 255)
    private String category;
}
