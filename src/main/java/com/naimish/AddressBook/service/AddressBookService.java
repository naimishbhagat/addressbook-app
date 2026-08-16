package com.naimish.AddressBook.service;

import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.repository.AddressBookRepository;
import com.naimish.AddressBook.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressBookService {

    private final AddressBookRepository addressBookRepository;
    private final ContactRepository contactRepository;

    public AddressBook createAddressBook(AddressBook addressBook) {
        addressBook.setId(null);
        AddressBook saved = addressBookRepository.save(addressBook);
        log.info("Created address book id={} branch={}", saved.getId(), saved.getBranch());
        return saved;
    }

    public List<AddressBook> getAllAddressBooks() {
        log.debug("Fetching all address books");
        return addressBookRepository.findAll();
    }

    public AddressBook getAddressBook(long addressBookId) {
        return addressBookRepository.findById(addressBookId).orElse(null);
    }

    public Contact getContact(long addressBookId,long contactId) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact != null && belongsToAddressBook(contact, addressBookId)) {
            return contact;
        }
        log.warn("Contact id={} not found in address book id={}", contactId, addressBookId);
        return null;
    }

    public Contact updateContact(long addressBookId,long contactId, Contact contact) {
        AddressBook addressBook = getAddressBook(addressBookId);
        Contact existingContact = contactRepository.findById(contactId).orElse(null);
        if(addressBook != null && existingContact != null && belongsToAddressBook(existingContact, addressBookId)){
            existingContact.setName(contact.getName());
            existingContact.setPhoneNo(contact.getPhoneNo());
            Contact saved = contactRepository.save(existingContact);
            log.info("Updated contact id={} in address book id={}", contactId, addressBookId);
            return saved;
        }
        log.warn("Failed to update contact id={} in address book id={}: not found or does not belong to that address book", contactId, addressBookId);
        return null;
    }

    public void deleteContact(long addressBookId,long contactId) {
        Contact existingContact = contactRepository.findById(contactId).orElse(null);
        if(existingContact != null && belongsToAddressBook(existingContact, addressBookId)) {
            contactRepository.deleteById(contactId);
            log.info("Deleted contact id={} from address book id={}", contactId, addressBookId);
        } else {
            log.warn("Failed to delete contact id={} from address book id={}: not found or does not belong to that address book", contactId, addressBookId);
        }
    }

    private boolean belongsToAddressBook(Contact contact, long addressBookId) {
        return contact.getAddressBook() != null && contact.getAddressBook().getId().equals(addressBookId);
    }

    public List<Contact> getAllContacts(long addressBookId) {
        AddressBook addressBook = getAddressBook(addressBookId);
        if(addressBook != null){
            return addressBook.getContacts();
        }
        log.warn("Address book id={} not found", addressBookId);
        return null;
    }


    public Contact addContact(long addressBookId, Contact contact) {
        AddressBook addressBook = getAddressBook(addressBookId);
        if(addressBook != null){
            contact.setContactId(null);
            contact.setAddressBook(addressBook);
            Contact saved = contactRepository.save(contact);
            log.info("Added contact id={} to address book id={}", saved.getContactId(), addressBookId);
            return saved;
        }
        log.warn("Failed to add contact: address book id={} not found", addressBookId);
        return null;
    }

    public List<Contact> getUniqueContacts() {
        List<Contact> uniqueContacts = contactRepository.findAll().stream()
                .distinct()
                .toList();
        log.debug("Computed unique contact set across all address books: size={}", uniqueContacts.size());
        return uniqueContacts;
    }
}
