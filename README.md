# addressbook-app

To Run in the Docker:
docker compose up --build

To Run project locally:
./mvnw spring-boot:run

Once it build / Run locally:
Go to http://localhost:8080 check if it is showing 404 and server is running in console.

Once it is running: 
Go to http://localhost:8080/swagger-ui/index.html Which has all the api documents and contracts

To Run Unit tests:
./mvnw test

AC requirement
Address book holds name + phone number(s)	        --->	Contact.name, Contact.phoneNo (List)
Add new contact entries	                            --->	POST /api/addressbooks/{addressBookId}/contacts
Remove existing contact entries	                    --->    DELETE /api/addressbooks/{addressBookId}/contacts/{contactId}
Print all contacts in an address book	            ---> 	GET /api/addressbooks/{addressBookId}/contacts
Maintain multiple address books	                    ---> 	POST /api/addressbooks, GET /api/addressbooks
Print unique set of contacts across address books	---> 	GET /api/addressbooks/contacts
Data persistence	                                ---> 	H2 (in-memory) via Spring Data JPA