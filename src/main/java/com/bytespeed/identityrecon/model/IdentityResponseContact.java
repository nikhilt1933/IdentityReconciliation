package com.bytespeed.identityrecon.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class IdentityResponseContact {
    private Integer primaryContactId;
    private Set<String> emails;
    private Set<String> phoneNumbers;
    private Set<Integer> secondaryContactIds;
}



/**
 * {
 * 		"contact":{
 * 			"primaryContactId": number,
 * 			"emails": string[], // first element being email of primary contact
 * 			"phoneNumbers": string[], // first element being phoneNumber of primary contact
 * 			"secondaryContactIds": number[] // Array of all Contact IDs that are "secondary" to the primary contact
 *                }* 	}
 */