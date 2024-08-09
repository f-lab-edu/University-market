package university.market.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import university.market.helper.fixture.MemberFixture;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.university.UniversityType;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;
import university.market.member.mapper.MemberMapper;
import university.market.member.service.dto.request.JoinRequest;
import university.market.member.service.dto.request.LoginRequest;
import university.market.member.service.dto.response.LoginResponse;
import university.market.member.utils.jwt.JwtTokenProvider;
import university.market.verify.email.service.EmailVerificationService;
import university.market.verify.email.service.dto.CheckVerificationCodeRequest;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberMapper memberMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private MemberVO member;

    @BeforeEach
    public void init() {
        member = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);
    }

    @Test
    @DisplayName("[success] 회원가입 성공")
    public void joinMember_회원가입_성공() {
        // given
        JoinRequest joinRequest = JoinRequest.builder()
                .name(member.getName())
                .email(member.getEmail())
                .password("testPassword")
                .university(UniversityType.fromValue(member.getUniversity()).name())
                .build();

        // mocking
        when(memberMapper.findMemberByEmail(joinRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(joinRequest.password())).thenReturn(member.getPassword());
        doNothing().when(memberMapper).joinMember(any(MemberVO.class));

        // when
        memberService.joinMember(joinRequest);

        // then
        verify(memberMapper).findMemberByEmail(joinRequest.email());
        verify(passwordEncoder).encode(joinRequest.password());
        verify(memberMapper).joinMember(any(MemberVO.class));
    }

    @Test
    @DisplayName("[fail] 회원가입 실패 - 이미 존재하는 회원")
    public void joinMember_회원가입_실패_이미_존재하는_회원() {
        // given
        JoinRequest joinRequest = JoinRequest.builder()
                .name(member.getName())
                .email(member.getEmail())
                .password("testPassword")
                .university(UniversityType.fromValue(member.getUniversity()).name())
                .build();

        // mocking
        when(memberMapper.findMemberByEmail(joinRequest.email())).thenReturn(Optional.of(member));

        // mocking
        when(memberMapper.findMemberByEmail(joinRequest.email())).thenReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.joinMember(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasMessageContaining(MemberExceptionType.ALREADY_EXISTED_MEMBER.errorMessage());
    }

    @Test
    @DisplayName("[fail] 회원가입 실패 - 데이터베이스 에러")
    public void joinMember_회원가입_실패_데이터베이스_에러() {
        // given
        JoinRequest joinRequest = JoinRequest.builder()
                .name(member.getName())
                .email(member.getEmail())
                .password("testPassword")
                .university(UniversityType.fromValue(member.getUniversity()).name())
                .build();

        // mocking
        when(memberMapper.findMemberByEmail(joinRequest.email())).thenThrow(new RuntimeException());

        // when & then
        assertThatThrownBy(() -> memberService.joinMember(joinRequest))
                .isInstanceOf(MemberException.class)
                .hasMessageContaining(MemberExceptionType.DATABASE_ERROR.errorMessage());
    }

    @Test
    @DisplayName("[success] 이메일 인증 성공")
    public void verifyEmailUser_이메일_인증_성공() {
        // given
        String email = member.getEmail();
        String verificationCode = "testVerificationCode";
        CheckVerificationCodeRequest checkVerificationCodeRequest = CheckVerificationCodeRequest.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();

        // mocking
        doNothing().when(emailVerificationService).checkVerificationCode(any());
        doNothing().when(memberMapper).updateAuth(email, AuthType.ROLE_VERIFY_USER);

        // when
        memberService.verifyEmailUser(checkVerificationCodeRequest);

        // then
        verify(emailVerificationService).checkVerificationCode(any());
        verify(memberMapper).updateAuth(email, AuthType.ROLE_VERIFY_USER);
    }

    @Test
    @DisplayName("[success] 로그인 성공")
    public void login_로그인_성공() {
        // given
        String email = member.getEmail();
        String password = "testPassword";
        String testToken = "testToken";
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // mocking
        when(memberMapper.findMemberByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(member.getId())).thenReturn(testToken);

        // when
        LoginResponse loginResponse = memberService.loginMember(loginRequest);

        // then
        verify(memberMapper).findMemberByEmail(email);
        verify(passwordEncoder).matches(password, member.getPassword());
        assertThat(loginResponse.token()).isEqualTo(testToken);
    }

    @Test
    @DisplayName("[fail] 로그인 실패 - 비밀번호 불일치")
    public void login_로그인_실패_비밀번호_불일치() {
        // given
        String email = member.getEmail();
        String password = "invalidPassword";
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        // mocking
        when(memberMapper.findMemberByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.loginMember(loginRequest))
                .isInstanceOf(MemberException.class)
                .hasMessageContaining(MemberExceptionType.INVALID_LOGIN_CREDENTIALS.errorMessage());
    }
}