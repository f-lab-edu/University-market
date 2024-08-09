package university.market.member.service.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(
        String email,
        String password
) {
}
