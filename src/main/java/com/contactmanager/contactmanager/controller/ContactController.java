package com.contactmanager.contactmanager.controller;
import com.contactmanager.contactmanager.entity.Contact;
import com.contactmanager.contactmanager.entity.User;
import com.contactmanager.contactmanager.exception.ResourceNotFoundException;
import com.contactmanager.contactmanager.payload.requestDto.ContactRequestDto;
import com.contactmanager.contactmanager.payload.requestDto.ContactSearchForm;
import com.contactmanager.contactmanager.payload.responseDto.ApiResponse;
import com.contactmanager.contactmanager.payload.responseDto.ResponseUtil;
import com.contactmanager.contactmanager.repository.ContactRepo;
import com.contactmanager.contactmanager.service.impl.ContactServiceImpl;
import com.contactmanager.contactmanager.utils.AppConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/contacts")
public class ContactController {
    private final Logger logger = LoggerFactory.getLogger(ContactController.class);
    private ContactServiceImpl contactService;
    @Autowired
    private ContactRepo contactRepo;
    @PostMapping
    public ResponseEntity<?> saveContact(@Valid @RequestBody ContactRequestDto contactRequestDTO, Authentication authentication) {
        Contact contact = new Contact();
        contact.setName(contactRequestDTO.getName());
        contact.setEmail(contactRequestDTO.getEmail());
        contact.setPhoneNumber(contactRequestDTO.getPhoneNumber());
        contact.setAddress(contactRequestDTO.getAddress());
        System.out.println(contactRequestDTO.getName());
        // User need to add
        User user = (User) authentication.getPrincipal();
//        System.out.println();
        contact.setUser(user);
//        System.out.println(authentication);
        contact.setId(UUID.randomUUID().toString());
        contactRepo.save(contact);
        return ResponseEntity.status(HttpStatus.OK).body("saved successfully");
    }
    @GetMapping
    public ResponseEntity<ApiResponse> getContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {
        logger.info("load all the user contacts");
        User user = (User) authentication.getPrincipal();
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        Page<Contact> pageContact =  contactRepo.findByUser(user, pageable);
        ApiResponse apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"records fetched successfully.",pageContact,null);
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    // view contact
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponse> getContactById(
            @PathVariable("contactId") String contactId,Authentication authentication) {
        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact with ID " + contactId + " not found"));
        Long userId = contact.getUser().getId();
        User user = (User) authentication.getPrincipal();
        if(!Objects.equals(user.getId(),userId))
        {
            ApiResponse apiResponse = ResponseUtil.error(HttpStatus.FORBIDDEN.value(),"You do not have permission to access this resource.","Forbidden",null,null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
        ApiResponse apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"record fetched successfully.",contact,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
     // detete contact
    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(
            @PathVariable("contactId") String contactId,
            Authentication authentication) {
        logger.info("contactId {} deleted", contactId);
        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact with ID " + contactId + " not found"));
        User user = (User) authentication.getPrincipal();
        Long userId = contact.getUser().getId();
        if(!Objects.equals(user.getId(),userId))
        {
            ApiResponse apiResponse = ResponseUtil.error(HttpStatus.FORBIDDEN.value(),"You do not have permission to access this resource.","Forbidden",null,null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
        contactRepo.delete(contact);
        ApiResponse apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"deleted successfully",null,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PutMapping(value = "/{contactId}")
    public ResponseEntity<?> updateContact(@PathVariable("contactId") String contactId,
                                @Valid @RequestBody ContactRequestDto contactRequestDTO, Authentication authentication) {
        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact with ID " + contactId + " not found"));
        User user = (User) authentication.getPrincipal();
        Long userId = contact.getUser().getId();
        if(!Objects.equals(user.getId(),userId))
        {
            ApiResponse apiResponse = ResponseUtil.error(HttpStatus.FORBIDDEN.value(),"You do not have permission to access this resource.","Forbidden",null,null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
        contact.setName(contactRequestDTO.getName());
        contact.setEmail(contactRequestDTO.getEmail());
        contact.setPhoneNumber(contactRequestDTO.getPhoneNumber());
        contact.setAddress(contactRequestDTO.getAddress());
        contactRepo.save(contact);
        logger.info("contactId {} updated", contactId);
        ApiResponse apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"updated successfully",null,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @RequestMapping("/search")
    public ResponseEntity<?> searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {

            String nameKeyword = contactSearchForm.getValue();
            Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            var pageable = PageRequest.of(page, size, sort);
            pageContact = contactRepo.findByUserAndNameContaining(user, nameKeyword,pageable);

        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {

            String emailKeyword = contactSearchForm.getValue();
            Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            var pageable = PageRequest.of(page, size, sort);
            pageContact = contactRepo.findByUserAndEmailContaining(user,emailKeyword,pageable);

        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {

            String phoneKeyword = contactSearchForm.getValue();
            Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            var pageable = PageRequest.of(page, size, sort);
            pageContact = contactRepo.findByUserAndPhoneNumberContaining(user,phoneKeyword,pageable);
        }
        ApiResponse apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"records fetched successfully.",pageContact,null);
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}