package com.bytespeed.identityrecon.repository;

import com.bytespeed.identityrecon.model.db.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Integer> {
}
