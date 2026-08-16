package com.naimish.AddressBook.controller;

import com.naimish.AddressBook.dto.AddressBookRequest;
import com.naimish.AddressBook.dto.ContactRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressBookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void canCreateAndListMultipleAddressBooks() throws Exception {
        createAddressBook("Sydney");
        createAddressBook("Perth");

        mockMvc.perform(get("/api/addressbooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].branch", hasItems("Sydney", "Perth")));
    }

    @Test
    void canAddListAndRemoveContactsInAnAddressBook() throws Exception {
        int addressBookId = createAddressBook("Brisbane");

        long contactId = addContact(addressBookId, "Alice", "111111");

        mockMvc.perform(get("/api/addressbooks/" + addressBookId + "/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));

        mockMvc.perform(delete("/api/addressbooks/" + addressBookId + "/contacts/" + contactId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/addressbooks/" + addressBookId + "/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void uniqueContactsEndpointDedupesSameContactAcrossAddressBooks() throws Exception {
        int bookOne = createAddressBook("Adelaide");
        int bookTwo = createAddressBook("Hobart");

        addContact(bookOne, "Duplicate Person", "222222");
        addContact(bookTwo, "Duplicate Person", "222222");

        mockMvc.perform(get("/api/addressbooks/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Duplicate Person')]", hasSize(1)));
    }

    @Test
    void deletingContactViaWrongAddressBookReturnsNotFound() throws Exception {
        int bookOne = createAddressBook("Canberra");
        int bookTwo = createAddressBook("Darwin");
        long contactId = addContact(bookOne, "Bob", "333333");

        mockMvc.perform(delete("/api/addressbooks/" + bookTwo + "/contacts/" + contactId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addingContactToNonExistentAddressBookReturnsNotFound() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setName("Nobody");
        request.setPhoneNo(List.of("000"));

        mockMvc.perform(post("/api/addressbooks/999999/contacts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void creatingAddressBookWithBlankBranchReturnsBadRequest() throws Exception {
        AddressBookRequest request = new AddressBookRequest();
        request.setBranch("");

        mockMvc.perform(post("/api/addressbooks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.branch").exists());
    }

    @Test
    void addingContactWithBlankNameReturnsBadRequest() throws Exception {
        int addressBookId = createAddressBook("Newcastle");
        ContactRequest request = new ContactRequest();
        request.setName("");
        request.setPhoneNo(List.of("444444"));

        mockMvc.perform(post("/api/addressbooks/" + addressBookId + "/contacts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void addingContactWithNoPhoneNumbersReturnsBadRequest() throws Exception {
        int addressBookId = createAddressBook("Wollongong");
        ContactRequest request = new ContactRequest();
        request.setName("No Phone");
        request.setPhoneNo(List.of());

        mockMvc.perform(post("/api/addressbooks/" + addressBookId + "/contacts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNo").exists());
    }

    private int createAddressBook(String branch) throws Exception {
        AddressBookRequest request = new AddressBookRequest();
        request.setBranch(branch);

        String response = mockMvc.perform(post("/api/addressbooks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asInt();
    }

    private long addContact(int addressBookId, String name, String phoneNo) throws Exception {
        ContactRequest request = new ContactRequest();
        request.setName(name);
        request.setPhoneNo(List.of(phoneNo));

        String response = mockMvc.perform(post("/api/addressbooks/" + addressBookId + "/contacts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("contactId").asLong();
    }
}
