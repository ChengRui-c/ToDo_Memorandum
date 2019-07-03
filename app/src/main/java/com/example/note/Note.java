package com.example.note;

/**
 * 对应数据库的实体类
 */

public class Note {

    private int id;
    private int tag;//颜色
    private int num;//列表中的位置
    private String textDate;
    private String textTime;
    private String alarm;
    private String mainText;


    public Note(){}

    public Note(int num, int tag, String textDate, String textTime, String alarm, String mainText, int id) {
        this.num = num;
        this.tag = tag;
        this.textDate = textDate;
        this.textTime = textTime;
        this.alarm = alarm;
        this.mainText = mainText;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTextDate() {
        return textDate;
    }

    public void setTextDate(String textDate) {
        this.textDate = textDate;
    }

    public String getTextTime() {
        return textTime;
    }

    public void setTextTime(String textTime) {
        this.textTime = textTime;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}
