package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

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
}
