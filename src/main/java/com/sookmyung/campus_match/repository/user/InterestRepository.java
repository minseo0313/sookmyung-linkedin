package com.sookmyung.campus_match.repository.user;

import com.sookmyung.campus_match.domain.user.Interest;
import com.sookmyung.campus_match.domain.common.enums.InterestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findByInterestType(InterestType interestType);
    
    @Query("SELECT i FROM Interest i WHERE " +
           "(:category IS NULL OR i.category LIKE %:category%) AND " +
           "(:interestType IS NULL OR i.interestType = :interestType)")
    Page<Interest> search(@Param("category") String category, 
                          @Param("interestType") InterestType interestType, 
                          Pageable pageable);
    
    List<Interest> findByCategory(String category);
}
