package university.market.dibs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import university.market.dibs.domain.DibsVO;
import university.market.dibs.exception.DibsException;
import university.market.dibs.exception.DibsExceptionType;
import university.market.dibs.mapper.DibsMapper;
import university.market.helper.fixture.DibsFixture;
import university.market.helper.fixture.ItemFixture;
import university.market.helper.fixture.MemberFixture;
import university.market.item.domain.ItemVO;
import university.market.item.service.ItemService;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.utils.http.HttpRequest;

@ExtendWith(MockitoExtension.class)
public class DibsServiceTest {
    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ItemService itemService;

    @Mock
    private DibsMapper dibsMapper;

    @InjectMocks
    private DibsServiceImpl dibsService;

    private DibsVO dibs;

    @BeforeEach
    public void init() {
        MemberVO seller = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);

        MemberVO member = MemberFixture.testIdMember(AuthType.ROLE_VERIFY_USER);

        ItemVO item = ItemFixture.testIdItem(seller);

        dibs = DibsFixture.testDibs(member, item);
    }

    @Test
    @DisplayName("[success] 찜 추가 성공")
    public void 찜_추가_성공() {
        // mocking
        when(httpRequest.getCurrentMember()).thenReturn(dibs.getMember());
        when(itemService.getItemById(dibs.getItem().getId())).thenReturn(dibs.getItem());

        // when
        dibsService.addDibs(dibs.getItem().getId());

        // then
        verify(dibsMapper).addDibs(dibs);
    }

    @Test
    @DisplayName("[fail] 자기 자신 아이템 찜 추가 실패")
    public void 자기_자신_아이템_찜_추가_실패() {
        // mocking
        when(httpRequest.getCurrentMember()).thenReturn(dibs.getItem().getSeller());
        when(itemService.getItemById(dibs.getItem().getId())).thenReturn(dibs.getItem());

        // when
        DibsException exception = assertThrows(DibsException.class, () -> {
            dibsService.addDibs(dibs.getItem().getId());
        });

        // then
        assertThat(exception.exceptionType().errorCode()).isEqualTo(DibsExceptionType.NO_DIBS_MYSELF.errorCode());
    }
}
