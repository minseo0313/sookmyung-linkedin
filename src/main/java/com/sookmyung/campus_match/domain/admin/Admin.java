package com.sookmyung.campus_match.domain.admin;

import com.sookmyung.campus_match.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseEntity {

    @Column(name = "admin_name", nullable = false, length = 255)
    private String adminName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
}
