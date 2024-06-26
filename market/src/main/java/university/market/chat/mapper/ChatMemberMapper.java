package university.market.chat.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import university.market.chat.domain.ChatMemberVO;

@Mapper
public interface ChatMemberMapper {

    List<ChatMemberVO> getChatsByMember(Long memberId);

    List<ChatMemberVO> getMembersByChat(Long chatId);

    ChatMemberVO getChatMemberByChatAndMember(Long chatId, Long memberId);

    void addMember(ChatMemberVO chatMember);

    void deleteMember(Long chatId, Long memberId);
}
