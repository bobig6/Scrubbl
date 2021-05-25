package com.example.scrubbl;

import java.sql.*;

public class DB_Manager {

     public static void addRound(String roomID, String drawing, int roundNumber, int guesses, String chosenWord, String word_2, String word_3){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/scrubbl","root","bobi");
            Statement stmt=con.createStatement();
            String query = String.format("INSERT into gamedata (room_ID, drawing, round_number, guesses, chosen_word, word_2, word_3) VALUES ('%s', '%s', %d, %d, '%s', '%s', '%s')", roomID, drawing, roundNumber, guesses, chosenWord, word_2, word_3);
            int rs=stmt.executeUpdate(query);
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
