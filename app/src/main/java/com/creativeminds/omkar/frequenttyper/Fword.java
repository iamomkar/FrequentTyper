package com.creativeminds.omkar.frequenttyper;

/**
 * Created by Omkar on 8/20/2017.
 */

public class Fword {

    String fword;
    String search_key;
    String date;

    public Fword(){

    }

    public Fword(String _fword,String _search_key,String _date){
        fword=_fword;
        search_key=_search_key;
        date=_date;
    }

    public void setFword(String _fword){
        fword=_fword;
    }
    public String getFword(){
        return fword;
    }

    public void setSearch_key(String _search_key){
        search_key=_search_key;
    }
    public String getSearch_key(){
        return search_key;
    }

    public void setDate(String _date){
        date=_date;
    }
    public String getDate(){
        return date;
    }
}
