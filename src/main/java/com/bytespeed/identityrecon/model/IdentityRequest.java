package com.bytespeed.identityrecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityRequest {
    private String email;
    private String phoneNumber;
}
