package com.example.note;

/**
 *  用于设置cardView的实体类
 */
public class OneNote {
    private int tag;//颜色
    private String textDate;
    private String textTime;
    private boolean alarm;
    private String mainText;

    public OneNote(int tag, String textDate, String textTime, boolean alarm, String mainText) {
        this.tag = tag;
        this.textDate = textDate;
        this.textTime = textTime;
        this.alarm = alarm;
        this.mainText = mainText;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
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

    public boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}
