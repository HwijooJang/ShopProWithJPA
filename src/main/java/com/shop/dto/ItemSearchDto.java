package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    // Qureydsl 을 Spring Data Jpa 와 함께 사용하기 위해서는 사용자 정의 리포지토리를 정의 해야 한다.
    // 총 3단계의 과정으로 구현하면 된다.
    // 1. 사용자 정의 인터페이스 작성 / 2. 사용자 정의 인터페이스 구현 / 3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속

    private String searchDateType;
    // 현재 시간과 산품 등록일을 비교해서 상품 데이터를 조회한다. 조회 시간 기준은 아래와 같다.
    // all : 상품 등록일 전체 / 1d : 최근 하루 동안 등록된 상품 / 1w : 최근 일주일 동안 등록된 상품 / 1m : 최근 한달 동안 등록된 상품 / 6m : 최근 6개월 동안 등록된 상품
    private ItemSellStatus searchSellStatus;
    // 상품의 판매상태를 기준으로 상품 데이터를 조회한다.
    private String searchBy;
    // 상품을 조회할 때 어떤 유형으로 조회할지 선택합니다.
    // itemNm : 상품명 / createBy : 상품 등록자 아이디
    private String searchQuery = "";
    // 조회할 검색어 저장할 변수이다. searchBy가 itemNm일 경우 상품명을 기준으로 검색하고, createBy 일 경우 상품 등록자 아이디 기준으로 검색한다.
}
