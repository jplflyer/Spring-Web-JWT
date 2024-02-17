package org.showpage.demo.repository;

import org.showpage.demo.dbmodel.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Access to the Member table.
 */
public interface MemberRepository extends JpaRepository<Member, Integer> {
    /**
     * Find a user by their username.
     * @param value The username
     * @return An Optional of the Member record
     */
    Optional<Member> findByUsername(String value);
}
