package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.Message;
import com.sookmyung.campus_match.domain.message.MessageThread;
import com.sookmyung.campus_match.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 스레드 내 메시지 페이징 (시간 오름/내림차순)
    Page<Message> findByThread_IdOrderByCreatedAtAsc(Long threadId, Pageable pageable);
    Page<Message> findByThread_IdOrderByCreatedAtDesc(Long threadId, Pageable pageable);

    // 인피니트 스크롤용 키셋 페이지네이션 (이전 메시지 더 불러오기)
    List<Message> findByThread_IdAndIdLessThanOrderByIdDesc(Long threadId, Long beforeId);

    // 스레드 최신 메시지 1건 (미리보기/정렬용)
    Optional<Message> findTop1ByThread_IdOrderByCreatedAtDesc(Long threadId);

    // 스레드 최신 N건 (간단 미리보기)
    List<Message> findTop20ByThread_IdOrderByCreatedAtDesc(Long threadId);

    // 보낸 사람 기준 조회
    List<Message> findBySender_Id(Long senderId);

    // 스레드 내 메시지 수
    long countByThread_Id(Long threadId);

    // 스레드 전체 삭제(스레드 삭제 전 정리용 — 보통은 cascade로 처리)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByThread_Id(Long threadId);

    // 스레드 내 본문 검색(필요 시)
    Page<Message> findByThread_IdAndContentContainingIgnoreCase(Long threadId, String keyword, Pageable pageable);

    // 기존 서비스 코드와의 호환성을 위한 메서드들 (중첩 속성 표기 사용)
    List<Message> findByThread_Id(Long threadId);
    
    @Query("SELECT m FROM Message m WHERE m.thread.id = :threadId ORDER BY m.createdAt ASC")
    List<Message> findByThread_IdOrderByCreatedAtAsc(@Param("threadId") Long threadId);
    
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.createdAt DESC")
    Page<Message> findBySender_IdOrderByCreatedAtDesc(@Param("senderId") Long senderId, Pageable pageable);

    // 안전한 대안 메서드들 (@Query 사용)
    @Query("SELECT m FROM Message m WHERE m.thread.id = :threadId")
    List<Message> findByThreadIdSafe(@Param("threadId") Long threadId);
    
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId")
    List<Message> findBySenderIdSafe(@Param("senderId") Long senderId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.thread.id = :threadId")
    long countByThreadIdSafe(@Param("threadId") Long threadId);
    
    @Query("SELECT m FROM Message m WHERE m.thread.id = :threadId ORDER BY m.createdAt ASC")
    List<Message> findAllByThreadIdOrderByCreatedAtAscSafe(@Param("threadId") Long threadId);
    
    @Query("SELECT m FROM Message m WHERE m.thread.id = :threadId ORDER BY m.createdAt DESC")
    List<Message> findAllByThreadIdOrderByCreatedAtDescSafe(@Param("threadId") Long threadId);

    // MessageService에서 호출하는 메서드들
    List<Message> findByThreadOrderByCreatedAtAsc(MessageThread thread);
    
    Optional<Message> findFirstByThreadOrderByCreatedAtDesc(MessageThread thread);
    
    Page<Message> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.thread.id IN " +
           "(SELECT mt.id FROM MessageThread mt WHERE mt.participant1.id = :userId OR mt.participant2.id = :userId) " +
           "AND m.sender.id != :userId AND m.deleted = false")
    long countUnreadMessagesByUser(@Param("userId") Long userId);
}
