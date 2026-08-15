package com.naimish.AddressBook.controller;
import com.naimish.AddressBook.dto.AddressBookMapper;
import com.naimish.AddressBook.dto.AddressBookRequest;
import com.naimish.AddressBook.dto.AddressBookResponse;
import com.naimish.AddressBook.dto.ContactRequest;
import com.naimish.AddressBook.dto.ContactResponse;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.service.AddressBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/addressbooks")
@RequiredArgsConstructor
public class AddressBookController {

    private final AddressBookService addressBookService;

    @PostMapping("")
    public ResponseEntity<AddressBookResponse> createAddressBook(@Valid @RequestBody AddressBookRequest request) {
        log.info("Received request to create address book: branch={}", request.getBranch());
        var saved = addressBookService.createAddressBook(AddressBookMapper.toEntity(request));
        return new ResponseEntity<>(AddressBookMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<AddressBookResponse>> listAddressBooks() {
        log.debug("Received request to list all address books");
        List<AddressBookResponse> response = addressBookService.getAllAddressBooks().stream()
                .map(AddressBookMapper::toResponse)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{addressBookId}/contacts")
    public ResponseEntity<List<ContactResponse>> listContacts(@PathVariable long addressBookId){
        log.debug("Received request to list contacts for address book id={}", addressBookId);
        List<Contact> contacts = addressBookService.getAllContacts(addressBookId);
        if (contacts == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(contacts.stream().map(AddressBookMapper::toResponse).toList(), HttpStatus.OK);
    }

    @PostMapping("/{addressBookId}/contacts")
    public ResponseEntity<ContactResponse> addContact(@PathVariable long addressBookId, @Valid @RequestBody ContactRequest request){
        log.info("Received request to add a contact to address book id={}", addressBookId);
        Contact savedContact = addressBookService.addContact(addressBookId, AddressBookMapper.toEntity(request));
        if (savedContact == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(AddressBookMapper.toResponse(savedContact), HttpStatus.CREATED);
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactResponse>> getUniqueContacts(){
        log.debug("Received request for unique contacts across all address books");
        List<ContactResponse> response = addressBookService.getUniqueContacts().stream()
                .map(AddressBookMapper::toResponse)
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<ContactResponse> updateContact(@PathVariable long addressBookId,@PathVariable int contactId, @Valid @RequestBody ContactRequest request){
        log.info("Received request to update contact id={} in address book id={}", contactId, addressBookId);
        Contact updated = addressBookService.updateContact(addressBookId, contactId, AddressBookMapper.toEntity(request));
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(AddressBookMapper.toResponse(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<String> removeContact(@PathVariable long addressBookId,@PathVariable int contactId){
        log.info("Received request to delete contact id={} from address book id={}", contactId, addressBookId);
        Contact contact = addressBookService.getContact(addressBookId, contactId);
        if(contact != null) {
            addressBookService.deleteContact(addressBookId, contactId);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
