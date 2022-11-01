package com.shop.repository;

// 사용자 정의 인터페이스 작성

import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage (ItemSearchDto itemSearchDto, Pageable pageable);
    // 상품 조회 조건을 담고 있는 itemSearchDto 객체와 페이징 정보를 담고 있는 pageable 객체를 파라미터로 받는
    // getAdminItemPage 메서드를 정의한다. 반환 데이터로 Page<Item> 객체를 반환한다.

    Page<Item> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
