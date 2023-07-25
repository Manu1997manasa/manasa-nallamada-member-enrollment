package com.yourcompany.memberenrollmentapi.controller;

import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
public class MemberController implements CommandLineRunner {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeMembers();
    }

    // Helper method to create and save members with predefined data
    private void initializeMembers() {
        if (memberRepository.count() == 0) {
            Member member1 = new Member("manasa reddy", "nallamada", "manasanallamada1992@gmail.com", new Date(97, 3, 24));
            memberRepository.save(member1);

            Member member2 = new Member("amthul", "afsa", "amthul@gmail.com", new Date(97, 4, 6));
            memberRepository.save(member2);
        }
    }

    @GetMapping("/api/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        return ResponseEntity.ok(member);
    }

    @PostMapping("/api/members") // Update the request mapping to include "/api"
    public ResponseEntity<String> createMember(@RequestBody Member member) {
        memberRepository.save(member);
        return new ResponseEntity<>("Member enrollment successful", HttpStatus.CREATED);
    }
}
