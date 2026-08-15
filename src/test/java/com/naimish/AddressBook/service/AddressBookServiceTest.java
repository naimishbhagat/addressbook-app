package com.naimish.AddressBook.service;

import com.naimish.AddressBook.model.AddressBook;
import com.naimish.AddressBook.model.Contact;
import com.naimish.AddressBook.repository.AddressBookRepository;
import com.naimish.AddressBook.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private AddressBookService addressBookService;

    private AddressBook addressBook;
    private Contact contact;

    @BeforeEach
    void setUp() {
        addressBook = new AddressBook();
        addressBook.setId(1L);
        addressBook.setBranch("Melbourne");

        contact = new Contact();
        contact.setContactId(1);
        contact.setName("Naimish");
        contact.setPhoneNo(List.of("0400000000"));
        contact.setAddressBook(addressBook);
    }

    @Test
    void createAddressBook_ignoresClientSuppliedIdSoItAlwaysInserts() {
        AddressBook incoming = new AddressBook();
        incoming.setId(99L);
        incoming.setBranch("Sydney");
        when(addressBookRepository.save(any(AddressBook.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressBook saved = addressBookService.createAddressBook(incoming);

        assertThat(saved.getId()).isNull();
        verify(addressBookRepository).save(incoming);
    }

    @Test
    void addContact_savesContactUnderRequestedAddressBook() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
        Contact newContact = new Contact();
        newContact.setContactId(42);
        newContact.setName("New Person");
        newContact.setPhoneNo(List.of("111"));
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));

        Contact result = addressBookService.addContact(1, newContact);

        assertThat(result).isNotNull();
        assertThat(result.getContactId()).isNull();
        assertThat(result.getAddressBook()).isEqualTo(addressBook);
    }

    @Test
    void addContact_returnsNullWhenAddressBookMissing() {
        when(addressBookRepository.findById(99L)).thenReturn(Optional.empty());

        Contact result = addressBookService.addContact(99, new Contact());

        assertThat(result).isNull();
        verifyNoInteractions(contactRepository);
    }

    @Test
    void getContact_returnsContactWhenItBelongsToAddressBook() {
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        Contact result = addressBookService.getContact(1, 1);

        assertThat(result).isEqualTo(contact);
    }

    @Test
    void getContact_returnsNullWhenContactBelongsToDifferentAddressBook() {
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        Contact result = addressBookService.getContact(2, 1);

        assertThat(result).isNull();
    }

    @Test
    void getContact_returnsNullWhenContactNotFound() {
        when(contactRepository.findById(999)).thenReturn(Optional.empty());

        Contact result = addressBookService.getContact(1, 999);

        assertThat(result).isNull();
    }

    @Test
    void updateContact_updatesNameAndPhoneWhenOwnershipMatches() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));
        Contact updates = new Contact();
        updates.setName("Updated Name");
        updates.setPhoneNo(List.of("999"));

        Contact result = addressBookService.updateContact(1, 1, updates);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getPhoneNo()).containsExactly("999");
        verify(contactRepository).save(contact);
    }

    @Test
    void updateContact_returnsNullWhenContactBelongsToDifferentAddressBook() {
        AddressBook otherBook = new AddressBook();
        otherBook.setId(2L);
        when(addressBookRepository.findById(2L)).thenReturn(Optional.of(otherBook));
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        Contact result = addressBookService.updateContact(2, 1, new Contact());

        assertThat(result).isNull();
        verify(contactRepository, never()).save(any());
    }

    @Test
    void deleteContact_deletesWhenOwnershipMatches() {
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        addressBookService.deleteContact(1, 1);

        verify(contactRepository).deleteById(1);
    }

    @Test
    void deleteContact_doesNothingWhenContactBelongsToDifferentAddressBook() {
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        addressBookService.deleteContact(2, 1);

        verify(contactRepository, never()).deleteById(anyInt());
    }

    @Test
    void getAllContacts_returnsContactsForExistingAddressBook() {
        addressBook.setContacts(List.of(contact));
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(addressBook));

        List<Contact> result = addressBookService.getAllContacts(1);

        assertThat(result).containsExactly(contact);
    }

    @Test
    void getAllContacts_returnsNullWhenAddressBookMissing() {
        when(addressBookRepository.findById(99L)).thenReturn(Optional.empty());

        List<Contact> result = addressBookService.getAllContacts(99);

        assertThat(result).isNull();
    }

    @Test
    void getUniqueContacts_dedupesSameNameAndPhoneAcrossAddressBooks() {
        Contact duplicateInAnotherBook = new Contact();
        duplicateInAnotherBook.setContactId(2);
        duplicateInAnotherBook.setName("Naimish");
        duplicateInAnotherBook.setPhoneNo(List.of("0400000000"));

        when(contactRepository.findAll()).thenReturn(List.of(contact, duplicateInAnotherBook));

        List<Contact> result = addressBookService.getUniqueContacts();

        assertThat(result).hasSize(1);
    }
}
