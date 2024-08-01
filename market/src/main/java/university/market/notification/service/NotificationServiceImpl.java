package university.market.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.service.MemberService;
import university.market.notification.domain.NotificationVO;
import university.market.notification.domain.PendingNotificationVO;
import university.market.notification.repository.NotificationRepository;
import university.market.notification.repository.PendingNotificationRepository;
import university.market.notification.service.dto.NotificationSendRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    @Value("${notification.timeout}")
    private long DEFAULT_TIMEOUT;

    private final NotificationRepository notificationRepository;
    private final PendingNotificationRepository pendingNotificationRepository;
    private final MemberService memberService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public SseEmitter subscribe(MemberVO member) {
        String emitterId = makeId(member.getId().toString());
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError((error) -> emitters.remove(emitterId));

        NotificationVO firstNotification = NotificationVO.builder()
                .title("connected success")
                .content(member.getName())
                .receiverId(member.getId().toString())
                .redirectUrl("blank")
                .build();
        firstNotification = notificationRepository.save(firstNotification);
        List<PendingNotificationVO> pendingNotifications = pendingNotificationRepository.findByReceiverId(
                member.getId().toString());

        sendNotification(emitter, emitterId, firstNotification);

        if (!pendingNotifications.isEmpty()) {
            sendLostNotifications(emitter, emitterId, pendingNotifications);
        }

        return emitter;
    }

    @Override
    public void send(NotificationSendRequest request) {
        List<MemberVO> receivers = memberService.findMembersByIds(request.receiverId());
        List<NotificationVO> notifications = receivers.stream()
                .map(receiver -> NotificationVO.builder()
                        .title(request.title())
                        .content(request.content())
                        .receiverId(receiver.getId().toString())
                        .redirectUrl(request.redirectUrl())
                        .build())
                .toList();

        List<NotificationVO> savedNotification = notificationRepository.saveAll(notifications);
        Map<String, SseEmitter> emitters;
        for (NotificationVO notification : savedNotification) {
            emitters = findEmittersByReceiverId(Long.parseLong(notification.getReceiverId()));
            if (emitters.isEmpty()) {
                PendingNotificationVO pendingNotification = PendingNotificationVO.builder()
                        .title(notification.getTitle())
                        .content(notification.getContent())
                        .receiverId(notification.getReceiverId())
                        .redirectUrl(notification.getRedirectUrl())
                        .build();
                pendingNotificationRepository.save(pendingNotification);
            } else {
                emitters.forEach((emitterId, emitter) -> sendNotification(emitter, emitterId, notification));
            }
        }
    }

    private void sendLostNotifications(SseEmitter emitter,
                                       String emitterId, List<PendingNotificationVO> pendingNotifications) {
        pendingNotifications
                .forEach(pendingNotification -> {
                    sendNotification(emitter, emitterId, pendingNotification);
                    pendingNotificationRepository.delete(pendingNotification);
                });
    }

    private void sendNotification(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitters.remove(emitterId);
            PendingNotificationVO pendingNotification = PendingNotificationVO.builder()
                    .title(((NotificationVO) data).getTitle())
                    .content(((NotificationVO) data).getContent())
                    .receiverId(((NotificationVO) data).getReceiverId())
                    .redirectUrl(((NotificationVO) data).getRedirectUrl())
                    .build();
            pendingNotificationRepository.save(pendingNotification);
        }
    }

    private String makeId(String memberId) {
        return memberId + "-" + System.currentTimeMillis();
    }

    private Map<String, SseEmitter> findEmittersByReceiverId(Long receiverId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(receiverId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public List<NotificationVO> getNotifications(MemberVO member) {
        return notificationRepository.findByReceiverId(member.getId().toString());
    }
}
