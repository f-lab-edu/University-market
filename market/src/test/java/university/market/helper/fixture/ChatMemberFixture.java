package university.market.helper.fixture;

import java.security.NoSuchAlgorithmException;
import university.market.chat.domain.ChatMemberVO;
import university.market.chat.domain.ChatVO;
import university.market.chat.domain.chatauth.ChatAuthType;
import university.market.member.domain.MemberVO;
import university.market.verify.email.utils.random.RandomUtil;
import university.market.verify.email.utils.random.RandomUtilImpl;

public class ChatMemberFixture {

    private static final RandomUtil randomUtil;

    static {
        try {
            randomUtil = new RandomUtilImpl();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChatMemberVO testChatMember(ChatAuthType chatAuthType, ChatVO chat, MemberVO member) {
        return ChatMemberVO.builder()
            .chatAuth(chatAuthType)
            .chat(chat)
            .member(member)
            .build();
    }

    public static ChatMemberVO testIdChatMember(ChatAuthType chatAuthType, ChatVO chat, MemberVO member) {
        return new ChatMemberVO(
                Long.parseLong(randomUtil.generateRandomCode('0','9',10)),
                chatAuthType,
                chat,
                member,
                null
        );
    }
}
