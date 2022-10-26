package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 정렬할때 사용하는 order 키워드가 있기 떄문에 orders로 지정한다.
@Getter
@Setter
public class Order {

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

    private LocalDateTime regTime;

    private LocalDateTime updateTime;



}
