package university.market.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;

@Mapper
public interface MemberMapper {
    void joinMember(MemberVO memberVO);

    MemberVO findMemberByEmail(String email);

    void deleteMemberByEmail(String email);

    void deleteMemberById(long id);

    void updateAuth(String email, AuthType auth);

    void updateMemberStatus(long id, MemberStatus memberStatus);

    MemberVO findMemberById(long id);
}
