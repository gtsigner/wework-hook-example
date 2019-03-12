package com.example.myapplication.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class WeWorkJsonMessage implements Parcelable {

    private int code = -1;
    private int status = -1;
    private String json = "";
    private int type = 0;
    private String error = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static Creator<WeWorkJsonMessage> getCREATOR() {
        return CREATOR;
    }


    protected WeWorkJsonMessage(Parcel in) {

    }


    public static final Creator<WeWorkJsonMessage> CREATOR = new Creator<WeWorkJsonMessage>() {
        @Override
        public WeWorkJsonMessage createFromParcel(Parcel in) {
            return new WeWorkJsonMessage(in);
        }

        @Override
        public WeWorkJsonMessage[] newArray(int size) {
            return new WeWorkJsonMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
