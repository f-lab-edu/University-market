package university.market.member.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;

@Mapper
public interface MemberMapper {
    void joinMember(MemberVO memberVO);

    Optional<MemberVO> findMemberByEmail(String email);

    Optional<MemberVO> findMemberById(long id);

    void deleteMemberById(long id);

    void updateAuth(String email, AuthType auth);

    void updateMemberStatus(long id, MemberStatus memberStatus);
}
