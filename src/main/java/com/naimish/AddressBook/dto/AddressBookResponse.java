package com.naimish.AddressBook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AddressBookResponse {

    private Long id;
    private String branch;
    private List<ContactResponse> contacts;
}
