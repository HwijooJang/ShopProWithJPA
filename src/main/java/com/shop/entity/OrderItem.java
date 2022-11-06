package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.engine.profile.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로 주문 상품 기준으로 다대일 단방향 매핑을 설정한다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 한 번의 주문엔 여러 개의 상품을 주문할 수 있으므로 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑을 먼저 설정한다.

    private int orderprice;

    private int count;

    // 주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메서드를 작성
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count); // 주문할 상품과 주문 수량을 세팅한다
        orderItem.setOrderprice(item.getPrice()); // 현재 시간 기준으로 상품 가격을 주문 가격으로 세팅한다. 상품 가격은 시간에 따라서 달라질수있다.
                                                  // 또한 쿠폰이나 할인을 적용하는 케이스들도 있지만 여기서는 고려하지 않는다.
        item.removeStock(count); // 주문 수량만큼 삼품의 재고 수량을 감소 시킨다.
        return orderItem;
    }

    public int getTotalPrice(){ // 주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메서드이다.
        return orderprice*count;
    }

    // 주문 취소 시 주문 수량 만큼 상품의 재고를 더해준다.
    public void cancel(){
        this.getItem().addStock(count);
    }

}
