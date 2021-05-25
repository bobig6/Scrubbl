package com.example.scrubbl;

public class SentLine{
    private final int prevX;
    private final int prevY;
    private final int curX;
    private final int curY;
    private final String color;
    private final int thickness;

    // This class is holding the lines that come from the client.
    // They dont have id and room id

    public SentLine(int prevX, int prevY, int curX, int curY, String color, int thickness) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.curX = curX;
        this.curY = curY;
        this.color = color;
        this.thickness = thickness;
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