package com.naimish.AddressBook.service;

import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.repository.AddressBookRepository;
import com.naimish.AddressBook.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private ContactRepository contactRepository;

    public AddressBook createAddressBook(AddressBook addressBook) {
        addressBook.setId(null);
        return addressBookRepository.save(addressBook);
    }
    public List<AddressBook> getAllAddressBooks() {
        return addressBookRepository.findAll();
    }

    public AddressBook getAddressBook(int addressBookId) {
        return addressBookRepository.findById(addressBookId).orElse(null);
    }

    public Contact getContact(int addressBookId,int contactId) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact != null && belongsToAddressBook(contact, addressBookId)) {
            return contact;
        }
        return null;
    }

    public AddressBook updateContact(int addressBookId,int contactId, Contact contact) {
        AddressBook addressBook = getAddressBook(addressBookId);
        Contact existingContact = contactRepository.findById(contactId).orElse(null);
        if(addressBook != null && existingContact != null && belongsToAddressBook(existingContact, addressBookId)){
            existingContact.setName(contact.getName());
            existingContact.setPhoneNo(contact.getPhoneNo());
            contactRepository.save(existingContact);
            return addressBook;
        }
        return null;
    }

    public void deleteContact(int addressBookId,int contactId) {
        Contact existingContact = contactRepository.findById(contactId).orElse(null);
        if(existingContact != null && belongsToAddressBook(existingContact, addressBookId)) {
            contactRepository.deleteById(contactId);
        }
    }

    private boolean belongsToAddressBook(Contact contact, int addressBookId) {
        return contact.getAddressBook() != null && contact.getAddressBook().getId().equals(addressBookId);
    }

    public List<Contact> getAllContacts(int addressBookId) {
        AddressBook addressBook = getAddressBook(addressBookId);
        if(addressBook != null){
            return addressBook.getContacts();
        }
        return null;
    }


    public Contact addContact(int addressBookId, Contact contact) {
        AddressBook addressBook = getAddressBook(addressBookId);
        if(addressBook != null){
            contact.setContactId(null);
            contact.setAddressBook(addressBook);
            return contactRepository.save(contact);
        }
        return null;
    }

    public List<Contact> getUniqueContacts() {
        return contactRepository.findAll().stream()
                .distinct()
                .toList();
    }
}
