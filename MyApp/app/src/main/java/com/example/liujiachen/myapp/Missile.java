package com.example.liujiachen.myapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by liujiachen on 3/26/16.
 */
public class Missile extends GameObject{

    private int score;
    private int speed;
    private Random rand=new Random();

    private Animation animation = new Animation();
    private Bitmap spreadsheets;



    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames){

        super.x=x;

        super.y=y;
        width=w;
        height=h;
        score =s;


        //the speed of missle increases
        speed=7+(int)(rand.nextDouble()*score/30);

        //cap missile speed

        if(speed>=30)
            speed=30;

        Bitmap[] image=new Bitmap[numFrames];

        spreadsheets=res;


        //each element represents an missle image (at least a part of it )


        for(int i=0;i<image.length;i++){
            image[i]=Bitmap.createBitmap(spreadsheets,0,i*height,width,height);


        }

        animation.setFrames(image);
        animation.setDelay(100-speed);






    }


    public void update(){

        x-=speed;

        animation.update();


    }


    public void draw(Canvas canvas){

        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }
        catch (Exception e){}





    }

    @Override
    public int getWidth() {
        return width-10;
        //offset slightly, so if the helicopter colides with the tail of the missile,it won't really explode

    }



}
