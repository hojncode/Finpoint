package com.fp.finpoint.domain.member.repository;

import com.fp.finpoint.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByCode(String code);
    boolean existsByEmail(String email);
}
