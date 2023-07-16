package com.bytespeed.identityrecon.controller;

import com.bytespeed.identityrecon.model.IdentityRequest;
import com.bytespeed.identityrecon.model.IdentityResponse;
import com.bytespeed.identityrecon.service.IdentityReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityReconciliationController {

    private final Logger LOGGER = LoggerFactory.getLogger(IdentityReconciliationService.class);
    @Autowired
    public IdentityReconciliationService identityReconciliationService;

    @PostMapping(path = "/identify", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public IdentityResponse identity(@RequestBody IdentityRequest request) {
        LOGGER.info("Request received : {}", request);
        return identityReconciliationService.reconcile(request);
    }
}
