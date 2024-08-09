package university.market.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;
import university.market.member.mapper.MemberMapper;
import university.market.member.service.dto.request.JoinRequest;
import university.market.member.service.dto.request.LoginRequest;
import university.market.member.service.dto.response.LoginResponse;
import university.market.member.utils.jwt.JwtTokenProvider;
import university.market.verify.email.service.EmailVerificationService;
import university.market.verify.email.service.dto.CheckVerificationCodeRequest;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public void joinMember(JoinRequest joinRequest) {
        joinUser(joinRequest, AuthType.ROLE_USER);
    }

    @Transactional
    public void joinAdminUser(JoinRequest joinRequest) {
        joinUser(joinRequest, AuthType.ROLE_ADMIN);
    }

    private void joinUser(JoinRequest joinRequest, AuthType authType) {

        try {
            if (memberMapper.findMemberByEmail(joinRequest.email()).isPresent()) {
                throw new MemberException(MemberExceptionType.ALREADY_EXISTED_MEMBER);
            }
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }

        final MemberVO memberVO = MemberVO.builder()
                .name(joinRequest.name())
                .email(joinRequest.email())
                .password(passwordEncoder.encode(joinRequest.password()))
                .university(joinRequest.university())
                .auth(authType)
                .build();

        try {
            memberMapper.joinMember(memberVO);
        } catch (Exception e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }
    }

    @AuthCheck({AuthType.ROLE_USER})
    @Transactional
    public void verifyEmailUser(CheckVerificationCodeRequest checkVerificationCodeRequest) {
        emailVerificationService.checkVerificationCode(checkVerificationCodeRequest);

        try {
            memberMapper.updateAuth(checkVerificationCodeRequest.email(), AuthType.ROLE_VERIFY_USER);
        } catch (Exception e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse loginMember(LoginRequest loginRequest) {
        final MemberVO member = memberMapper.findMemberByEmail(loginRequest.email())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new MemberException(MemberExceptionType.INVALID_LOGIN_CREDENTIALS);
        }

        return LoginResponse.builder()
                .memberId(member.getId())
                .token(jwtTokenProvider.generateToken(member.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public MemberVO findMemberById(long memberId) {
        return memberMapper.findMemberById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    @AuthCheck({AuthType.ROLE_ADMIN})
    @Transactional
    public void deleteMember(long memberId) {
        try {
            memberMapper.deleteMemberById(memberId);
        } catch (RuntimeException e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }
    }

    @AuthCheck({AuthType.ROLE_ADMIN, AuthType.ROLE_VERIFY_USER, AuthType.ROLE_USER})
    @Transactional
    public void deleteMyself(MemberVO member) {
        try {
            memberMapper.deleteMemberById(member.getId());
        } catch (RuntimeException e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateMemberStatus(long memberId, MemberStatus memberStatus) {
        try {
            memberMapper.deleteMemberById(memberId);
        } catch (RuntimeException e) {
            throw new MemberException(MemberExceptionType.DATABASE_ERROR);
        }
    }
}
