package com.example.scrubbl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScrubblController {
    int currentUserId = 0;

    ArrayList<Room> rooms = new ArrayList<>();

    public String generateRoomId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private Room getRoomById(String roomId){
        return rooms.stream().filter(a -> a.getRoomId().equals(roomId)).collect(Collectors.toList()).get(0);
    }

    @GetMapping("/")
    public String start() {
        return "start";
    }


    // The drawing page
    @GetMapping("/draw")
    public String index(Model model) {
        model.addAttribute("room", rooms.get(0));
        return "index";
    }

    // The guessing page
    @GetMapping("/guess")
    public String client(Model model) {
        model.addAttribute("room", rooms.get(0));
        return "client";
    }

    @PostMapping("/createRoom")
    @ResponseBody
    public String createRoom(@RequestBody Map<String, String> payload) {
        User owner = new User(currentUserId, payload.get("name"), Roles.DRAWER);
        currentUserId++;
        Room room = new Room(generateRoomId(), owner, Integer.parseInt(payload.get("time")), Integer.parseInt(payload.get("rounds")));
        room.addUser(owner);
        rooms.add(room);
        if(payload.get("category").equals("custom")){
            room.setCategory(Arrays.asList(payload.get("custom").split(", ")));
        }else{
            room.setCategory(payload.get("category"), payload.get("lang"));
        }
        System.out.println(room.dictionary.toString());
        return room.getRoomId();
    }

    @PostMapping("/{roomID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody String name, @PathVariable String roomID) {
        User user = new User(currentUserId, name, Roles.GUESSER);
        currentUserId++;
        System.out.println(roomID);
        System.out.println(getRoomById(roomID));
        getRoomById(roomID).addUser(user);

    }

    // A method for getting all lines after certain id
    @RequestMapping(value = "/getRole/{roomId}/{name}", method = RequestMethod.GET, produces="text/plain")
    @ResponseBody
    public String getRole(@PathVariable String name, @PathVariable String roomId){
        if(getRoomById(roomId).getUserByName(name).getRole() == Roles.DRAWER){
            return "drawer";
        }
        else {
            return "guesser";
        }
    }

    @RequestMapping(value = "/getTime/{roomId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<Integer> getTime(@PathVariable String roomId){
        return ResponseEntity.ok(getRoomById(roomId).timer);
    }

    //A method for posting the lines onto the server
    @RequestMapping(value = "/draw/clear/{roomId}", method = RequestMethod.POST)
    @ResponseBody
    public int clear(@PathVariable String roomId){
        getRoomById(roomId).drawing.clear();
        System.out.println("cleared");
        return 200;
    }


    //A method for posting the lines onto the server
    @RequestMapping(value = "/draw/send/{roomId}", method = RequestMethod.POST,
            consumes = "application/json")
    @ResponseBody
    public int send(@RequestBody SentLine[] lines, @PathVariable String roomId){
        for (int i = 0; i < lines.length; i++){
            //we convert the SentLine array to Line ArrayList
            SentLine curLine = lines[i];
            Line line = new Line("a", getRoomById(roomId).getCurrLineId(), curLine.getPrevX(), curLine.getPrevY(), curLine.getCurX(), curLine.getCurY(), curLine.getColor(), curLine.getThickness());
            getRoomById(roomId).increaseCurrLineId();
            getRoomById(roomId).drawing.add(line);
        }
        return 200;
    }



    // A method for getting all lines after certain id
    @RequestMapping(value = "/getLines/{roomId}/{LineId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<ArrayList<Line>> getLines(@PathVariable Long LineId, @PathVariable String roomId){

        ArrayList<Line> toSend = new ArrayList<>();
        //if the array is empty we erase everything by sending a packet with id = -1
        if(getRoomById(roomId).drawing.size() == 0){
            toSend.add(new Line("a", -1, -1, -1, -1, -1, "none", 0));
            return ResponseEntity.ok(toSend);
        }
        for(int i = 0; i < getRoomById(roomId).drawing.size(); i++){
            if(getRoomById(roomId).drawing.get(i).getId() > LineId){
                toSend.add(getRoomById(roomId).drawing.get(i));
            }
        }
        // if the packet is empty we send id = -2
        if(toSend.size() == 0){
            toSend.add(new Line("a", -2, -1, -1, -1, -1, "none", 0));
        }
        return ResponseEntity.ok(toSend);
    }

    @RequestMapping(value = "/sendMessage/{roomId}", method = RequestMethod.POST,
            consumes = "application/json")
    @ResponseBody
    public int sendMessage(@RequestBody ObjectNode objectNode, @PathVariable String roomId){
        getRoomById(roomId).chat.add(new Message(objectNode.get("sender").asText(), objectNode.get("message").asText()));
        return 200;
    }

    @RequestMapping(value = "/getMessages/{roomId}/{messageId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<ArrayList<Message>> getMessages(@PathVariable String roomId, @PathVariable int messageId){
        ArrayList<Message> toSend = new ArrayList<>();
        for(int i = 0; i < getRoomById(roomId).chat.size(); i++){
            if(i > messageId){
                toSend.add(getRoomById(roomId).chat.get(i));
            }
        }
        return ResponseEntity.ok(toSend);
    }

    // A method for getting all words
    @RequestMapping(value = "/getWords/{roomId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<ArrayList<String>> getWords(@PathVariable String roomId){
        ArrayList<String> words = getRoomById(roomId).getWords(3);
        if(getRoomById(roomId).getCurrentRound() >= getRoomById(roomId).getRounds()){
            words.add("true");
        }else{
            words.add("false");
        }
        return ResponseEntity.ok(words);
    }

    // A method for getting current word
    @RequestMapping(value = "/getCurrentWord/{roomId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<List<String>> getWord(@PathVariable String roomId){
        List<String> word = new ArrayList<>(List.of(getRoomById(roomId).getCurrentWord()));
        if(getRoomById(roomId).getCurrentRound() >= getRoomById(roomId).getRounds()){
            word.add("true");
        }else{
            word.add("false");
        }
        return ResponseEntity.ok(word);
    }

    @RequestMapping(value = "/sendWord/{roomId}", method = RequestMethod.POST)
    @ResponseBody
    public int sendWord(@RequestBody String word, @PathVariable String roomId){
        getRoomById(roomId).setCurrentWord(word);
        System.out.println(word);
        return 200;
    }

    @RequestMapping(value = "/endRound/{roomId}/{name}", method = RequestMethod.POST)
    @ResponseBody
    public int endRound(@RequestBody String word, @PathVariable String roomId, @PathVariable String name){
        System.out.println(word);
        return 200;
    }

    @RequestMapping(value = "/WordGuessed/{roomId}/{name}", method = RequestMethod.POST)
    @ResponseBody
    public int wordGuessed(@RequestBody String word, @PathVariable String roomId, @PathVariable String name){
        System.out.println(name + " guessed the word");
        getRoomById(roomId).guessWord(name, word);
        return 200;
    }

    // A method for getting current word
    @RequestMapping(value = "/getStats/{roomId}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<HashMap<String, Integer>> getStats(@PathVariable String roomId){
        return ResponseEntity.ok(getRoomById(roomId).getStats());
    }

    @RequestMapping(value = "/saveDrawing/{roomId}", method = RequestMethod.POST)
    @ResponseBody
    public int saveDrawing(@RequestBody String drawingURL, @PathVariable String roomId){
        getRoomById(roomId).setDrawingURL(drawingURL);
        return 200;
    }

    // A method for getting current word
    @RequestMapping(value = "/getRooms/", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<List<String>> getRooms(){
        return ResponseEntity.ok(rooms.stream().map(Room::getRoomId).collect(Collectors.toList()));
    }

}
