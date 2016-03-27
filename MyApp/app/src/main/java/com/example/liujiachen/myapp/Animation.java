package com.example.liujiachen.myapp;

/**
 * Created by liujiachen on 3/26/16.
 */
import android.graphics.Bitmap;

public class Animation {
    private Bitmap[] frames;
    private int currentFrames; //represents a number here

    private long startTime;
    private long delay;
    private boolean playedOnce;


    //we want the animation to go through the spreadsheets
    public void setFrames(Bitmap[] frames){

       this.frames=frames;

        currentFrames=0;
        startTime= System.nanoTime();



    }

    public void setDelay (long d) {
        delay =d;

    }


    public void setFrame(int i){
        currentFrames=i;
    }


    public void update (){
        long  elapsed =(System.nanoTime()-startTime)/1000000;

        if(elapsed>delay){
            currentFrames++;
            startTime=System.nanoTime();



        }

        if(currentFrames==frames.length){
            currentFrames=0;
            playedOnce=true;

        }




    }

    public Bitmap getImage(){
        return frames[currentFrames];

    }


    public int getFrame(){
        return currentFrames;

    }

    public boolean isPlayedOnce(){
        return playedOnce;
    }








}
