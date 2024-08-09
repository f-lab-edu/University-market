package university.market.chat.room.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import university.market.chat.room.annotation.ChatAuth;
import university.market.chat.room.domain.ChatMemberVO;
import university.market.chat.room.domain.ChatVO;
import university.market.chat.room.domain.chatauth.ChatAuthType;
import university.market.chat.room.mapper.ChatMapper;
import university.market.chat.room.mapper.ChatMemberMapper;
import university.market.chat.room.service.dto.ChatCreateRequest;
import university.market.item.service.ItemService;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.service.MemberService;
import university.market.member.utils.auth.PermissionCheck;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ItemService itemService;

    private final MemberService memberService;

    private final ChatMapper chatMapper;

    private final ChatMemberMapper chatMemberMapper;

    private final PermissionCheck permissionCheck;

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    @Transactional
    public ChatVO createChat(ChatCreateRequest request, MemberVO currentMember) {
        ChatVO chat = ChatVO.builder()
                .title(request.title())
                .item(itemService.getItemById(request.itemId()))
                .build();

        chatMapper.createChat(chat);

        request.memberIds()
                .forEach(
                        member -> {
                            MemberVO memberVO = memberService.findMemberById(member);
                            permissionCheck.hasPermission(
                                    () -> memberVO.getAuth().equals(AuthType.ROLE_USER));
                            chatMemberMapper.addMember(ChatMemberVO.builder()
                                    .chatAuth(ChatAuthType.GUEST)
                                    .chat(chat)
                                    .member(memberVO)
                                    .build());
                        }
                );

        chatMemberMapper.addMember(ChatMemberVO.builder()
                .chatAuth(ChatAuthType.HOST)
                .chat(chat)
                .member(currentMember)
                .build());

        return chat;
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public ChatVO getChat(@ChatAuth Long chatId, MemberVO currentMember) {
        ChatMemberVO chatMember = chatMemberMapper.getChatMemberByChatAndMember(chatId,
                currentMember.getId());
        return chatMember.getChat();
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public List<ChatVO> getChatsByMember(MemberVO currentMember) {
        return chatMemberMapper.getChatsByMember(currentMember.getId())
                .stream().map(
                        ChatMemberVO::getChat
                ).collect(Collectors.toList());
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    public List<MemberVO> getMembersByChat(@ChatAuth Long chatId, MemberVO currentMember) {
        List<MemberVO> members = chatMemberMapper.getMembersByChat(chatId).stream().map(
                ChatMemberVO::getMember
        ).toList();

        return members;
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public void deleteChat(Long chatId, MemberVO currentMember) {
        hostPermissionCheck(chatId, currentMember);
        chatMapper.deleteChat(chatId);
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public void addMember(@ChatAuth Long chatId, Long memberId, MemberVO currentMember) {
        chatMemberMapper.addMember(ChatMemberVO.builder()
                .chat(chatMapper.getChat(chatId))
                .member(memberService.findMemberById(memberId))
                .build());
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public void removeMember(@ChatAuth Long chatId, Long memberId, MemberVO currentMember) {
        hostPermissionCheck(chatId, currentMember);
        chatMemberMapper.deleteMember(chatId, memberId);
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public void removeMyself(@ChatAuth Long chatId, MemberVO currentMember) {
        chatMemberMapper.deleteMember(chatId, currentMember.getId());
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public void updateChat(Long chatId, String title, MemberVO currentMember) {
        hostPermissionCheck(chatId, currentMember);
        chatMapper.updateChat(chatId, title);
    }

    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @Override
    public ChatMemberVO getChatMember(Long chatId, Long memberId) {
        return chatMemberMapper.getChatMemberByChatAndMember(chatId, memberId);
    }

    private void hostPermissionCheck(Long chatId, MemberVO currentMember) {
        permissionCheck.hasPermission(
                () -> chatMemberMapper.getChatMemberByChatAndMember(chatId, currentMember.getId())
                        .getChatAuth()
                        != ChatAuthType.HOST && currentMember.getAuth() != AuthType.ROLE_ADMIN);
    }
}
