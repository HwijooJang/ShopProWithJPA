package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart; // 하나의 장바구니에는 여러가지 상품을 담을 수 있으므로 @ManyToOne으로 다대일 관계로 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 담을 상품의 정보를 알아야 하므로 상품 엔티티를 매핑해준다.
                       // 하나의 상품은 여러 장바구니 담길수 있으므로 마찬가지로 @ManyToOne으로 다대일 관계로 매핑한다

    private int count; // 같은 상품을 몇개나 담을건지 카운트(저장)

    // 상품 엔티티를 생성하는 메서드와 장바구니에 담을 수량을 증가시켜 주는 메서드
    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }
    // 장바구니에 기존에 담겨있는 상품인데, 해당 상품을 추가로 장바구니에 담을시 현재 담을 수량을 더해줄 때 사용할 메서드
    public void addCount(int count){
        this.count += count;
    }
    // 남은 수량을 변경하는 메서드
    public void updateCount(int count){
        this.count = count;
    }
}
