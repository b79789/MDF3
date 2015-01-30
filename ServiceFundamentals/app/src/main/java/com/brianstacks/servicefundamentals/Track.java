package com.brianstacks.servicefundamentals;

/**
 * Created by Brian Stacks
 * on 1/28/15
 * for FullSail.edu.
 */
public class Track {
    private String mTitle;
    private String mArtist;
    private String mUri;

    public Track(){
        mTitle="";
        mArtist="";
        mUri="";

    }

    public String getmTitle(){
        return this.mTitle;
    }

    public void setmTitle(String title){
        this.mTitle=title;
    }

    public String getmArtist(){
        return this.mArtist;
    }

    public void setmArtist(String artist){
        this.mArtist=artist;
    }



    public String getmUri(){
        return this.mUri;
    }

    public void setmUri(String uri){
        this.mUri=uri;
    }
}
