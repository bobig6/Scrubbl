package com.example.scrubbl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Dictionary {
    public static ArrayList<String> getDictionary(String lang, String category) throws FileNotFoundException {
        String filename = category + "_" + lang;
        String path = "src/main/resources/words/" + filename;
        Scanner s = new Scanner(new File(path));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()){
            list.add(s.next());
        }
        s.close();
        return list;
    }

}
