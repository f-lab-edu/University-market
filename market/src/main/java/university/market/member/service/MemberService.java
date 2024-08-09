package university.market.member.service;


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

    MemberVO findMemberById(long memberId);

    void deleteMember(long memberId);

    void deleteMyself(MemberVO memberVO);

    void updateMemberStatus(long memberId, MemberStatus memberStatus);

    void verifyEmailUser(CheckVerificationCodeRequest checkVerificationCodeRequest);
}
