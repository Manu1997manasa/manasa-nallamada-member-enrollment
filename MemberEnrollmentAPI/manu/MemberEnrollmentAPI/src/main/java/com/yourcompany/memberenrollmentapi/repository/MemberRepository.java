package com.yourcompany.memberenrollmentapi.repository;

import com.yourcompany.memberenrollmentapi.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
