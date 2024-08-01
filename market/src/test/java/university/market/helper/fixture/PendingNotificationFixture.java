package university.market.helper.fixture;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import university.market.member.domain.MemberVO;
import university.market.notification.domain.PendingNotificationVO;
import university.market.verify.email.utils.random.RandomUtil;
import university.market.verify.email.utils.random.RandomUtilImpl;

public class PendingNotificationFixture {
    private static final RandomUtil randomUtil;

    static {
        try {
            randomUtil = new RandomUtilImpl();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private PendingNotificationFixture() {
    }

    public static PendingNotificationVO testNotification(MemberVO receiver) {
        return PendingNotificationVO.builder()
                .title(randomUtil.generateRandomCode('A', 'z', 50))
                .content(randomUtil.generateRandomCode('A', 'z', 300))
                .receiverId(receiver.getId().toString())
                .redirectUrl(randomUtil.generateRandomCode('A', 'z', 100))
                .build();
    }

    public static PendingNotificationVO testIdNotification(MemberVO receiver) {
        return new PendingNotificationVO(
                new ObjectId(randomUtil.generateRandomCode('0', '9', 24)),
                randomUtil.generateRandomCode('A', 'z', 50),
                randomUtil.generateRandomCode('A', 'z', 300),
                receiver.getId().toString(),
                randomUtil.generateRandomCode('A', 'z', 100),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
