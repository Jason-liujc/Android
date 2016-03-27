package com.example.liujiachen.myapp;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by liujiachen on 3/6/16.
 */
public class MainThread extends Thread
{

    private int FPS=30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder,GamePanel gamePanel){

        super(); //call the constructor at super class
        this.surfaceHolder=surfaceHolder;
        this.gamePanel=gamePanel;



    }

    @Override
    public void run(){

        //capp the fps at 30
        long startTime;
        long timeMill;
        long waitTime;
        long totalTime=0;
        long frameCount=0;
        long targetTime=1000/FPS;

        while(running){

            startTime=System.nanoTime();
            canvas=null;

            //try locking the canvas
            try{
                canvas=this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    //each time update and draw goes through
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);

                }
            }catch (Exception e
                    ){}

            finally {
                if(canvas!=null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch (Exception e){e.printStackTrace();}
                }
            }



            timeMill=(System.nanoTime()-startTime)/1000000;


            waitTime=targetTime-timeMill;
            try{
                this.sleep(waitTime); //this is how we cap fps here
            }catch (Exception e){}

            totalTime+=System.nanoTime()-startTime;
            frameCount++;
            if(frameCount==FPS){
                averageFPS=1000/(totalTime/frameCount/1000000);
                frameCount=0;
                totalTime=0;
                System.out.println(averageFPS);
            }

        }



    }

    public void setRunning(boolean b){

        running=b;

    }




}
