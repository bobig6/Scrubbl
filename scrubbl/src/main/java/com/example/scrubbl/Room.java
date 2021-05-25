package com.example.scrubbl;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private final String roomId;
    private long currLineId = 0;
    public ArrayList<Line> drawing = new ArrayList<>();
    private final User owner;
    private String currentWord = "";
    private int guessesForRound = 0;

    private String drawingURL = "";

    private final int time;
    private final int rounds;

    public int timer;



    private int currentRound = 1;

    private ArrayList<User> users = new ArrayList<>();

    public ArrayList<Message> chat = new ArrayList<>();

    public ArrayList<String> dictionary = new ArrayList<>();


    private ArrayList<String> lastGuesses = new ArrayList<>();

    // This class holds the room
    // Every room has id, current line id, and all the lines that had been drawn

    public Room(String roomId, User owner, int time, int rounds) {
        this.roomId = roomId;
        this.owner = owner;
        this.time = time;
        this.rounds = rounds;
        startTimer();
    }

    public void setCategory(String category, String lang){
        dictionary.clear();
        try {
            dictionary.addAll(Dictionary.getDictionary(lang, category));
        } catch (FileNotFoundException e) {
            System.out.println("Wrong language or category");
        }
    }

    public void setCategory(List<String> dict){
        dictionary.clear();
        dictionary.addAll(dict);
    }

    public ArrayList<String> getWords(int numberOfElements){
        Random rand = new Random();
        // create a temporary list for storing
        // selected element
        lastGuesses = new ArrayList<>();
        for (int i = 0; i < numberOfElements; i++) {
            // take a random index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(dictionary.size());

            // add element in temporary list
            lastGuesses.add(dictionary.get(randomIndex));
        }
        return lastGuesses;
    }

    public String getRoomId() {
        return roomId;
    }

    public long getCurrLineId() {
        return currLineId;
    }

    public void increaseCurrLineId() {
        currLineId++;
    }

    public void setCurrLineId(long currLineId) {
        this.currLineId = currLineId;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUserById(int userID){
        users.removeIf(user -> user.getId() == userID);
    }

    public User getUserByName(String name){
        return users.stream().filter(a -> a.getName().equals(name)).collect(Collectors.toList()).get(0);
    }

    public User getOwner() {
        return owner;
    }

    public int getTime() {
        return time;
    }

    void startTimer(){
        int delay = 0; // delay for 0 sec.
        int period = 1000; // repeat every sec.
        timer = time;
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // Your code
                timer--;
                if(timer == 0){
                    timer = time;
                    if(currentRound <= rounds){
                        endRound();
                    }
                }
            }
        }, delay, period);

    }

    public int getRounds() {
        return rounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void guessWord(String name, String word){
        getUserByName(name).addPoint();
        guessesForRound++;
    }

    public void endRound(){
        System.out.println(drawing.toString());
        lastGuesses.remove(currentWord);
        DB_Manager.addRound(roomId, drawingURL, currentRound, guessesForRound, currentWord, lastGuesses.get(0), lastGuesses.get(1));

        currentRound++;
        System.out.println("Round: " + currentRound + "/" + rounds);
        int drawer = users.stream().filter(a -> a.getRole() == Roles.DRAWER).collect(Collectors.toList()).get(0).getId();
        int newDrawer = drawer + 1;
        if(newDrawer > users.size() - 1){
            newDrawer = 0;
        }
        currentWord = "";
        for (int i = 0; i < users.size(); i++){
            if(i == newDrawer){
                users.get(i).setRole(Roles.DRAWER);
            }else {
                users.get(i).setRole(Roles.GUESSER);
            }
        }
        guessesForRound = 0;
    }


    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public HashMap<String, Integer> getStats(){
        HashMap<String, Integer> stats = new HashMap<>();
        ArrayList<User> sortedUsers = new ArrayList<>(users);
        sortedUsers.sort(Comparator.comparingInt(User::getCurrentPoints));
        Collections.reverse(sortedUsers);
        for (User user : sortedUsers) {
            stats.put(user.getName(), user.getCurrentPoints());
        }
        return stats;
    }

    public String getDrawingURL() {
        return drawingURL;
    }

    public void setDrawingURL(String drawingURL) {
        this.drawingURL = drawingURL;
    }
}
