package university.market.member.utils.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import university.market.helper.fixture.MemberFixture;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("[success] login success generate token")
    void generateToken_login_success_generate_token() {
        // given
        MemberVO testMember = MemberFixture.testIdMember(AuthType.ROLE_ADMIN);
        final String token = "testToken";

        // mocking
        when(jwtTokenProvider.generateToken(testMember.getId())).thenReturn(token);
        when(jwtTokenProvider.extractMemberId(token)).thenReturn(testMember.getId());

        // when
        final String generatedToken = jwtTokenProvider.generateToken(testMember.getId());
        final long extractedMemberId = jwtTokenProvider.extractMemberId(generatedToken);

        // then
        assertThat(extractedMemberId).isEqualTo(testMember.getId());
    }

    @Test
    @DisplayName("[fail] token expired")
    void token_expired_not_valid() {
        // given
        MemberVO testMember = MemberFixture.testIdMember(AuthType.ROLE_ADMIN);
        String token = "testToken";
        final LocalDateTime now = LocalDateTime.now();
        final long expireTime = 1000L;

        ExpireDateSupplier expireDateSupplier = () -> Date.from(LocalDateTime.from(now.minusSeconds(expireTime))
                .atZone(ZoneId.systemDefault()).toInstant());

        // mocking
        when(jwtTokenProvider.generateToken(testMember.getId(), expireDateSupplier)).thenReturn(token);
        doThrow(new MemberException(MemberExceptionType.EXPIRED_ACCESS_TOKEN)).when(jwtTokenProvider)
                .validateToken(token);

        // when
        final String generatedToken = jwtTokenProvider.generateToken(testMember.getId(), expireDateSupplier);

        // then
        assertThatCode(() -> jwtTokenProvider.validateToken(generatedToken))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExceptionType.EXPIRED_ACCESS_TOKEN.errorMessage());
    }
}
