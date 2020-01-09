/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DTO;

import java.io.Serializable;


/**
 *
 * @author benja
 */

public class QuoteDTO implements Serializable {

   private String _id;
   private String quoteText;
   private String quoteAuthor;

    public QuoteDTO(String id, String quoteText, String quoteAuthor) {
        this._id = id;
        this.quoteText = quoteText;
        this.quoteAuthor = quoteAuthor;
    }

    @Override
    public String toString() {
        return "QuoteDTO{" + "id=" + _id + ", quoteText=" + quoteText + ", quoteAuthor=" + quoteAuthor + '}';
    }

   
  
   
   
 
  

 

   

}
