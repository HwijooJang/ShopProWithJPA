package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

// 주문 상품 정보를 담을 Dto클래스
@Getter
@Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String ImgUrl){
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderprice();
        this.imgUrl = ImgUrl;
    }

    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;
}
