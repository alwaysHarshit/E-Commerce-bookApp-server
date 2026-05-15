package org.booknest.catelogservice.utils;

import org.booknest.catelogservice.entity.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;

/* this method direclty bulk save to book in db*/
@Component
public class UploadBooksCheat {


    public  void addBook(){
        // load the data from csv file
        String[] line;


        try (BufferedReader br=new BufferedReader(new FileReader("F:\\capgi-training\\BookApp-SERVER\\data.csv"))){
            br.readLine();
            for (int i = 0; i < 3; i++) {
             line=br.readLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
