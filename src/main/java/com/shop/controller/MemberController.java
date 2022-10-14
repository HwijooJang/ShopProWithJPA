package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

   /* @PostMapping(value = "/new")
    public String memberForm(MemberFormDto memberFormDto){

        Member member = Member.createMember(memberFormDto, passwordEncoder);
        memberService.saveMember(member);

        return "redirect:/";
    }*/

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
                            // 검증하려는 객체안에 @Valid를 선언하고 파라미터로 bindingResult를 추가, 검사 후 결과를 bindingResult에 담고 .hasError를 호출
                            // 에러가 검증이 되면 회원가입 페이지로 이동한다.
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage()); // 중복회원가입 예외가 발생하면 에러 메시지를 뷰로 전달한다.
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
         return "member/memberLoginForm";
    }

}
