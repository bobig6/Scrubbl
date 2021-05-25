package com.example.scrubbl;

public class Line {
    private final String roomId;
    private final long id;
    private final int prevX;
    private final int prevY;
    private final int curX;
    private final int curY;
    private final String color;
    private final int thickness;

    // This class holds the lines after they are given id and room_id

    public Line(String roomId, long id, int prevX, int prevY, int curX, int curY, String color, int thickness) {
        this.roomId = roomId;
        this.id = id;
        this.prevX = prevX;
        this.prevY = prevY;
        this.curX = curX;
        this.curY = curY;
        this.color = color;
        this.thickness = thickness;
    }

    public String getRoomId() {
        return roomId;
    }

    public long getId() {
        return id;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public int getCurX() {
        return curX;
    }

    public int getCurY() {
        return curY;
    }

    public String getColor() {
        return color;
    }

    public int getThickness() {
        return thickness;
    }
}
