package org.showpage.demo.dbmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * One Member in the database.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
public class Member implements UserDetails {
    /**
     * Default constructor.
     */
    public Member() {
    }

    /** Primary Key */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_jpa_sequence_generator")
    @SequenceGenerator(name = "member_jpa_sequence_generator", sequenceName = "member_id_seq", allocationSize = 1)
    Integer id;

    /** Username. We prefer email addresses. */
    String username;

    /** Encrypted password. */
    String password;

    /** User Role -- Admin, Member, et cetera. */
    @Enumerated(EnumType.STRING)
    UserRole role;

    /** When this user was first created. */
    Timestamp createdAt;

    //======================================================================
    // Methods from UserDetails. These are largely boilerplate. You can
    // make them complicated if there's a reason to do so.
    //======================================================================

    /**
     * Return the list of user roles for UserDetails. These are just a List
     * of strings (effectively) and can be used to secure portions of your
     * URL space based on role.
     *
     * @return The list of roles. We only support one role per user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Is this account non-expired?
     *
     * @return True normally. False if the account is marked expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Is this account locked?
     *
     * @return True normally. false if the account is marked locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Are the creds non-expired?
     *
     * @return True normally. False if the creds are expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Is the account enabled?
     *
     * @return True normally.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
