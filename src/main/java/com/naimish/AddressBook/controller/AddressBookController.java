package com.naimish.AddressBook.controller;
import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addressbooks")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("")
    public ResponseEntity<AddressBook> createAddressBook(@RequestBody AddressBook addressBook) {
        return new ResponseEntity<>(addressBookService.createAddressBook(addressBook), HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<List<AddressBook>> listAddressBooks() {
        return new ResponseEntity<>(addressBookService.getAllAddressBooks(), HttpStatus.OK);
    }

    @GetMapping("/{addressBookId}/contacts")
    public ResponseEntity<List<Contact>> listContacts(@PathVariable int addressBookId){
        List<Contact> contacts = addressBookService.getAllContacts(addressBookId);
        if (contacts == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @PostMapping("/{addressBookId}/contacts")
    public ResponseEntity<?> addContact(@PathVariable int addressBookId, @RequestBody Contact contact){
        try{
            Contact savedContact = addressBookService.addContact(addressBookId,contact);
            if (savedContact == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(savedContact,HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getUniqueContacts(){
        return new ResponseEntity<>(addressBookService.getUniqueContacts(), HttpStatus.OK);
    }

    @PutMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<?> updateContact(@PathVariable int addressBookId,@PathVariable int contactId, @RequestBody Contact contact){
        try{
            AddressBook result = addressBookService.updateContact(addressBookId,contactId,contact);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{addressBookId}/contacts/{contactId}")
    public ResponseEntity<String> removeContact(@PathVariable int addressBookId,@PathVariable int contactId){
        Contact contact = addressBookService.getContact(addressBookId, contactId);
        if(contact != null) {
            addressBookService.deleteContact(addressBookId, contactId);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
