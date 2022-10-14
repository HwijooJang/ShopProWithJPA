package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member CreateMember(){  // 회원 엔티티를 만드는 메서드
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test1@example.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("종로");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = CreateMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush()를 호출하여 데이터베이스에 반영한다.
                    // 회원 엔티티와 장바구니 엔티티를 영속성 컨텍스트에 저장 후 엔티티 매니저로부터 강제로 flush() 를 호출하여 DB에 반영
        em.clear(); // JPA는 영속성 컨텍스트로부터 엔티티를 조회 후 영속성 컨텍스트에 엔티티가 없을 경우 데이터베이스를 조회한다.
                    // 실제 DB에서 장바구니 엔티티를 가지고 올 때 회원 엔티티도 같이 가지고 오는지 보기위해 영속성 컨텍스트를 비워준다.

        Cart savedCart = cartRepository.findById(cart.getId()) // 저장된 장바구니 엔티티를 조회한다.
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(savedCart.getMember().getId(), member.getId()); // 처음 저장한 member엔티티의 id와 함께 savedCart에 매핑된 member 엔티티의 id를 비교


        // 엔티티를 조회할 때 해당 엔티티와 매핑된 엔티티도 한 번에 조회하는 것을 '즉시 로딩'이라고 한다.
        // 일대일, 다대일로 매핑할 경우 즉시 로딩을 기본 Fetch 전략으로 설정, cart.java 클래스에서 member 엔티티와 일대일 매핑 관계를 맺어줄 때 따로 옵션을 주지 않으면
        // FetchType.Eager(즉시 로딩) 으로 설정하는 것과 동일하다.
    }



}