[![Build Status](https://travis-ci.org/amandajuhl95/TypeOneExam.svg?branch=master)](https://travis-ci.org/amandajuhl95/TypeOneExam)

# How to use the startcode

## Settting up the project on your own computer

* Download or clone the project and open in Netbeans
* Go to the the config.properties file in other sources, and change the database information to your own, as well as the desired database for use
* Go to the pom.xml file in Project Files and change the domain to your own
* To make sure the project is working, run the file SetupTestUsers.java, and check your database

## Using the startcode
* Go to the DataFacade and change the URL to your desired api endpoint
* In the DTO folder make a DTO for your desired data fetched, similar to the QuoteDTO
* In the DataFacade method "getData" change the return class to your new DTO
* Got to the rest folder, in the QuoteResource change the path to your desired path
* Change the DTO class to your new DTO
* If any changes in the user or admin endpoint is desired, go to the LoginResource and add or change the methods 
