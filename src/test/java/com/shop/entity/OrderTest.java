package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    // 영속성 전이 테스트
    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderprice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); // 영속성 컨테스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담아준다
        }

        orderRepository.saveAndFlush(order); // order 엔티티를 저장하면서 강제로 flush 를 호출하여 영속성 컨텍스트에 있는 객체들을 DB에 반영
        em.clear(); // 영속성 컨텍스트 초기화

        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        // 영속성 컨텍스트를 초기화 했기 때문에 DB에서 주문 엔티티를 조회한다.
        assertEquals(3, savedOrder.getOrderItems().size());
        // itemOrder 엔티티 3개가 실제로 DB에 저장되었는지 확인한다.

    }

    // 고아객체 삭제 테스트
    public Order CreateOrder() { // 주문 데이터 저장 메서드
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderprice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.CreateOrder();
        order.getOrderItems().remove(0); // order 엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 요소를 제거
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest1(){
        Order order = this.CreateOrder(); // 기존에 만들어 두었던 주문 생성 메서드 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        // 영속성 컨텍스트의 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem을 DB에서 다시 조회한다
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);
        // orderItem 엔티티에 있는 order 객체의 클래스를 출력한다. order 클래스가 출력되는 것을 확인할 수 있다.
        System.out.println("Order Class : " + orderItem.getOrder().getClass());
    }
    @Test
    @DisplayName("(Fetch 설정 후)지연 로딩 테스트")
    public void lazyLoadingTest2(){
        Order order = this.CreateOrder(); // 기존에 만들어 두었던 주문 생성 메서드 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        // 영속성 컨텍스트의 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem을 DB에서 다시 조회한다
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);
        // orderItem 엔티티에 있는 order 객체의 클래스를 출력한다. order 클래스가 출력되는 것을 확인할 수 있다.
        System.out.println("Order Class : " + orderItem.getOrder().getClass());
        System.out.println("=".repeat(20));
        orderItem.getOrder().getOrderDate();
        System.out.println("=".repeat(20));
    }

}