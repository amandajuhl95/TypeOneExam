/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 * REST Web Service
 *
 * @author aamandajuhl
 */
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates a new instance of PersonResource
     */
    public PersonResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStartpage() {
        return "{\"msg\":\"Hello to person\"}";
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonDTO> getAllPersons() {

        List<PersonDTO> persons = FACADE.getAllPersons();
        return persons;

    }

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public PersonDTO getPerson(@PathParam("id") long id) {

        PersonDTO person = FACADE.getPersonById(id);
        return person;
    }

    @GET
    @Path("/{number}")
    @Produces({MediaType.APPLICATION_JSON})
    public PersonDTO getPerson(@PathParam("number") int number) {

        PersonDTO person = FACADE.getPersonByNumber(number);
        return person;
    }

    @GET
    @Path("/hobby/{hobby}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<PersonDTO> getPersonsByHobby(@PathParam("hobby") String hobby) {

        if (hobby == null || "".equals(hobby)) {

            throw new WebApplicationException("Hobby must be defined", 400);
        }

        List<PersonDTO> persons = FACADE.getAllPersonsByHobby(hobby);
        return persons;
    }

    @GET
    @Path("/hobby/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HobbyDTO> getAllHobbies() {

        List<HobbyDTO> hobbies = FACADE.getAllHobbies();
        return hobbies;

    }

}
