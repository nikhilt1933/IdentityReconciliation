package com.bytespeed.identityrecon.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IdentityResponse {
    private IdentityResponseContact contact;
}

