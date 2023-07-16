package com.bytespeed.identityrecon.repository;

import com.bytespeed.identityrecon.model.db.PrimaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrimaryRecordRepository extends JpaRepository<PrimaryRecord, Integer> {

    Optional<List<PrimaryRecord>> findByPrimaryContactId(Integer primaryContactId);

}
