package com.example.useri.chatbot;

public class Note {
    private String Query;
    private String Result;


    public Note() {
        //empty constructor needed
    }

    public Note(String Query, String Result) {
        this.Query = Query;
        this.Result = Result;
    }

    public String getQuery() {
        return Query;
    }

    public String getResult() {
        return Result;
    }
}
