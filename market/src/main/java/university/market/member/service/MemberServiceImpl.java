package university.market.member.service;

import java.util.ArrayList;
import java.util.List;
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
        if (memberMapper.findMemberByEmail(joinRequest.email()) != null) {
            throw new MemberException(MemberExceptionType.ALREADY_EXISTED_MEMBER);
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
        memberMapper.updateAuth(checkVerificationCodeRequest.email(), AuthType.ROLE_VERIFY_USER);
    }

    @Override
    public List<MemberVO> findMembersByIds(List<Long> ids) {
        List<MemberVO> members = new ArrayList<>();
        for (Long id : ids) {
            members.add(memberMapper.findMemberById(id));
        }

        return members;
    }

    @Transactional(readOnly = true)
    public LoginResponse loginMember(LoginRequest loginRequest) {
        final MemberVO member = memberMapper.findMemberByEmail(loginRequest.email());
        if (member == null || !passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new MemberException(MemberExceptionType.INVALID_LOGIN_CREDENTIALS);
        }

        return LoginResponse.builder()
                .memberId(member.getId())
                .token(jwtTokenProvider.generateToken(member.getEmail()))
                .build();
    }

    @Transactional(readOnly = true)
    public MemberVO findMemberByEmail(String email) {
        return memberMapper.findMemberByEmail(email);
    }

    @AuthCheck({AuthType.ROLE_ADMIN})
    @Transactional
    public void deleteMember(Long id) {
        memberMapper.deleteMemberById(id);
    }

    @AuthCheck({AuthType.ROLE_ADMIN, AuthType.ROLE_VERIFY_USER, AuthType.ROLE_USER})
    @Transactional
    public void deleteMyself(String token) {
        memberMapper.deleteMemberByEmail(jwtTokenProvider.extractEmail(token));
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long id, MemberStatus memberStatus) {
        memberMapper.updateMemberStatus(id, memberStatus);
    }

    @Override
    @Transactional
    public MemberVO findMemberByToken(String token) {
        jwtTokenProvider.validateToken(token);
        String email = jwtTokenProvider.extractEmail(token);
        return memberMapper.findMemberByEmail(email);
    }
}
