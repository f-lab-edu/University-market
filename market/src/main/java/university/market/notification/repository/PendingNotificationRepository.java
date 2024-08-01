package university.market.notification.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import university.market.notification.domain.PendingNotificationVO;

public interface PendingNotificationRepository extends MongoRepository<PendingNotificationVO, ObjectId> {
    List<PendingNotificationVO> findByReceiverId(String receiverId);
}
