package com.yourcompany.memberenrollmentapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.memberenrollmentapi.entity.Member;
import com.yourcompany.memberenrollmentapi.MemberEnrollmentApiApplication; // Correct import statement here
import com.yourcompany.memberenrollmentapi.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.web.servlet.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MemberEnrollmentApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        // Clear the database or perform any setup before each test
        memberRepository.deleteAll();
    }

    @Test
    public void testUpdateMember() throws Exception {
        // Create a sample member and save it to the database
        Member sampleMember = new Member("John", "Doe", "john.doe@example.com", new Date(), "USA", "1234567890", "Gold");
        sampleMember = memberRepository.save(sampleMember);

        // Create a new Member with updated details
        Member updatedMember = new Member("Updated", "Member", "updated@example.com", new Date(), "USA", "1234567890", "Gold");

        // Perform the update using the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/members/" + sampleMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedMember)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Retrieve the updated member from the repository
        Member retrievedMember = memberRepository.findById(sampleMember.getId())
                .orElseThrow(() -> new RuntimeException("Updated member not found"));

        // Verify that the member details have been updated
        assertEquals(updatedMember.getFirstName(), retrievedMember.getFirstName());
        assertEquals(updatedMember.getLastName(), retrievedMember.getLastName());
        assertEquals(updatedMember.getAddress(), retrievedMember.getAddress());
        assertEquals(updatedMember.getPhone(), retrievedMember.getPhone());
        assertEquals(updatedMember.getMembershipType(), retrievedMember.getMembershipType());
        // Add more assertions if needed
    }

    @Test
    public void testGetAllMembers() throws Exception {
        // Create sample members and save them to the database
        Member member1 = new Member("John", "Doe", "john.doe@example.com", new Date(), "USA", "1234567890", "Gold");
        Member member2 = new Member("Jane", "Smith", "jane.smith@example.com", new Date(), "Canada", "9876543210", "Silver");
        memberRepository.saveAll(List.of(member1, member2));

        // Perform the GET request to retrieve all members
        mockMvc.perform(MockMvcRequestBuilders.get("/api/members"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2)) // Assuming two members were added
                .andReturn();
    }

    @Test
    public void testDeleteMember() throws Exception {
        // Create a sample member and save it to the database
        Member sampleMember = new Member("John", "Doe", "john.doe@example.com", new Date(), "USA", "1234567890", "Gold");
        sampleMember = memberRepository.save(sampleMember);

        // Perform the DELETE request to delete the member
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/members/" + sampleMember.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Verify that the member has been deleted
        assertEquals(0, memberRepository.count());
    }
}
