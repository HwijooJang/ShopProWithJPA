package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 주문 정보를 담을 dto
@Getter
@Setter
public class OrderHistDto {

    public OrderHistDto(Order order) { // order객체를 파라미터로 받아 멤버 변수 값을 세팅, 주문 날짜는 포맷을 수정해 yyyy-MM--dd HH:mm 형태로 수정
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM--dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto){ // orderItemDto 객체를 주문 상품 리스트에 추가하는 메서드 이다.
        orderItemDtoList.add(orderItemDto);
    }

}
