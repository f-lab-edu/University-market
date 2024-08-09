package university.market.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;
import university.market.helper.fixture.MemberFixture;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;
import university.market.member.mapper.MemberMapper;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberMapperTest {
    @Autowired
    private MemberMapper memberMapper;

    private MemberVO member;

    @BeforeEach
    public void init() {
        // given
        member = MemberFixture.testMember(AuthType.ROLE_VERIFY_USER);
    }

    @Test
    @Transactional
    @DisplayName("[success] member 등록 성공")
    public void member_등록_성공() {
        // when
        memberMapper.joinMember(member);

        // then
        assertThat(member.getId()).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("[success] member 조회 성공")
    public void member_조회_성공() {
        // given
        memberMapper.joinMember(member);

        // when
        MemberVO foundMember = memberMapper.findMemberById(member.getId()).get();

        // then
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @Transactional
    @DisplayName("[success] member 이메일로 조회 성공")
    public void member_이메일로_조회_성공() {
        // given
        memberMapper.joinMember(member);

        // when
        MemberVO foundMember = memberMapper.findMemberByEmail(member.getEmail()).get();

        // then
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @Transactional
    @DisplayName("[success] member 삭제 성공")
    public void member_삭제_성공() {
        // given
        memberMapper.joinMember(member);

        // when
        memberMapper.deleteMemberById(member.getId());

        // then
        assertThat(memberMapper.findMemberById(member.getId())).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("[success] member 권한 업데이트 성공")
    public void member_권한_업데이트_성공() {
        // given
        memberMapper.joinMember(member);

        // when
        memberMapper.updateAuth(member.getEmail(), AuthType.ROLE_USER);

        // then
        assertThat(memberMapper.findMemberById(member.getId()).get().getAuth()).isEqualTo(AuthType.ROLE_USER);
    }

    @Test
    @Transactional
    @DisplayName("[success] member 상태 업데이트 성공")
    public void member_상태_업데이트_성공() {
        // given
        memberMapper.joinMember(member);

        // when
        memberMapper.updateMemberStatus(member.getId(), MemberStatus.ONLINE);

        // then
        assertThat(memberMapper.findMemberById(member.getId()).get().getMemberStatus()).isEqualTo(MemberStatus.ONLINE);
    }
}
