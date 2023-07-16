package com.bytespeed.identityrecon.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PrimaryRecord")
@Entity
public class PrimaryRecord {
    @Id
    @Column(name = "ContactID")
    private Integer Id;

    @Column(name = "PrimaryContactId")
    private Integer primaryContactId;

}
