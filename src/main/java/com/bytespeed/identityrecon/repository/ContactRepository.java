package com.bytespeed.identityrecon.repository;

import com.bytespeed.identityrecon.model.db.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    List<Contact> findByEmail(String email);
    List<Contact> findByPhone(String phone);

    @Query(value = "Select Max(Id) From Contact", nativeQuery = true)
    Optional<Integer> findMaxId();
}
