package com.sookmyung.campus_match.service.message;

import com.sookmyung.campus_match.domain.message.Message;
import com.sookmyung.campus_match.domain.message.MessageThread;
import com.sookmyung.campus_match.domain.user.User;
import com.sookmyung.campus_match.dto.message.MessageReplyRequest;
import com.sookmyung.campus_match.dto.message.MessageReportRequest;
import com.sookmyung.campus_match.dto.message.MessageResponse;
import com.sookmyung.campus_match.dto.message.MessageSendRequest;
import com.sookmyung.campus_match.dto.message.MessageThreadResponse;
import com.sookmyung.campus_match.exception.ApiException;
import com.sookmyung.campus_match.exception.ErrorCode;
import com.sookmyung.campus_match.repository.message.MessageRepository;
import com.sookmyung.campus_match.repository.message.MessageThreadRepository;
import com.sookmyung.campus_match.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse sendMessage(MessageSendRequest request, String username) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "수신자를 찾을 수 없습니다."));

        // 기존 스레드가 있는지 확인
        MessageThread thread = messageThreadRepository.findByParticipants(sender.getId(), recipient.getId())
                .orElse(null);

        if (thread == null) {
            // 새로운 스레드 생성
            thread = MessageThread.builder()
                    .user1(sender)
                    .user2(recipient)
                    .startedFromType(request.getStartedFromType())
                    .startedFromId(request.getStartedFromId())
                    .lastMessageAt(LocalDateTime.now())
                    .build();
            thread = messageThreadRepository.save(thread);
        }

        // 메시지 생성
        Message message = Message.builder()
                .thread(thread)
                .sender(sender)
                .messageContent(request.getContent())
                .build();

        Message savedMessage = messageRepository.save(message);

        // 스레드의 마지막 메시지 시간 업데이트
        thread.updateLastMessageAt(LocalDateTime.now());
        messageThreadRepository.save(thread);

        return MessageResponse.from(savedMessage);
    }

    public List<MessageThreadResponse> getMessageList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<MessageThread> threads = messageThreadRepository.findByParticipant(user.getId());
        
        return threads.stream()
                .map(MessageThreadResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reportMessage(Long messageId, MessageReportRequest request, String username) {
        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // TODO: 메시지 신고 로직 구현 필요
        log.info("메시지 신고: 메시지 ID {}, 신고자 {}, 사유 {}", messageId, username, request.getReason());
    }

    public MessageResponse getMessageDetail(Long messageId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        // 메시지에 접근 권한이 있는지 확인
        if (!message.getThread().getParticipantA().getId().equals(user.getId()) &&
            !message.getThread().getParticipantB().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "메시지에 접근할 권한이 없습니다.");
        }

        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse replyToMessage(Long messageId, MessageReplyRequest request, String username) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Message originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND, "메시지를 찾을 수 없습니다."));

        MessageThread thread = originalMessage.getThread();

        // 스레드에 참여하고 있는지 확인
        if (!thread.getParticipantA().getId().equals(sender.getId()) &&
            !thread.getParticipantB().getId().equals(sender.getId())) {
            throw new ApiException(ErrorCode.MESSAGE_ACCESS_DENIED, "메시지에 접근할 권한이 없습니다.");
        }

        // 답장 메시지 생성
        Message reply = Message.builder()
                .thread(thread)
                .sender(sender)
                .messageContent(request.getContent())
                .build();

        Message savedReply = messageRepository.save(reply);

        // 스레드의 마지막 메시지 시간 업데이트
        thread.updateLastMessageAt(LocalDateTime.now());
        messageThreadRepository.save(thread);

        return MessageResponse.from(savedReply);
    }
}
