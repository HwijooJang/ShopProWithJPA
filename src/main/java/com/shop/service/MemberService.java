package com.shop.service;

import com.shop.entity.Member;
import com.shop.entity.QItem;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;

@Service
@Transactional
@RequiredArgsConstructor // final이나 @Notnull 이 붙은 생성자를 생성해준다.
public class MemberService implements UserDetailsService {
                                        // UserDetailService는 DB에서 회원정보를 가져오는 역할을 담당한다.
    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override //UserDetailService를 사용하면 implement되며 회원정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetails 인터페이스를 반환한다.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email); // 로그인할 유저의 email을 파라미터로 전달받는다.

        if(member == null){
            throw new UsernameNotFoundException(email); // 예외처리문
        }
        return User.builder() // User 생성을 위해 회원의 이메일, 비밀번호, role 을 파라미터로 넘긴다.
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
