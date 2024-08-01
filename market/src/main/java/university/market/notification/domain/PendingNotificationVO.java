package university.market.notification.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pending_notification")
@Getter
public class PendingNotificationVO {
    @Id
    private ObjectId id;

    private String title;

    private String content;

    private String receiverId;

    private String redirectUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Builder
    public PendingNotificationVO(String title, String content, String receiverId, String redirectUrl) {
        this.title = title;
        this.content = content;
        this.receiverId = receiverId;
        this.redirectUrl = redirectUrl;
    }
}
