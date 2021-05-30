package com.example.photoedge.ui.customview;

public class RatioValues {
    private String ratioHorizontal;
    private final String ratioVertical;

    public RatioValues(String ratioHorizontal, String ratioVertical) {
        this.ratioHorizontal = ratioHorizontal;
        this.ratioVertical = ratioVertical;
    }

    public RatioValues(String ratioHorizontal) {
        this.ratioHorizontal = ratioHorizontal;
        this.ratioVertical = "";
    }

    public String getRatioVertical() {
        return ratioVertical;
    }

    public String getRatioHorizontal() {
        return ratioHorizontal;
    }

    public void setRatioHorizontal(String ratioHorizontal) {
        this.ratioHorizontal = ratioHorizontal;
    }

}
