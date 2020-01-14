/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

import entities.Hobby;

/**
 *
 * @author aamandajuhl
 */
public class HobbyDTO {

    private long id;
    private String name;
    private String description;

    public HobbyDTO(Hobby hobby) {

        this.id = hobby.getId();
        this.name = hobby.getName();
        this.description = hobby.getDescription();
    }

    public HobbyDTO(String name, String description) {

        this.name = name;
        this.description = description;
    }

    public HobbyDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
