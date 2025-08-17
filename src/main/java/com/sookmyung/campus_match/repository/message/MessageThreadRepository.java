package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.MessageThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    // 중첩 속성 표기를 사용한 메서드들 (OR 조건은 2개 인자 필요)
    List<MessageThread> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);
    
    Optional<MessageThread> findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);
    
    Page<MessageThread> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id, Pageable pageable);
    
    // 정렬이 포함된 OR 조건 메서드들
    List<MessageThread> findByUser1_IdOrUser2_IdOrderByUpdatedAtDesc(Long user1Id, Long user2Id);
    
    List<MessageThread> findByStartedFromTypeAndStartedFromId(String startedFromType, Long startedFromId);

    // 추가 메서드들 (기존 서비스 코드와 호환성을 위해)
    @Query("SELECT mt FROM MessageThread mt WHERE " +
           "(mt.user1.id = :userId OR mt.user2.id = :userId)")
    List<MessageThread> findByParticipant(@Param("userId") Long userId);
    
    @Query("SELECT mt FROM MessageThread mt WHERE " +
           "(mt.user1.id = :userId1 OR mt.user2.id = :userId1) AND " +
           "(mt.user1.id = :userId2 OR mt.user2.id = :userId2)")
    Optional<MessageThread> findByParticipants(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT mt FROM MessageThread mt WHERE " +
           "(mt.user1.id = :userId OR mt.user2.id = :userId) ORDER BY mt.updatedAt DESC")
    Page<MessageThread> findAllForUserOrderByLastMessageAtDesc(@Param("userId") Long userId, Pageable pageable);

    // MessageService에서 호출하는 메서드들
    @Query("SELECT mt FROM MessageThread mt WHERE " +
           "(mt.user1.id = :userId OR mt.user2.id = :userId OR " +
           "mt.participant1.id = :userId OR mt.participant2.id = :userId)")
    List<MessageThread> findByUserId(@Param("userId") Long userId);
}
