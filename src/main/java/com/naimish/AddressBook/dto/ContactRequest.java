package com.naimish.AddressBook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContactRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotEmpty(message = "at least one phone number is required")
    private List<String> phoneNo;
}
