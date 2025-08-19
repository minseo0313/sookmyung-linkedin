package com.sookmyung.campus_match.service.message;

import com.sookmyung.campus_match.domain.message.Message;
import com.sookmyung.campus_match.domain.message.MessageThread;
import com.sookmyung.campus_match.domain.message.MessageReport;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.message.MessageSendRequest;
import com.sookmyung.campus_match.dto.message.MessageResponse;
import com.sookmyung.campus_match.dto.message.MessageThreadResponse;
import com.sookmyung.campus_match.dto.message.MessageReplyRequest;
import com.sookmyung.campus_match.dto.message.MessageReportRequest;
import com.sookmyung.campus_match.dto.message.MessageReportResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.message.MessageRepository;
import com.sookmyung.campus_match.repository.message.MessageThreadRepository;
import com.sookmyung.campus_match.repository.message.MessageReportRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageThreadRepository messageThreadRepository;
    private final MessageReportRepository messageReportRepository;
    private final UserRepository userRepository;

    /**
     * 메시지 전송
     */
    @Transactional
    public MessageResponse sendMessage(MessageSendRequest request, String senderUsername) {
        Long senderId = Long.valueOf(senderUsername);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "발신자를 찾을 수 없습니다."));

        User recipient = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "수신자를 찾을 수 없습니다."));

        // 자기 자신에게 메시지를 보낼 수 없음
        if (sender.getId().equals(recipient.getId())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "자기 자신에게 메시지를 보낼 수 없습니다.");
        }

        // 기존 스레드가 있는지 확인
        MessageThread thread = messageThreadRepository.findByParticipants(sender.getId(), recipient.getId())
                .orElseGet(() -> createNewThread(sender, recipient));

        Message message = Message.builder()
                .sender(sender)
                .messageContent(request.getContent())
                .thread(thread)
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // 스레드의 마지막 메시지 시간 업데이트
        thread.updateLastMessageTime(LocalDateTime.now());
        messageThreadRepository.save(thread);

        return MessageResponse.from(savedMessage);
    }

    /**
     * 새로운 메시지 스레드 생성
     */
    private MessageThread createNewThread(User sender, User recipient) {
        MessageThread thread = MessageThread.builder()
                .participant1(sender)
                .participant2(recipient)
                .lastMessageTime(LocalDateTime.now())
                .build();
        
        return messageThreadRepository.save(thread);
    }

    /**
     * 메시지 답장
     */
    @Transactional
    public MessageResponse replyToMessage(Long threadId, MessageReplyRequest request, String senderUsername) {
        Long senderId = Long.valueOf(senderUsername);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "발신자를 찾을 수 없습니다."));

        MessageThread thread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지 스레드를 찾을 수 없습니다."));

        // 스레드 참여자인지 확인
        if (!thread.isParticipant(sender.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "이 스레드에 참여할 권한이 없습니다.");
        }

        Message message = Message.builder()
                .sender(sender)
                .messageContent(request.getContent())
                .thread(thread)
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // 스레드의 마지막 메시지 시간 업데이트
        thread.updateLastMessageTime(LocalDateTime.now());
        messageThreadRepository.save(thread);

        return MessageResponse.from(savedMessage);
    }

    /**
     * 메시지 스레드 목록 조회
     */
    public List<MessageThreadResponse> getMessageThreads(String username) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<MessageThread> threads = messageThreadRepository.findByUserId(user.getId());
        
        return threads.stream()
                .map(thread -> {
                    // 각 스레드의 마지막 메시지 조회
                    Message lastMessage = messageRepository.findFirstByThreadOrderByCreatedAtDesc(thread)
                            .orElse(null);
                    return MessageThreadResponse.from(thread, lastMessage);
                })
                .collect(Collectors.toList());
    }

    /**
     * 스레드의 메시지 목록 조회
     */
    public List<MessageResponse> getMessagesInThread(Long threadId, String username) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        MessageThread thread = messageThreadRepository.findById(threadId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지 스레드를 찾을 수 없습니다."));

        // 스레드 참여자인지 확인
        if (!thread.isParticipant(user.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "이 스레드에 참여할 권한이 없습니다.");
        }

        List<Message> messages = messageRepository.findByThreadOrderByCreatedAtAsc(thread);
        
        return messages.stream()
                .filter(message -> !message.isDeleted())
                .map(MessageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 메시지 수정
     */
    @Transactional
    public MessageResponse editMessage(Long messageId, String newContent, String username) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // 발신자만 메시지를 수정할 수 있음
        if (!message.getSender().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "메시지 발신자만 수정할 수 있습니다.");
        }

        message.edit(newContent);
        Message updatedMessage = messageRepository.save(message);

        return MessageResponse.from(updatedMessage);
    }

    /**
     * 메시지 삭제
     */
    @Transactional
    public void deleteMessage(Long messageId, String username) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // 발신자만 메시지를 삭제할 수 있음
        if (!message.getSender().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "메시지 발신자만 삭제할 수 있습니다.");
        }

        message.softDelete();
        messageRepository.save(message);
    }

    /**
     * 메시지 신고
     */
    @Transactional
    public MessageReportResponse reportMessage(Long messageId, MessageReportRequest request, String username) {
        Long reporterId = Long.valueOf(username);
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "신고자를 찾을 수 없습니다."));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // 자기 자신의 메시지는 신고할 수 없음
        if (message.getSender().getId().equals(reporter.getId())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "자기 자신의 메시지는 신고할 수 없습니다.");
        }

        // 이미 신고한 메시지인지 확인
        if (messageReportRepository.existsByMessageAndReporter(message, reporter)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "이미 신고한 메시지입니다.");
        }

        MessageReport report = MessageReport.builder()
                .message(message)
                .reporter(reporter)
                .reason(request.getReason())
                .description(request.getDescription())
                .reportedAt(LocalDateTime.now())
                .build();

        MessageReport savedReport = messageReportRepository.save(report);
        return MessageReportResponse.from(savedReport);
    }

    /**
     * 사용자의 메시지 목록 조회 (페이징)
     */
    public Page<MessageResponse> getUserMessages(String username, Pageable pageable) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Page<Message> messages = messageRepository.findBySenderOrderByCreatedAtDesc(user, pageable);
        
        return messages.map(MessageResponse::from);
    }

    /**
     * 읽지 않은 메시지 수 조회
     */
    public long getUnreadMessageCount(String username) {
        Long userId = Long.valueOf(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return messageRepository.countUnreadMessagesByUser(user.getId());
    }
}
