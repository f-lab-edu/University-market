package university.market.member.service;


import java.util.List;
import university.market.member.domain.MemberVO;
import university.market.member.domain.memberstatus.MemberStatus;
import university.market.member.service.dto.request.JoinRequest;
import university.market.member.service.dto.request.LoginRequest;
import university.market.member.service.dto.response.LoginResponse;
import university.market.verify.email.service.dto.CheckVerificationCodeRequest;

public interface MemberService {
    void joinMember(JoinRequest joinRequest);

    void joinAdminUser(JoinRequest joinRequest);

    LoginResponse loginMember(LoginRequest loginRequest);

    MemberVO findMemberByEmail(String email);

    void deleteMember(Long id);

    void deleteMyself(String token);

    void updateMemberStatus(Long id, MemberStatus memberStatus);

    void verifyEmailUser(CheckVerificationCodeRequest checkVerificationCodeRequest);

    List<MemberVO> findMembersByIds(List<Long> ids);

    MemberVO findMemberByToken(String token);
}
