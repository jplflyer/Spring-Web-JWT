package org.showpage.springwebjwt.repository;

import org.showpage.springwebjwt.dbmodel.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String value);
}
