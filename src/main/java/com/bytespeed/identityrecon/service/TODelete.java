package com.bytespeed.identityrecon.service;

import com.bytespeed.identityrecon.model.IdentityRequest;
import com.bytespeed.identityrecon.model.IdentityResponse;
import com.bytespeed.identityrecon.model.IdentityResponseContact;
import com.bytespeed.identityrecon.model.db.Contact;
import com.bytespeed.identityrecon.model.db.Mapping;
import com.bytespeed.identityrecon.model.db.PrimaryRecord;
import com.bytespeed.identityrecon.repository.ContactRepository;
import com.bytespeed.identityrecon.repository.MappingRepository;
import com.bytespeed.identityrecon.repository.PrimaryRecordRepository;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class TODelete {
}


package com.bytespeed.identityrecon.service;

        import com.bytespeed.identityrecon.model.IdentityRequest;
        import com.bytespeed.identityrecon.model.IdentityResponse;
        import com.bytespeed.identityrecon.model.IdentityResponseContact;
        import com.bytespeed.identityrecon.model.db.Contact;
        import com.bytespeed.identityrecon.model.db.Mapping;
        import com.bytespeed.identityrecon.model.db.PrimaryRecord;
        import com.bytespeed.identityrecon.repository.ContactRepository;
        import com.bytespeed.identityrecon.repository.MappingRepository;
        import com.bytespeed.identityrecon.repository.PrimaryRecordRepository;
        import org.json.JSONArray;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.util.CollectionUtils;
        import org.springframework.util.StringUtils;

        import java.text.SimpleDateFormat;
        import java.util.*;

