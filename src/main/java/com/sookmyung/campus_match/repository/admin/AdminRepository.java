package com.sookmyung.campus_match.repository.admin;

import com.sookmyung.campus_match.domain.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a WHERE a.user.name = :adminName")
    Optional<Admin> findByAdminName(@Param("adminName") String adminName);
    
    @Query("SELECT COUNT(a) > 0 FROM Admin a WHERE a.user.name = :adminName")
    boolean existsByAdminName(@Param("adminName") String adminName);
}
