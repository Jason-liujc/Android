package com.example.liujiachen.myapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by liujiachen on 3/26/16.
 */
public class Player extends GameObject{
    private Bitmap spreadsheet;
    private int score;
    private boolean up;

    private boolean playing;
    private Animation animation=new Animation();
    private long startTime;

    public Player(Bitmap res,int w,int h, int numFrames){
        x=100;
        y=GamePanel.HEIGHT/2;
        dy=0;

        score=0;
        height=h;

        width=w;


        //create a bitmap array to store all the images of the player
        Bitmap[] image=new Bitmap[numFrames];
        //in this case, it'll be three frames, since the helicopter image we have only have three frames.

        spreadsheet=res;

        for(int i=0;i<image.length;i++){
            image[i]=Bitmap.createBitmap(spreadsheet,i*width,0,width,height);



        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime=System.nanoTime();



    }

    //be called by the motion event
    public void setUp(boolean b){
        up=b;
    }

    public void update(){
        long elapsed=(System.nanoTime()-startTime)/1000000;

        if(elapsed>100){
            score++;
            startTime=System.nanoTime(); //reset it
        }

        animation.update();

        //if we are pressing down on the screen:
        if(up){
            dy-=1.1; //acceleration

        }else{

            dy+=1.1;

        }

        //capp the speed of helicopter
        if(dy>10)
                dy=10;

        else if(dy<-10)
            dy=-10;


        y+=dy*2;


    }


    public void draw (Canvas canvas){
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public int getScore(){
        return score;
    }

    public boolean getPlaying(){
        return playing;
    }
    public void setPlaying(boolean b){
        playing=b;
    }


    public void resetDYA(){
        dy=0;
    }

    public void resetScore(){
        score=0;
    }





}
