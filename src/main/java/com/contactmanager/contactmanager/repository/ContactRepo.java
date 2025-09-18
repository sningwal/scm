package com.contactmanager.contactmanager.repository;

import java.util.*;

import com.contactmanager.contactmanager.entity.Contact;
import com.contactmanager.contactmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    // find the contact by user
    // custom finder method
    Page<Contact> findByUser(User user, Pageable pageable);
    // custom query method
//    @Query("SELECT c FROM Contact c WHERE c.user_id = :userId")
    List<Contact> findByUserId(Long userId);
    Optional<Contact> findById(String id);

    Page<Contact> findByUserAndNameContaining(User user, String namekeyword, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user, String emailkeyword, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phonekeyword, Pageable pageable);

}