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
@Table(name = "Contact")
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Precedence")
    private String precedence;

    @Column(name = "LinkedId")
    private Integer linkedId;

    @Column(name = "CreatedOn")
    private String createdOn;

    @Column(name = "UpdatedOn")
    private String updatedOn;

    @Column(name = "DeletedOn")
    private String deletedOn;

}
