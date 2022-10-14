package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart; // 하나의 장바구니에는 여러가지 상품을 담을 수 있으므로 @ManyToOne으로 다대일 관계로 매핑

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 담을 상품의 정보를 알아야 하므로 상품 엔티티를 매핑해준다.
                       // 하나의 상품은 여러 장바구니 담길수 있으므로 마찬가지로 @ManyToOne으로 다대일 관계로 매핑한다

    private int count; // 같은 상품을 몇개나 담을건지 카운트(저장)
}