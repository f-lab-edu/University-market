package university.market.helper.fixture;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.domain.memberstatus.MemberStatus;
import university.market.member.domain.university.UniversityType;
import university.market.verify.email.utils.random.RandomUtil;
import university.market.verify.email.utils.random.RandomUtilImpl;

public class MemberFixture {

    private static final RandomUtil randomUtil;

    static {
        try {
            randomUtil = new RandomUtilImpl();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    ;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberFixture() {
    }

    public static MemberVO testMember(AuthType authType) {
        return MemberVO.builder()
                .name(randomUtil.generateRandomCountCode('A', 'z', 1, 255))
                .email(randomUtil.generateRandomCountCode('A', 'z', 1, 50)
                        + "@" + randomUtil.generateRandomCountCode('A', 'z', 1, 20)
                        + randomUtil.generateRandomCountCode('A', 'z', 1, 10))
                .password(passwordEncoder.encode(randomUtil.generateRandomCountCode('A', 'z', 1, 50)))
                .university(String.valueOf(UniversityType.values()[
                        randomUtil.generateRandomIntCode(0, UniversityType.values().length - 1)]))
                .auth(authType)
                .build();
    }

    public static MemberVO testIdMember(AuthType authType) {
        return new MemberVO(
                Long.parseLong(randomUtil.generateRandomCode('0', '9', 16)),
                randomUtil.generateRandomCountCode('A', 'z', 1, 255),
                randomUtil.generateRandomCountCode('A', 'z', 1, 50)
                        + "@" + randomUtil.generateRandomCountCode('A', 'z', 1, 20)
                        + randomUtil.generateRandomCountCode('A', 'z', 1, 10),
                passwordEncoder.encode(randomUtil.generateRandomCountCode('A', 'z', 1, 50)),
                UniversityType.values()[
                        randomUtil.generateRandomIntCode(0, UniversityType.values().length - 1)].getValue(),
                authType,
                MemberStatus.OFFLINE,
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                false
        );
    }
}
