package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.domain.common.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByStudentId(String studentId);
    
    boolean existsByStudentId(String studentId);
    
    boolean existsByEmail(String email);
    
    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsBySookmyungEmail(String sookmyungEmail);
    
    boolean existsByPhone(String phone);
    
    long countByApprovalStatus(ApprovalStatus status);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR u.name LIKE %:name%) AND " +
           "(:department IS NULL OR u.department LIKE %:department%)")
    Page<User> search(@Param("name") String name, 
                      @Param("department") String department, 
                      Pageable pageable);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    List<User> findByApprovalStatus(ApprovalStatus status);
    
    @Query("SELECT u FROM User u WHERE " +
           "u.fullName LIKE %:keyword% OR u.username LIKE %:keyword%")
    Page<User> findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            @Param("keyword") String keyword1, 
            @Param("keyword") String keyword2, 
            Pageable pageable);

    // SearchService에서 호출하는 메서드들
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR (u.name LIKE %:keyword% OR u.department LIKE %:keyword%)) AND " +
           "(:department IS NULL OR u.department = :department)")
    Page<User> searchByKeywordAndDepartment(@Param("keyword") String keyword, 
                                           @Param("department") String department, 
                                           Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.department = :department")
    Page<User> findByDepartment(@Param("department") String department, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.department LIKE %:department%")
    Page<User> findByDepartmentContaining(@Param("department") String department, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% OR u.department LIKE %:keyword%")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.approvalStatus = :status AND u.id != :userId")
    Page<User> findByApprovalStatusAndIdNot(@Param("status") ApprovalStatus status, 
                                           @Param("userId") Long userId, 
                                           Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.approvalStatus = :status AND u.id != :userId")
    List<User> findByApprovalStatusAndIdNot(@Param("status") ApprovalStatus status, 
                                           @Param("userId") Long userId);
    
    @Query("SELECT u FROM User u WHERE u.department = :department AND u.approvalStatus = :status")
    List<User> findByDepartmentAndApprovalStatus(@Param("department") String department, 
                                                @Param("status") ApprovalStatus status);
}
