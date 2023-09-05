package com.yourcompany.memberenrollmentapi.controller;

import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MemberController {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        initializeMembers(); // Initialize members here
    }

    private void initializeMembers() {
        try {
            // Create and save initial member objects
            Member member1 = new Member("manasa reddy", "nallamada", "manasanallamada@gmail.com",
                    parseDate("1997-04-24"), "USA", "72085-3180", "basic");
            memberRepository.save(member1);

            Member member2 = new Member("amthul", "afsa", "amthulafsa@gmail.com",
                    parseDate("1997-05-06"), "India", "3093004718", "premium");
            memberRepository.save(member2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateString);
    }


    @PostMapping("/api/members")
    public ResponseEntity<Member> createMember(@RequestBody @Valid Member member) {
        Member createdMember = memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PutMapping("/api/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody @Valid Member memberDetails) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        member.setFirstName(memberDetails.getFirstName());
        member.setLastName(memberDetails.getLastName());
        member.setAddress(memberDetails.getAddress());
        member.setPhone(memberDetails.getPhone());
        member.setMembershipType(memberDetails.getMembershipType());

        Member updatedMember = memberRepository.save(member);
        return ResponseEntity.ok(updatedMember);
    }

    @GetMapping("/api/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/api/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/api/members/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteMember(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        memberRepository.delete(member);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
