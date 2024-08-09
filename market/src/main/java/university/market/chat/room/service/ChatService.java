package university.market.chat.room.service;

import java.util.List;
import university.market.chat.room.domain.ChatMemberVO;
import university.market.chat.room.domain.ChatVO;
import university.market.chat.room.service.dto.ChatCreateRequest;
import university.market.member.domain.MemberVO;

public interface ChatService {
    ChatVO createChat(ChatCreateRequest request, MemberVO currentMember);

    ChatVO getChat(Long chatId, MemberVO currentMember);

    List<ChatVO> getChatsByMember(MemberVO currentMember);

    List<MemberVO> getMembersByChat(Long chatId, MemberVO currentMember);

    void deleteChat(Long chatId, MemberVO currentMember);

    void addMember(Long chatId, Long memberId, MemberVO currentMember);

    void removeMember(Long chatId, Long memberId, MemberVO currentMember);

    void removeMyself(Long chatId, MemberVO currentMember);

    void updateChat(Long chatId, String title, MemberVO currentMember);

    ChatMemberVO getChatMember(Long chatId, Long memberId);
}
