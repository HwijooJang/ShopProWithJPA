package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mockMvc; // MockMVC를 통해서 실제 객체와 비슷하지만 테스트에 필요한 기능만 가지는 가짜 객체
                             // MockMVC를 이용하면 웹 브라우저에서 요청을 하는거 처럼 테스트가 가능
    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password){ // 회원가입 메서드
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test1@example.com";
        String password = "1234";
        this.createMember(email, password);                                 // 회원가입 메서드를 실행 후 가입된 회원 정보로 로그인이 되는지 테스트를 진행
        mockMvc.perform(formLogin().userParameter("email").loginProcessingUrl("/members/login")
                        .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated()); // 테스트가 통과하면 인증이 된다 (authenticated)
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception {
        String email = "test2@example.com";
        String password = "1234";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email").loginProcessingUrl("/members/login")
                        .user(email).password("12345")) // 비밀번호를 다르게 주었다.
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
        // 회원가입은 정상적으로 진행이 되었지만 로그인 시 다른 암호를 입력하여 인증되지 않은 결과 값이 출력되어 테스트가 통과된다.
    }

}