package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.List;

// 인터페이스를 구현하는 Impl 클래스를 작성. Querydsl 에서는 BooleanExpression 이라는 where 절에서 사용할 수 있는 값을 지원한다.
// BooleanExpression 을 반환하는 메서드를 만들고 해당 조건들을 다른 쿼리를 생성할 때 사용할 수 있기 때문에 중복을 줄일 수 있다는 장점이 있음
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{ // 상속

    private JPAQueryFactory queryFactory; // 동적으로 쿼리를 생성하기 위해서 JPAQueryFactory 클래스를 사용합니다.

    public ItemRepositoryCustomImpl(EntityManager em){ // JpaQueryFactory 생성자로 Entity Manager 클래스를 사용합니다.
        this.queryFactory = new JPAQueryFactory(em);
    }

            // 상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴한다. 결과값이 null이면 where절에서 해당 조건은 무시된다.
            // 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회한다.
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
            // seachDataType 의 값에 따라서 dateTime 의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회한다.
            // 예를들어 searchDataType 값이 "1m" 일 경우 dateTime 의 시간을 한 달 전으로 세팅 후 최근 한 달 동안 등록된 상품만 조회하도록 조건값을 반환한다.
    }

    // seachDataType 의 값에 따라서 dateTime 의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회한다.
    // 예를들어 searchDataType 값이 "1m" 일 경우 dateTime 의 시간을 한 달 전으로 세팅 후 최근 한 달 동안 등록된 상품만 조회하도록 조건값을 반환한다.
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    // searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // queryFactory 를 이용해 쿼리를 생성한다. 쿼리문을 직접 작성할때의 형태와 비슷하다
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item) // 상품 데이터를 조회하기 위해서 QItem 의 item 단위를 지정
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),// BooleanExpression 반환하는 조건문들을 넣어준다. "," 단위로 넣어줄 경우 and 조건으로 인식
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스를 지정한다.
                .limit(pageable.getPageSize()) // 한 번에 가지고 올 최대 개수를 지정한다.
                .fetchResults(); // 조회한 리스트 및 전체 개수를 포함하는 QueryResults 를 반환한다.
                                 // 상품 데이터 리스트 조회 및 상품 데이터 전체 개수를 조회하는 2번의 쿼리문이 실행된다.

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total); // 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환한다.
    }
}
