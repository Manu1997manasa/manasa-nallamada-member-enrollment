package com.yourcompany.memberenrollmentapi.controller; // Replace with the actual package name of your Spring Boot application

import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.MemberEnrollmentApiApplication; // Correct import statement here
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MemberEnrollmentApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        // Clear the database or perform any setup before each test
        memberRepository.deleteAll();
    }

    @Test
    public void testGetMember() {
        // Create a sample member and save it to the database
        Member sampleMember = new Member("John", "Doe", "john.doe@example.com", new Date());
        sampleMember = memberRepository.save(sampleMember);

        // Make a GET request to the API endpoint to fetch the created member
        String url = "http://localhost:" + port + "/api/members/" + sampleMember.getId();
        ResponseEntity<Member> responseEntity = restTemplate.getForEntity(url, Member.class);

        // Verify the response status code (HTTP 200 OK)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Get the response body (Member object)
        Member responseMember = responseEntity.getBody();
        assertNotNull(responseMember);

        // Verify that the retrieved member has the same ID as the sample member
        assertEquals(sampleMember.getId(), responseMember.getId());

        // Add other assertions to verify other properties if needed
        assertEquals(sampleMember.getFirstName(), responseMember.getFirstName());
        assertEquals(sampleMember.getLastName(), responseMember.getLastName());
        assertEquals(sampleMember.getEmail(), responseMember.getEmail());
        assertEquals(sampleMember.getBirthdate(), responseMember.getBirthdate());
    }

    // Add other test methods for different API endpoints and scenarios
}
