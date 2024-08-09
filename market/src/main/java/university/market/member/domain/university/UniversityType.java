package university.market.member.domain.university;

import lombok.Getter;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;

@Getter
public enum UniversityType {
    ADMIN(0),
    PUSAN(1),
    SEOUL(2),
    YONSEI(3),
    KOREA(4);
    
    private final int value;

    UniversityType(int value) {
        this.value = value;
    }

    public static UniversityType fromValue(int value) {
        for (UniversityType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new MemberException(MemberExceptionType.NOT_FOUND_UNIVERSITY);
    }
}
