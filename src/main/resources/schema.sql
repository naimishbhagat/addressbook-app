CREATE TABLE address_book (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    branch VARCHAR(255)
);
CREATE TABLE contact (
    contact_id       INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255),
    address_book_id  INT,
    FOREIGN KEY (address_book_id) REFERENCES address_book(id)
);
CREATE TABLE contact_phone (
    contact_id INT,
    phone_no   VARCHAR(255),
    FOREIGN KEY (contact_id) REFERENCES contact(contact_id)
);