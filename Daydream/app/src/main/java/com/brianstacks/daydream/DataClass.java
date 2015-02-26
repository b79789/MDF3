package com.brianstacks.daydream;

import java.io.Serializable;

/**
 * Created by Brian Stacks
 * on 2/25/15
 * for FullSail.edu.
 */
public class DataClass implements Serializable {

    private static final long serialVersionUID = 8733333333330552888L;

    private String mText;

    public DataClass(){
        mText="";
    }

    public String getmText(){
        return mText;
    }

    public void setmText(String text){
        mText=text;
    }


}
