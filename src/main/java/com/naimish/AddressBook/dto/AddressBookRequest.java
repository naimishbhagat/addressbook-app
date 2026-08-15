package com.naimish.AddressBook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressBookRequest {

    @NotBlank(message = "branch is required")
    private String branch;
}
