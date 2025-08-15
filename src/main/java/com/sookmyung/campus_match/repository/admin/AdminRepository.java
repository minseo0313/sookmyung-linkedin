package com.sookmyung.campus_match.repository.admin;

import com.sookmyung.campus_match.domain.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminName(String adminName);
    
    boolean existsByAdminName(String adminName);
}