@Service
 class IdentityReconciliationService1 {

    @Autowired
    public ContactRepository contactRepository;

    @Autowired
    public MappingRepository mappingRepository;

    @Autowired
    public PrimaryRecordRepository primaryRecordRepository;

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final Logger LOGGER = LoggerFactory.getLogger(IdentityReconciliationService.class);

    public IdentityResponse reconcile(IdentityRequest request) {
        if (!isValidRequest(request)) {
            return buildErrorResponse();
        }

        Integer primaryId;
        LOGGER.info("Performing match for email and phone");
        List<Contact> emailMatch, phoneMatch;
        emailMatch = phoneMatch = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getEmail()) &&
                !"null".equalsIgnoreCase(request.getEmail())) {
            emailMatch = contactRepository.findByEmail(request.getEmail());
        }
        if (!StringUtils.isEmpty(request.getPhoneNumber()) &&
                !"null".equalsIgnoreCase(request.getPhoneNumber())) {
            phoneMatch = contactRepository.findByPhone(request.getPhoneNumber());
        }

        if (CollectionUtils.isEmpty(emailMatch) && CollectionUtils.isEmpty(phoneMatch)) {
            primaryId = createNewContactV2(request);
            createPrimaryRecord(primaryId, primaryId);
            createNewMapping(primaryId, "[]");
        } else if (CollectionUtils.isEmpty(emailMatch) || CollectionUtils.isEmpty(phoneMatch)) {
            LOGGER.info("Email or Phone not matching in DB");
            List<Contact> matchingContactList = CollectionUtils.isEmpty(emailMatch) ? phoneMatch : emailMatch;
            Integer matchingContactId = matchingContactList.get(0).getId();
            Optional<PrimaryRecord> primaryRecord = primaryRecordRepository.findById(matchingContactId);
            LOGGER.info("Primary record for id : {}, {}", matchingContactId, primaryRecord);
            primaryId = primaryRecord.get().getPrimaryContactId();
            LOGGER.info("Got primary ID : {}", primaryId);
            if (!StringUtils.isEmpty(request.getEmail()) &&
                    !StringUtils.isEmpty(request.getPhoneNumber()) &&
                    !"null".equalsIgnoreCase(request.getEmail()) &&
                    !"null".equalsIgnoreCase(request.getPhoneNumber())) {
                LOGGER.info("New Email or Phone in present in request");
                Integer newContactId = createNewContactV2(request);
                createPrimaryRecord(newContactId, primaryId);
                updateMapping(newContactId, primaryId);
            }
        } else {
            LOGGER.info("Email and Phone both matches");
            Integer emailMatchPrimaryId = primaryRecordRepository
                    .findById(emailMatch.get(0).getId()).get().getPrimaryContactId();
            Integer phoneMatchPrimaryId = primaryRecordRepository
                    .findById(phoneMatch.get(0).getId()).get().getPrimaryContactId();
            if (emailMatchPrimaryId == phoneMatchPrimaryId) {
                LOGGER.info("Email and Phone match both have same primary id");
                primaryId = emailMatchPrimaryId;
            } else {
                LOGGER.info("Email and Phone match both have different primary id {} {}",
                        emailMatchPrimaryId, phoneMatchPrimaryId);
                Contact oldestContact = findOldestContactById(emailMatchPrimaryId, phoneMatchPrimaryId);
                assert oldestContact != null;
                Integer updatePrimaryId = emailMatchPrimaryId == oldestContact.getId() ?
                        phoneMatchPrimaryId : emailMatchPrimaryId;
                LOGGER.info("Updating newer contact with the older id");
                Contact updateContact = contactRepository.findById(updatePrimaryId).get();
                updateContact.setLinkedId(oldestContact.getId());
                updateContact.setPrecedence("secondary");
                contactRepository.save(updateContact);
                LOGGER.info("Updated newer contact with the older id details");
                Optional<List<PrimaryRecord>> linkedContacts = primaryRecordRepository
                        .findByPrimaryContactId(updateContact.getId());
                if (Optional.ofNullable(linkedContacts).isPresent()) {
                    LOGGER.info("Linked contacts : {}", linkedContacts.get());
                    linkedContacts.get().forEach(record -> {
                        record.setPrimaryContactId(oldestContact.getId());
                        primaryRecordRepository.save(record);
                    });
                }
                LOGGER.info("Updated primary records for the newer contact with older primary id");
                updateMappings(oldestContact.getId(), updateContact.getId());
                primaryId = oldestContact.getId();
            }
        }
        LOGGER.info("Primary Id : {}", primaryId);

        return buildResponse(primaryId);
    }

    private IdentityResponse buildResponse(Integer primaryId) {
        LOGGER.info("Building response object");
        Contact primaryContact = contactRepository.findById(primaryId).get();
        Mapping mapping = mappingRepository.findById(primaryId).get();
        Set<String> emailList = new HashSet<>(List.of(primaryContact.getEmail()));
        Set<String> phoneList = new HashSet<>(List.of(primaryContact.getPhone()));
        Set<Integer> secondaryContactList = new HashSet<>();
        JSONArray mappingArray = new JSONArray(mapping.getSecondaryMapping());
        mappingArray.toList().stream()
                .forEach(contactId -> {
                    Optional<Contact> contact = contactRepository.findById((Integer) contactId);
                    Optional.ofNullable(contact).ifPresent(record -> {
                        if(!StringUtils.isEmpty(record.get().getEmail()))
                            emailList.add(record.get().getEmail());
                        if(!StringUtils.isEmpty(record.get().getPhone()))
                            phoneList.add(record.get().getPhone());
                        secondaryContactList.add(record.get().getId());
                    });
                });
        IdentityResponseContact responseContact = IdentityResponseContact.builder()
                .primaryContactId(primaryId)
                .emails(emailList)
                .phoneNumbers(phoneList)
                .secondaryContactIds(secondaryContactList)
                .build();
        return IdentityResponse.builder()
                .contact(responseContact)
                .build();
    }

    private void updateMappings(Integer primaryId, Integer idToDelete) {
        LOGGER.info("Updating mappings for ID : {}, {}", primaryId, idToDelete);
        Mapping primaryMapping = mappingRepository.findById(primaryId).get();
        Mapping mappingToDelete = mappingRepository.findById(idToDelete).get();
        String primaryMappingArray = primaryMapping.getSecondaryMapping();
        String mappingArrayToDelete = mappingToDelete.getSecondaryMapping();
        String mergedMappingArray = primaryMappingArray.concat(mappingArrayToDelete).replace("][", ",");
        LOGGER.info("Merged Mappings : {}", mergedMappingArray);
        primaryMapping.setSecondaryMapping(mergedMappingArray);
        mappingRepository.delete(mappingToDelete);
        LOGGER.info("Updated Mappings successfully");
    }

    private Contact findOldestContactById(Integer firstId, Integer secondId) {
        LOGGER.info("Finding oldest contact");
        try {
            Contact firstContact = contactRepository.findById(firstId).get();
            Contact secondContact = contactRepository.findById(secondId).get();
            Date firstDate = FORMAT.parse(firstContact.getCreatedOn());
            Date secondDate = FORMAT.parse(secondContact.getCreatedOn());
            LOGGER.info("Dates : {} {}", firstDate, secondDate);
            return firstDate.before(secondDate) ? firstContact : secondContact;
        } catch (Exception e) {
            LOGGER.error("Error occurred while parsing date", e);
            return null;
        }
    }

    private void updateMapping(Integer newContactId, Integer primaryId) {
        LOGGER.info("Updating mappings");
        Mapping mapping = mappingRepository.findById(primaryId).get();
        JSONArray mappingArray = new JSONArray(mapping.getSecondaryMapping());
        LOGGER.info("Mapping array for Id : {}, {}", primaryId, mappingArray);
        mappingArray.put(newContactId);
        mapping.setSecondaryMapping(mappingArray.toString());
        mappingRepository.save(mapping);
        LOGGER.info("Saved successfully. New Mapping array for Id : {}, {}.", primaryId, mappingArray);
    }

    private Integer createNewContactV2(IdentityRequest request) {
        LOGGER.info("Creating new contact");
        Contact newContact = buildContact(request);
        contactRepository.save(newContact);
        LOGGER.info("New Contact created successfully");
        return contactRepository.findMaxId().get();
    }

    private void createPrimaryRecord(Integer contactId, Integer primaryId) {
        LOGGER.info("Creating new primary record");
        PrimaryRecord newPrimaryRecord = PrimaryRecord.builder()
                .Id(contactId)
                .primaryContactId(primaryId)
                .build();
        primaryRecordRepository.save(newPrimaryRecord);
        LOGGER.info("New primary record created successfully");
    }

    private void createNewMapping(Integer id, String mappingArray) {
        LOGGER.info("Creating new mapping");
        Mapping newMapping = Mapping.builder()
                .Id(id)
                .secondaryMapping(mappingArray)
                .build();
        mappingRepository.save(newMapping);
        LOGGER.info("New mapping created successfully");
    }

    private Contact buildContact(IdentityRequest request) {
        return Contact.builder()
                .email(request.getEmail())
                .phone(request.getPhoneNumber())
                .precedence("primary")
                .linkedId(-1)
                .createdOn(FORMAT.format(new Date()))
                .updatedOn(FORMAT.format(new Date()))
                .build();
    }

    private List<Contact> createNewContact(List<Contact> emailMatch, List<Contact> phoneMatch,
                                           IdentityRequest request) {
        List<Contact> matchingContactList = CollectionUtils.isEmpty(emailMatch) ? phoneMatch :
                CollectionUtils.isEmpty(phoneMatch) ? emailMatch : new ArrayList<>();
        Contact matchingContact = !matchingContactList.isEmpty() ? matchingContactList.get(0) : null;
        int linkedId = Optional.ofNullable(matchingContact).isPresent() ? matchingContact.getLinkedId() : -1;
        String precedence = linkedId == -1 ? "primary" : "secondary";
        Contact newContact = Contact.builder()
                .email(request.getEmail())
                .phone(request.getPhoneNumber())
                .precedence(precedence)
                .linkedId(linkedId)
                .createdOn(new Date().toString())
                .updatedOn(new Date().toString())
                .build();
        contactRepository.save(newContact);
        matchingContactList.add(newContact);
        return matchingContactList;
    }

    private IdentityResponse buildErrorResponse() {
        return IdentityResponse.builder()
                .contact(null)
                .build();
    }

    private boolean isValidRequest(IdentityRequest request) {
        return Optional.ofNullable(request.getEmail()).isPresent() ||
                Optional.ofNullable(request.getPhoneNumber()).isPresent();
    }
}
