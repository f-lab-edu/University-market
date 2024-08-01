package university.market.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import university.market.helper.fixture.MemberFixture;
import university.market.helper.fixture.NotificationFixture;
import university.market.helper.fixture.PendingNotificationFixture;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.notification.domain.NotificationVO;
import university.market.notification.domain.PendingNotificationVO;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataMongoTest
public class NotificationRepositoryTest {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PendingNotificationRepository pendingNotificationRepository;

    private MemberVO member;

    @BeforeEach
    public void init() {
        member = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);
    }

    @Test
    @DisplayName("[success] 알림을 저장한다.")
    public void 알림_저장_성공() {
        // given
        NotificationVO notification = NotificationFixture.testNotification(member);

        // when
        NotificationVO savedNotification = notificationRepository.save(notification);

        // then
        assertThat(savedNotification.getId()).isNotNull();
        assertThat(savedNotification.getTitle()).isEqualTo(notification.getTitle());
        assertThat(savedNotification.getContent()).isEqualTo(notification.getContent());
        assertThat(savedNotification.getReceiverId()).isEqualTo(notification.getReceiverId());
        assertThat(savedNotification.getRedirectUrl()).isEqualTo(notification.getRedirectUrl());
    }

    @Test
    @DisplayName("[success] 미전송 알림을 저장한다.")
    public void 미전송_알림_저장_성공() {
        // given
        PendingNotificationVO pendingNotification = PendingNotificationFixture.testIdNotification(member);

        // when
        PendingNotificationVO savedPendingNotification = pendingNotificationRepository.save(pendingNotification);

        // then
        assertThat(savedPendingNotification.getId()).isNotNull();
        assertThat(savedPendingNotification.getTitle()).isEqualTo(pendingNotification.getTitle());
        assertThat(savedPendingNotification.getContent()).isEqualTo(pendingNotification.getContent());
        assertThat(savedPendingNotification.getReceiverId()).isEqualTo(pendingNotification.getReceiverId());
        assertThat(savedPendingNotification.getRedirectUrl()).isEqualTo(pendingNotification.getRedirectUrl());
    }

    @Test
    @DisplayName("[success] receiver의 알람을 전체 받아온다.")
    public void receiver의_알람을_전체_받아온다() {
        // given
        MemberVO member2 = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);
        NotificationVO notification = NotificationFixture.testNotification(member);
        NotificationVO notification2 = NotificationFixture.testNotification(member);
        NotificationVO notification3 = NotificationFixture.testNotification(member);
        NotificationVO notification4 = NotificationFixture.testNotification(member2);
        notificationRepository.save(notification);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        notificationRepository.save(notification4);

        // when
        List<NotificationVO> notifications = notificationRepository.findByReceiverId(member.getId().toString());

        // then
        assertThat(notifications).isNotNull();
        assertThat(notifications).hasSize(3);
    }

    @Test
    @DisplayName("[success] receiver의 미전송 알람을 전체 받아온다.")
    public void receiver의_미전송_알람을_전체_받아온다() {
        // given
        MemberVO member2 = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);
        PendingNotificationVO pendingNotification = PendingNotificationFixture.testIdNotification(member);
        PendingNotificationVO pendingNotification2 = PendingNotificationFixture.testIdNotification(member);
        PendingNotificationVO pendingNotification3 = PendingNotificationFixture.testIdNotification(member);
        PendingNotificationVO pendingNotification4 = PendingNotificationFixture.testIdNotification(member2);
        pendingNotificationRepository.save(pendingNotification);
        pendingNotificationRepository.save(pendingNotification2);
        pendingNotificationRepository.save(pendingNotification3);
        pendingNotificationRepository.save(pendingNotification4);

        // when
        List<PendingNotificationVO> pendingNotifications = pendingNotificationRepository.findByReceiverId(
                member.getId().toString());

        // then
        assertThat(pendingNotifications).isNotNull();
        assertThat(pendingNotifications).hasSize(3);
    }
}
