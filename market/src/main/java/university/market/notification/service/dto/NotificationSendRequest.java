package university.market.notification.service.dto;

import java.util.List;

public record NotificationSendRequest(
        String title,
        String content,
        List<Long> receiverId,
        String redirectUrl
) {
}
