package com.example.misenews;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Location {
    @SerializedName("meta")
    Meta meta;
    @SerializedName("document")
    List<Document> document;

    public class Meta{
        @SerializedName("total_count")
        String total_count;

        public String getX() {return total_count;}
    }

    public class Document{
        @SerializedName("x")String x;
        @SerializedName("y")String y;

        public String getX() {return x;}
        public String getY() {return y;}

    }


}
