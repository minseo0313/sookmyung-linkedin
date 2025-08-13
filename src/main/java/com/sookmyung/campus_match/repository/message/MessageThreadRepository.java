package com.sookmyung.campus_match.repository.message;

import com.sookmyung.campus_match.domain.message.MessageThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    // 특정 유저가 참여 중인 스레드 목록 (최근 메시지순)
    @Query("""
           select t
             from MessageThread t
            where t.participantA.id = :userId
               or t.participantB.id = :userId
            order by t.lastMessageAt desc, t.id desc
           """)
    Page<MessageThread> findAllForUserOrderByLastMessageAtDesc(Long userId, Pageable pageable);

    // 두 사용자 간 스레드 단건 조회(순서 무관)
    @Query("""
           select t
             from MessageThread t
            where (t.participantA.id = :userId1 and t.participantB.id = :userId2)
               or (t.participantA.id = :userId2 and t.participantB.id = :userId1)
           """)
    Optional<MessageThread> findBetweenUsers(Long userId1, Long userId2);

    // 두 사용자 간 스레드 존재 여부(순서 무관)
    @Query("""
           select (count(t) > 0)
             from MessageThread t
            where (t.participantA.id = :userId1 and t.participantB.id = :userId2)
               or (t.participantA.id = :userId2 and t.participantB.id = :userId1)
           """)
    boolean existsBetweenUsers(Long userId1, Long userId2);

    // 스레드 단건 조회(권한 체크용: 해당 유저가 참여중인지 함께 확인)
    @Query("""
           select t
             from MessageThread t
            where t.id = :threadId
              and (t.participantA.id = :userId or t.participantB.id = :userId)
           """)
    Optional<MessageThread> findByIdAndParticipant(Long threadId, Long userId);

    // 내가 참여 중인 스레드 수
    long countByParticipantA_IdOrParticipantB_Id(Long userIdA, Long userIdB);

    // 두 사용자 간 스레드 단건 조회 (MessageService에서 사용)
    @Query("""
           select t
             from MessageThread t
            where (t.participantA.id = :userId1 and t.participantB.id = :userId2)
               or (t.participantA.id = :userId2 and t.participantB.id = :userId1)
           """)
    Optional<MessageThread> findByParticipants(Long userId1, Long userId2);

    // 특정 사용자가 참여 중인 스레드 목록 (MessageService에서 사용)
    @Query("""
           select t
             from MessageThread t
            where t.participantA.id = :userId
               or t.participantB.id = :userId
            order by t.lastMessageAt desc, t.id desc
           """)
    List<MessageThread> findByParticipant(Long userId);
}
