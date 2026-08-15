package com.naimish.AddressBook.dto;

import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;

import java.util.Collections;
import java.util.List;

public final class AddressBookMapper {

    private AddressBookMapper() {
    }

    public static AddressBook toEntity(AddressBookRequest request) {
        AddressBook addressBook = new AddressBook();
        addressBook.setBranch(request.getBranch());
        return addressBook;
    }

    public static AddressBookResponse toResponse(AddressBook addressBook) {
        List<ContactResponse> contacts = addressBook.getContacts() == null
                ? Collections.emptyList()
                : addressBook.getContacts().stream().map(AddressBookMapper::toResponse).toList();
        return new AddressBookResponse(addressBook.getId(), addressBook.getBranch(), contacts);
    }

    public static Contact toEntity(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhoneNo(request.getPhoneNo());
        return contact;
    }

    public static ContactResponse toResponse(Contact contact) {
        return new ContactResponse(contact.getContactId(), contact.getName(), contact.getPhoneNo());
    }
}
