/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

import entities.Hobby;
import entities.Person;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aamandajuhl
 */
public class PersonDTO {

    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private int phone;
    private String street;
    private String city;
    private int zip;
    private Set<HobbyDTO> hobbies = new HashSet();

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.firstname = person.getFirstName();
        this.lastname = person.getLastName();
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.street = person.getAddress().getStreet();
        this.city = person.getAddress().getCity();
        this.zip = person.getAddress().getZip();

        for (Hobby hobby : person.getHobbies()) {
            this.hobbies.add(new HobbyDTO(hobby));
        }
    }

    public PersonDTO(String firstname, String lastname, String email, String phone, String street, String city, String zip) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = Integer.parseInt(phone);
        this.street = street;
        this.city = city;
        this.zip = Integer.parseInt(zip);
    }

    public PersonDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    public Set<HobbyDTO> getHobbies() {
        return hobbies;
    }

}
