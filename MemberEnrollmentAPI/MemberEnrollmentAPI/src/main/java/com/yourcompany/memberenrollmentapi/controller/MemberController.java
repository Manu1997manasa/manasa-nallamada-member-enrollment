package com.yourcompany.memberenrollmentapi.controller;

import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import com.yourcompany.memberenrollmentapi.security.AuthenticationRequest;
import com.yourcompany.memberenrollmentapi.security.AuthenticationResponse;
import com.yourcompany.memberenrollmentapi.security.JwtUtil;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberController(MemberRepository memberRepository,
                            AuthenticationManager authenticationManager,
                            UserDetailsService userDetailsService,
                            JwtUtil jwtUtil,
                            PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        initializeMembers();
    }

    private void initializeMembers() {
        try {
            // Create and save initial member objects
            Member member1 = new Member("manasa reddy", "nallamada", "manasanallamada@gmail.com",
                    parseDate("1997-04-24"), "USA", "72085-3180", "basic", "ROLE_USER", Collections.singleton("CREATE_MEMBER"));
            member1.setPassword(passwordEncoder.encode("password1")); // Set and hash the password
            memberRepository.save(member1);

            Member member2 = new Member("amthul", "afsa", "amthulafsa@gmail.com",
                    parseDate("1997-05-06"), "India", "3093004718", "premium", "ROLE_ADMIN", Collections.singleton("UPDATE_MEMBER"));
            member2.setPassword(passwordEncoder.encode("password2")); // Set and hash the password
            memberRepository.save(member2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateString);
    }


    // Other methods as you provided...

    @PostMapping("/api/members")
    @PreAuthorize("hasAuthority('CREATE_MEMBER')")
    public ResponseEntity<Member> createMember(@RequestBody @Valid Member member) {
        if (!authenticationHasPermission("CREATE_MEMBER")) {
            throw new AccessDeniedException("Unauthorized to create members");
        }

        String hashedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(hashedPassword);

        Member createdMember = memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PutMapping("/api/members/{id}")
    @PreAuthorize("hasAuthority('UPDATE_MEMBER')")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody @Valid Member memberDetails) {
        if (!authenticationHasPermission("UPDATE_MEMBER")) {
            throw new AccessDeniedException("Unauthorized to update members");
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        // Update other fields as needed
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
        if (!authenticationHasPermission("VIEW_MEMBERS")) {
            throw new AccessDeniedException("Unauthorized to view members");
        }
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

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    private boolean authenticationHasPermission(String permission) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return member.getPermissions().contains(permission);
    }
}
