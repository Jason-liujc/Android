package com.example.liujiachen.myapp;

/**
 * Created by liujiachen on 3/26/16.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Smokepuff extends GameObject{
    public int r; //radius
    public Smokepuff(int x, int y){

        r=5;
        super.x=x;
        super.y=y;
        //setting the x and y in the gameobject to the x and y passed in the constructor.




    }


    public void update(){
        x-=10;

    }

    public void draw(Canvas canvas){
        Paint paint =new Paint();
        paint.setColor(Color.GRAY);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x-r,y-r,r,paint);
        //smokepuff is going be consisted of three little circles


        canvas.drawCircle(x-r+2,y-r-2,r,paint);
        canvas.drawCircle(x-r+4,y-r+1,r,paint);


    }

}
