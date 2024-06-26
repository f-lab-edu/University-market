package university.market.item.mapper;

import org.apache.ibatis.annotations.Mapper;
import university.market.item.domain.ItemVO;

@Mapper
public interface ItemMapper {
    void postItem(ItemVO itemVO);

    ItemVO getItemById(Long id);

    void updateItem(Long id, ItemVO item);

    void deleteItem(Long id);
}
