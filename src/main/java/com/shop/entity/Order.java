package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 정렬할때 사용하는 order 키워드가 있기 떄문에 orders로 지정한다.
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 한 명의 회원은 여러 번 주문을 할 수 있으므로 주문 엔티티 기준에서 다대일 단반향 매핑을 한다.

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

                                    // cascade : 부모 엔티티의 영속성 상태변화를 자식 엔티티에 모두 전이하는 옵션
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // orphanRemoval : 고아객체 제가
    // 주문 상품 엔티티와 일대다 매핑을 한다. 외래키(order_id)가 order_item 테이블에 있으므로 연관 관계의 주인은
    // orderitem 엔티티 이다. Order 엔티티가 주인이 아니므로 "mappedBy" 속성으로 연관 관계의 주인을 설정, 속성 값으로 order를 적어준 이유는 orderItem에 있는
    // Order에 의해 관리된다는 의미로 해석 즉, 연관 관계의 주인의 필드인 order를 mappedBy의 값으로 세팅하면 된다.
    private List<OrderItem> orderItems = new ArrayList<>(); // 하나의 상품이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑

    // 생성한 주문 객체를 이용해 주문 객체를 만드는 메서드를 작성
    public void addOrderItem(OrderItem orderItem){ // orderitems 에는 주문 상품 정보들을 모아준다. orderItem 객체를 order객체의 orderitems에 추가한다.
        orderItems.add(orderItem);
        orderItem.setOrder(this); // Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계 이므로, orderItem 객체에도 order 객체를 세팅한다.
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member); // 상품을 주문한 회원의 정보를 세팅

        // 상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한 번에 여러 개의 상품을 주문할 수 있다. 따라서 여러개의 주문 상품을
        // 담을 수 있도록 리스트형태로 파라미터 값을 받으며 주문 객체에 orderItem 객체를 추가한다.
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); // 주문 상태는 ORDER로 세팅
        order.setOrderDate(LocalDateTime.now()); // 현재 시간을 주문 시간으로 세팅
        return order;
    }
    public int getTotalPrice(){ // 총 주문 구액을 구하는 메서드 이다.
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 주문 취소 시 주문 수량을 상품의 재고에 더해주고 주문 상태를 취소 상태로 바꿔주는 메서드
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

}
