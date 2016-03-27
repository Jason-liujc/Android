package com.example.liujiachen.myapp;

/**
 * Created by liujiachen on 3/6/16.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Background {

    private Bitmap image;

    private int x,y,dx;

    public Background(Bitmap res){
        image=res;
        dx=GamePanel.MOVESPEED;

    }

    public void update(){

        x+=dx;

        if(x<-GamePanel.WIDTH)
            x=0;



    }


    public void draw(Canvas canvas){

        canvas.drawBitmap(image,x,y,null);

        //draw a second one here
        if(x<0) {
            canvas.drawBitmap(image,x+GamePanel.WIDTH,y,null);
        }

    }




}
