package university.market.chat.room.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ChatCreateRequest(
        String title,
        Long itemId,
        List<Long> memberIds
) {
}
