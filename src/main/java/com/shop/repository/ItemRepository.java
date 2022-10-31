package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
                                                                  // Querydsl을 상속 받아 qdsl을 사용한다.    // ItemRepository에서 Querydsl 구현한 상품 관리 페이지 목록을 불러오는
                                                                                                          // getAdminItemPage() 메서드를 사용할 수 있다.
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm);
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    List<Item> findByPriceLessThan(Integer price);
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail); //@Query 어노테이션을 사용한 findBy 문

    @Query(value = "select * from item i where i.item_Detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
    // @Query 어노테이션을 사용, native 쿼리 문 (쿼리 자체에 접근을 할 수 있지만 특정 쿼리에 종속이 될 가능성이 커서 잘 사용하지 않는다)


}
