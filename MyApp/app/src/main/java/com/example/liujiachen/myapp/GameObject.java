package com.example.liujiachen.myapp;

import android.graphics.Rect;

/**
 * Created by liujiachen on 3/26/16.
 */

public abstract class GameObject {


    protected int x,y,dx,dy,width,height;

    //call mutators and accessors

    public void setX(int x){
        this.x=x;
    }


    public void setY(int y){
        this.y=y;

    }


    public int getX(){
        return this.x;
    }


    public int getY(){
        return this.y;
    }


    public int getHeight(){
        return this.height;
    }


    public int getWidth(){
        return this.width;
    }


    //check for collision

    public Rect getRectangle(){
        return new Rect(x,y,x+width,y+height);
    }







}
