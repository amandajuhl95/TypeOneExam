/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import entities.Hobby;
import entities.Person;
import entities.Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import utils.EMF_Creator;

/**
 *
 * @author aamandajuhl
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonDTO addPerson(PersonDTO p) {
        EntityManager em = getEntityManager();

        try {

            em.getTransaction().begin();

            Address address;
            List<Address> addressDB = getAddress(p.getStreet(), p.getCity(), p.getZip());
            if (addressDB.size() > 0) {
                address = addressDB.get(0);
            } else {
                address = new Address(p.getStreet(), p.getCity(), p.getZip());
            }

            Address mergedAddress = em.merge(address);

            Person person = new Person(p.getFirstname(), p.getLastname(), p.getEmail(), p.getPhone(), mergedAddress);
            person.setAddress(mergedAddress);

            em.persist(person);
            em.getTransaction().commit();

            return new PersonDTO(person);

        } finally {
            em.close();
        }

    }

    public PersonDTO editPerson(PersonDTO p) {

        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, p.getId());
        if (person == null) {
            throw new WebApplicationException("Person not found", 400);
        }

        Address address;
        List<Address> addressDB = getAddress(p.getStreet(), p.getCity(), p.getZip());
        if (addressDB.size() > 0) {
            address = addressDB.get(0);
        } else {
            address = new Address(p.getStreet(), p.getCity(), p.getZip());
        }

        person.setFirstName(p.getFirstname());
        person.setLastName(p.getLastname());
        person.setEmail(p.getEmail());
        person.setPhone(p.getPhone());

        Address oldAddress = person.getAddress();
        if (oldAddress.getPersons().size() == 1 && !oldAddress.equals(address)) {
            oldAddress.removePerson(person);
        }

        try {

            em.getTransaction().begin();

            Address mergedAddress = em.merge(address);
            person.setAddress(mergedAddress);

            em.merge(person);
            em.persist(person);

            if (oldAddress.getPersons().isEmpty()) {
                em.remove(oldAddress);
            }

            em.getTransaction().commit();
            return new PersonDTO(person);

        } finally {
            em.close();
        }

    }

    public void deletePerson(long person_id) {
        EntityManager em = getEntityManager();

        try {

            em.getTransaction().begin();

            Person person = em.find(Person.class, person_id);
            if (person.getAddress().getPersons().size() == 1) {
                em.remove(person.getAddress());
            }

            em.remove(person);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    private List<Address> getAddress(String street, String city, int zip) {

        EntityManager em = getEntityManager();

        List<Address> addressList;
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.city = :city AND a.zip = :zip", Address.class);
        addressList = query.setParameter("street", street).setParameter("city", city).setParameter("zip", zip).getResultList();
        return addressList;
    }

    public List<PersonDTO> getAllPersons() {
        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> persons = query.getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));
        }

        return personsDTO;

    }

    public PersonDTO getPersonById(long id) {

        EntityManager em = getEntityManager();

        try {
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new WebApplicationException("Person was not found", 400);
            }
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getPersonByEmail(String email) {

        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.email = :email", Person.class);
        List<Person> persons = query.setParameter("email", email).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));
        }

        return personsDTO;
    }

    public PersonDTO getPersonByNumber(int phone) {

        EntityManager em = getEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.phone = :phone", Person.class);
        Person person = query.setParameter("phone", phone).getSingleResult();
        if (person == null) {
            throw new WebApplicationException("There is no person with this phone number", 400);

        }

        PersonDTO personDTO = new PersonDTO(person);
        return personDTO;

    }

    public HobbyDTO addHobbyToList(HobbyDTO h) {
        EntityManager em = getEntityManager();

        Hobby hobby;
        List<Hobby> hobbies = getHobby(h.getName());
        if (hobbies.size() > 0) {
            throw new WebApplicationException("Hobby already exsist", 400);

        } else {
            hobby = new Hobby(h.getName().toLowerCase(), h.getDescription());
        }

        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.getTransaction().commit();

            return new HobbyDTO(hobby);

        } finally {
            em.close();
        }

    }

    public PersonDTO addHobbyToPerson(long person_id, HobbyDTO h) {
        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, person_id);
        if (person == null) {
            throw new WebApplicationException("No person with the given id was found");
        }

        Hobby hobby;
        List<Hobby> hobbies = getHobby(h.getName());
        if (hobbies.size() > 0) {
            hobby = hobbies.get(0);
        } else {
            hobby = new Hobby(h.getName().toLowerCase(), h.getDescription());
        }

        person.addHobby(hobby);

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }

    public HobbyDTO getHobbyById(long hobby_id) {
        EntityManager em = getEntityManager();

        try {

            Hobby hobby = em.find(Hobby.class, hobby_id);
            if (hobby == null) {
                throw new WebApplicationException("Hobby not found", 400);
            }

            HobbyDTO hobbyDTO = new HobbyDTO(hobby);

            return hobbyDTO;
        } finally {
            em.close();
        }

    }

    public void deleteHobbyFromList(long id) {

        EntityManager em = getEntityManager();
        try {

            em.getTransaction().begin();

            Hobby hobby = em.find(Hobby.class, id);

            if (hobby == null) {
                throw new WebApplicationException("Hobby doesn't exsist", 400);
            }

            for (Person person : hobby.getPersons()) {
                person.removeHobby(hobby);
                em.merge(person);
            }

            em.remove(hobby);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

    }

    public HobbyDTO editHobby(HobbyDTO h) {
        EntityManager em = getEntityManager();

        Hobby hobby = em.find(Hobby.class, h.getId());
        if (hobby == null) {
            throw new WebApplicationException("Hobby not found", 400);
        }

        hobby.setName(h.getName());
        hobby.setDescription(h.getDescription());

        try {

            em.getTransaction().begin();

            em.merge(hobby);
            em.persist(hobby);

            em.getTransaction().commit();

            return new HobbyDTO(hobby);

        } finally {
            em.close();
        }

    }

    private List<Hobby> getHobby(String name) {
        EntityManager em = getEntityManager();

        TypedQuery<Hobby> query = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
        return query.setParameter("name", name).getResultList();

    }

    public List<PersonDTO> getAllPersonsByHobby(String name) {

        EntityManager em = getEntityManager();

        List<PersonDTO> personsDTO = new ArrayList();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p INNER JOIN p.hobbies pho WHERE pho.name = :name", Person.class);
        List<Person> persons = query.setParameter("name", name).getResultList();

        for (Person person : persons) {
            personsDTO.add(new PersonDTO(person));
        }

        return personsDTO;
    }

    public List<HobbyDTO> getAllHobbies() {
        EntityManager em = getEntityManager();

        List<HobbyDTO> hobbiesDTO = new ArrayList();

        TypedQuery<Hobby> query = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
        List<Hobby> hobbies = query.getResultList();

        for (Hobby hobby : hobbies) {
            hobbiesDTO.add(new HobbyDTO(hobby));
        }

        return hobbiesDTO;
    }

}
