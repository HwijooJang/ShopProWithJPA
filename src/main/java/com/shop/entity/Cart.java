package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 회원 엔티티와 일대일로 매핑을 진행
    @JoinColumn(name = "member_id") // 매핑을 할 외래키를 지정. name 속성에는 매핑할 외래키의 이름을 설정합니다.
                                    // @JoinColum에 name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는대로 생성이 되지 않을 수 있다.
    private Member member;

}
