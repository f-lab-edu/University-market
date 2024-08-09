package university.market.member.domain;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;
import university.market.member.domain.university.UniversityType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {

    private Long id;

    private String name;

    private String email;

    private String password;

    private int university;

    private AuthType auth;

    private MemberStatus memberStatus;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @Builder
    public MemberVO(String name, String email, String password, String university, AuthType auth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.university = UniversityType.valueOf(university.toUpperCase()).getValue();
        this.memberStatus = MemberStatus.OFFLINE;
        this.auth = auth;
        this.isDeleted = false;
    }
}
