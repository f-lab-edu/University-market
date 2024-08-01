package university.market.notification.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import university.market.notification.domain.NotificationVO;

public interface NotificationRepository extends MongoRepository<NotificationVO, ObjectId> {
    List<NotificationVO> findByReceiverId(String receiverId);
}
