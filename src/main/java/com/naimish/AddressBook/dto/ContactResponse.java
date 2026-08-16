package com.naimish.AddressBook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ContactResponse {

    private Long contactId;
    private String name;
    private List<String> phoneNo;
}
