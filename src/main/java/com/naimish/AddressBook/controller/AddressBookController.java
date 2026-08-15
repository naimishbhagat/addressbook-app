package com.naimish.AddressBook.controller;
import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.service.AddressBookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/addressbooks")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("")
    public ResponseEntity<AddressBook> createAddressBook(@Valid @RequestBody AddressBook addressBook) {
        log.info("Received request to create address book: branch={}", addressBook.getBranch());
        return new ResponseEntity<>(addressBookService.createAddressBook(addressBook), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<AddressBook>> listAddressBooks() {
        log.debug("Received request to list all address books");
        return new ResponseEntity<>(addressBookService.getAllAddressBooks(), HttpStatus.OK);
    }

    @GetMapping("/{addressBookId}/contacts")
    public ResponseEntity<List<Contact>> listContacts(@PathVariable int addressBookId){
        log.debug("Received request to list contacts for address book id={}", addressBookId);
        List<Contact> contacts = addressBookService.getAllContacts(addressBookId);
        if (contacts == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @PostMapping("/{addressBookId}/contacts")
    public ResponseEntity<?> addContact(@PathVariable int addressBookId, @Valid @RequestBody Contact contact){
        log.info("Received request to add a contact to address book id={}", addressBookId);
        try{
            Contact savedContact = addressBookService.addContact(addressBookId,contact);
            if (savedContact == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(savedContact,HttpStatus.CREATED);
        }catch(Exception e){
            log.error("Failed to add contact to address book id={}", addressBookId, e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getUniqueContacts(){
        log.debug("Received request for unique contacts across all address books");
        return new ResponseEntity<>(addressBookService.getUniqueContacts(), HttpStatus.OK);
    }

    @PutMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<?> updateContact(@PathVariable int addressBookId,@PathVariable int contactId, @Valid @RequestBody Contact contact){
        log.info("Received request to update contact id={} in address book id={}", contactId, addressBookId);
        try{
            AddressBook result = addressBookService.updateContact(addressBookId,contactId,contact);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e){
            log.error("Failed to update contact id={} in address book id={}", contactId, addressBookId, e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<String> removeContact(@PathVariable int addressBookId,@PathVariable int contactId){
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
