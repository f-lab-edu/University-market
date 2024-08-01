package university.market.notification.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.auth.AuthType;
import university.market.member.utils.http.HttpRequest;
import university.market.notification.domain.NotificationVO;
import university.market.notification.service.NotificationService;
import university.market.notification.service.dto.NotificationSendRequest;

@RequiredArgsConstructor
@RequestMapping("api/notification")
@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private final HttpRequest httpRequest;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @AuthCheck({AuthType.ROLE_ADMIN, AuthType.ROLE_VERIFY_USER})
    public ResponseEntity<SseEmitter> subscribe() {
        SseEmitter sseEmitter = notificationService.subscribe(httpRequest.getCurrentMember());
        return ResponseEntity.ok(sseEmitter);
    }

    @GetMapping("/")
    @AuthCheck({AuthType.ROLE_ADMIN, AuthType.ROLE_VERIFY_USER})
    public ResponseEntity<List<NotificationVO>> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotifications(httpRequest.getCurrentMember()));
    }

    @PostMapping("/")
    @AuthCheck({AuthType.ROLE_ADMIN})
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationSendRequest request) {
        notificationService.send(request);
        return ResponseEntity.ok().build();
    }
}
