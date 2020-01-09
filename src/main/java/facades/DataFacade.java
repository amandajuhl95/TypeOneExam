/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import DTO.QuoteDTO;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author benja
 */
public class DataFacade {

    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static DataFacade instance;

    private DataFacade() {
    }

    public static DataFacade getDataFacade() {
        if (instance == null) {

            instance = new DataFacade();
        }
        return instance;
    }

    private String getQuoteData() throws MalformedURLException, IOException {
        URL url = new URL("https://quote-garden.herokuapp.com/quotes/random");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json;charset=UTF-8");
//    con.setRequestProperty("User-Agent", "server"); //remember if you are using SWAPI
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = null;
        if (scan.hasNext()) {
            jsonStr = scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }

    public List<QuoteDTO> getData() throws InterruptedException, ExecutionException {
        List<QuoteDTO> quotes = new ArrayList<>();
        Queue<Future<QuoteDTO>> queue = new ArrayBlockingQueue(5);

        for (int i = 1; i <= 5; i++) {
            Future<QuoteDTO> future = executor.submit(() -> {
                
                QuoteDTO quote = GSON.fromJson(getQuoteData(), QuoteDTO.class);
                return quote;
            });

            queue.add(future);
        }
        while (!queue.isEmpty()) {
            Future<QuoteDTO> qoute = queue.poll();
            if (qoute.isDone()) {
                quotes.add(qoute.get());
            } else {
                queue.add(qoute);
            }
        }

        return quotes;
    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        
        DataFacade df = DataFacade.getDataFacade();
        
        List<QuoteDTO> qoutes = df.getData();
        
        for (QuoteDTO qoute : qoutes) {
            System.out.println(qoute);
        }
        
    }

}

