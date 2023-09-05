package com.yourcompany.memberenrollmentapi.security;

import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MyUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                member.getEmail(), member.getPassword(), member.getAuthorities());
    }
}