package university.market.notification.service;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import university.market.member.domain.MemberVO;
import university.market.notification.domain.NotificationVO;
import university.market.notification.service.dto.NotificationSendRequest;

public interface NotificationService {
    SseEmitter subscribe(MemberVO member);

    void send(NotificationSendRequest request);

    List<NotificationVO> getNotifications(MemberVO member);

}
