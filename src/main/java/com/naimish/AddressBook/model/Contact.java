package com.naimish.AddressBook.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(exclude = "addressBook")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    private String name;

    @ElementCollection
    @CollectionTable(name = "contact_phone", joinColumns = @JoinColumn(name = "contact_id"))
    @Column(name = "phone_no")
    private List<String> phoneNo;

    @ManyToOne(fetch= FetchType.LAZY)
    private AddressBook addressBook;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact other)) return false;
        return Objects.equals(name, other.name)
                && Objects.equals(normalizedPhoneNo(), other.normalizedPhoneNo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, normalizedPhoneNo());
    }

    private List<String> normalizedPhoneNo() {
        return phoneNo == null ? List.of() : new ArrayList<>(phoneNo);
    }
}
