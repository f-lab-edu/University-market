package university.market.member.utils.http;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import university.market.helper.fixture.MemberFixture;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;
import university.market.member.service.MemberService;
import university.market.member.utils.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class HttpRequestTest {
    @Mock
    private MemberService memberService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private HttpRequestImpl httpRequest;

    private MemberVO member;

    private final String validToken = "valid-token";


    private final String bearer = "Bearer ";

    private final String authorization = "Authorization";

    @BeforeEach
    public void init() {
        HttpRequestImpl.clearCurrentUser();
        member = MemberFixture.testIdMember(AuthType.ROLE_USER);
    }

    @Test
    @Transactional
    @DisplayName("[success] 현재 사용자 조회")
    public void 현재_사용자_조회() {
        // when
        when(httpServletRequest.getHeader(authorization)).thenReturn(bearer + validToken);
        doNothing().when(jwtTokenProvider).validateToken(validToken);
        when(jwtTokenProvider.extractMemberId(validToken)).thenReturn(member.getId());
        when(memberService.findMemberById(member.getId())).thenReturn(member);

        // then
        assertThat(httpRequest.getCurrentMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("[fail] 토큰 유효 조회 - 토큰 유효기간 지남")
    public void 토큰_유호_조회_토큰_유효기간_지남() {
        // given
        String expiredToken = "expired-token";

        // mocking
        when(httpServletRequest.getHeader(authorization)).thenReturn(bearer + expiredToken);
        doThrow(new MemberException(MemberExceptionType.EXPIRED_ACCESS_TOKEN))
                .when(jwtTokenProvider).validateToken(expiredToken);

        // when
        assertThatThrownBy(() -> httpRequest.getCurrentMember())
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExceptionType.EXPIRED_ACCESS_TOKEN.errorMessage());
    }
}
