package rest;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.User;
import facades.PersonFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

/**
 * @author amandajuhl
 */

@Path("info")
public class LoginResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            List<User> users = em.createQuery("select user from User user").getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @POST
    @Path("/addperson")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public PersonDTO addPerson(String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().length() < 2) {

            throw new WebApplicationException("Firstname must be 2 characters", 400);
        }

        if (person.getFirstname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Firstname must not contain digits", 400);
        }

        if (person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().length() < 2) {

            throw new WebApplicationException("Lastname must be 2 characters", 400);
        }

        if (person.getLastname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Lastname must not contain digits", 400);
        }

        if (person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") || !person.getEmail().contains(".")) {

            throw new WebApplicationException("Please enter valid email", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonByEmail(person.getEmail());

        if (persons.size() > 0) {

            throw new WebApplicationException("Email is already in use", 400);
        }

        if (person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().matches(".*\\d+.*") || person.getStreet().length() < 3) {

            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters", 400);
        }

        if (person.getCity() == null || person.getCity().isEmpty() || person.getCity().matches(".*\\d+.*") || person.getCity().length() < 3) {

            throw new WebApplicationException("City must be at least 3 characters", 400);
        }
        if (person.getZip() < 1000 || person.getZip() > 9999) {

            throw new WebApplicationException("Zipcode must be 4 digits", 400);
        }

        return FACADE.addPerson(person);
    }

    @PUT
    @Path("person/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public PersonDTO editPerson(@PathParam("id") long id, String personAsJSON) {

        PersonDTO person = GSON.fromJson(personAsJSON, PersonDTO.class);

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }
        if (person.getFirstname() == null || person.getFirstname().isEmpty() || person.getFirstname().length() < 2) {

            throw new WebApplicationException("Firstname must be 2 characters", 400);
        }

        if (person.getFirstname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Firstname must not contain digits", 400);
        }

        if (person.getLastname() == null || person.getLastname().isEmpty() || person.getLastname().length() < 2) {

            throw new WebApplicationException("Lastname must be 2 characters", 400);
        }

        if (person.getLastname().matches(".*\\d+.*")) {

            throw new WebApplicationException("Lastname must not contain digits", 400);
        }

        if (person.getEmail() == null || person.getEmail().isEmpty() || !person.getEmail().contains("@") || !person.getEmail().contains(".")) {

            throw new WebApplicationException("Please enter valid email", 400);
        }

        List<PersonDTO> persons = FACADE.getPersonByEmail(person.getEmail());

        if (persons.size() > 0) {

            throw new WebApplicationException("Email is already in use", 400);
        }

        if (person.getStreet() == null || person.getStreet().isEmpty() || person.getStreet().matches(".*\\d+.*") || person.getStreet().length() < 3) {

            throw new WebApplicationException("Street must only contain letters, and be at least 3 characters", 400);
        }

        person.setId(id);
        return FACADE.editPerson(person);
    }

    @DELETE
    @Path("person/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public String deletePerson(@PathParam("id") long id) {

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }

        FACADE.deletePerson(id);

        return "{\"status\": \"Person has been deleted\"}";
    }

    @POST
    @Path("/addhobby/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public HobbyDTO addHobbyToList(String HobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(HobbyAsJSON, HobbyDTO.class);

        if (hobby.getName() == null || hobby.getName().length() < 2) {

            throw new WebApplicationException("Hobby must be 2 at least characters", 400);
        }

        if (hobby.getDescription() == null || hobby.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addHobbyToList(hobby);

    }

    @POST
    @Path("/addhobby/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public PersonDTO addHobby(@PathParam("id") long person_id, String HobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(HobbyAsJSON, HobbyDTO.class);

        if (person_id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        if (hobby.getName() == null || hobby.getName().length() < 2) {

            throw new WebApplicationException("Hobby must be 2 at least characters", 400);
        }

        if (hobby.getDescription() == null || hobby.getDescription().length() < 2) {

            throw new WebApplicationException("Description must be at least 2 characters", 400);
        }

        return FACADE.addHobbyToPerson(person_id, hobby);

    }

    @PUT
    @Path("/hobby/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public HobbyDTO editHobby(@PathParam("id") long id, String hobbyAsJSON) {

        HobbyDTO hobby = GSON.fromJson(hobbyAsJSON, HobbyDTO.class);

        if (id == 0) {

            throw new WebApplicationException("Id not passed correctly", 400);
        }

        hobby.setId(id);
        return FACADE.editHobby(hobby);
    }

    @DELETE
    @Path("/hobby/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String deleteHobbyFromList(@PathParam("id") long id) {

        if (id == 0) {
            throw new WebApplicationException("Id not passed correctly", 400);
        }

        FACADE.deleteHobbyFromList(id);

        return "{\"status\": \"Hobby has been deleted\"}";
    }

}
