package com.bytespeed.identityrecon.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MAPPING")
@Entity
public class Mapping {
    @Id
    @Column(name = "PRIMARYCONTACTID")
    private Integer Id;

    @Column(name = "SECONDARYMAPPING")
    private String secondaryMapping;

}
