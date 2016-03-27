package com.example.liujiachen.myapp;

/**
 * Created by liujiachen on 3/6/16.
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{


    public static final int WIDTH=856;

    public static final int HEIGHT=480;

    private long smokeStartTime;
    private long missileStartTime;


    public static final int MOVESPEED=-5;


    private MainThread thread;
    private Background bg;



    private Player player;


    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missile> missiles;


    private ArrayList<TopBorder> topborder;
    private ArrayList<BottomBorder> botborder;

    private int maxBorderHeight;
    private int minBorderHeight; //this is gonna change

    private int difficulty=20;

    private boolean topDown=true;
    private boolean botDown=true;//when reaches max or min border height, this will reverse

    private boolean newGameCreated;

    private Random rand=new Random();

    private Explosion explosion;
    private long startReset;

    private boolean reset;
    private boolean started;

    private boolean disappear;


    private int bestScore=0;


    //constructor


    public GamePanel(Context context){
        super(context);

        //add the call back to surfaceholder to intercept events
        getHolder().addCallback(this);



        //make gamePanel focusable to true;

        setFocusable(true);

    }
    //override some methods in surface view


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){


    }


    @Override
    //stop when the surface is destroyed
    public void surfaceDestroyed (SurfaceHolder holder){
        boolean retry=true;
        int counter=0; //prevent infinite loop

        while(retry && counter<1000){
            counter++;
            try{
                thread.setRunning(false);
                thread.join();
                retry=false;

                thread=null;



            }catch (InterruptedException e){e.printStackTrace();}


        }
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder){


        bg=new Background(BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));

        player=new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter),66,25,3);

        smoke=new ArrayList<Smokepuff>();

        topborder=new ArrayList<TopBorder>();
        botborder=new ArrayList<BottomBorder>();

        missiles=new ArrayList<Missile>();


        //we want the smokepuff to have a delay of some sort
        smokeStartTime=System.nanoTime();
        missileStartTime=System.nanoTime();

        thread=new MainThread(getHolder(),this);






        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();



    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {


        //make sure the player is not playing
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()  && newGameCreated && reset) {
                player.setPlaying(true);
                player.setUp(true);
            }

            if(player.getPlaying()) {

                if(!started)started=true;

                reset=false;
                player.setUp(true); //the helicopter will fly up

            }
            return true;
        }


        if(event.getAction()==MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);


    }

    public void update(){
        //we only update the backgroudn if the player is playing
        if(player.getPlaying()) {


            if(botborder.isEmpty()){
                player.setPlaying(false);
                return;
            }

            if(topborder.isEmpty()){

                player.setPlaying(false);
                return;

            }


            bg.update();
            player.update();


            ///calculate the threshold of height the border can have based on the score

            //max and min border heart are updated, and the border switched direction when either
            // max or min is met

            maxBorderHeight=30+player.getScore()/difficulty;
            //cap max border height so that borders can only take up a total of 1/2 of the screen
            if(maxBorderHeight>HEIGHT/4)
                maxBorderHeight=HEIGHT/4;

            minBorderHeight=5+player.getScore()/difficulty;

            //check top border collision

            for(int i=0;i<topborder.size();i++){
                if(collision(topborder.get(i), player))
                    player.setPlaying(false);
            }





            //check bottom
            for(int i=0;i<botborder.size();i++){

                if(collision(botborder.get(i),player))
                    player.setPlaying(false);

            }





            ///create top border here

            this.updateTopBorder();


            ///create bottom border here

            this.updateBottomBorder();




            //add missiels on timer
            long   missilesElapsed=(System.nanoTime()-missileStartTime)/1000000;
            if(missilesElapsed>(2000-player.getScore()/4)){//cuz it scales with player score
                //first missile always goes down the middle

                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,HEIGHT/2,45,15,player.getScore(),13));
                }else{

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH+10,(int)(rand.nextDouble()*(HEIGHT-(maxBorderHeight*2))
                            +maxBorderHeight),45,15,player.getScore(),13));

                }

                missileStartTime=System.nanoTime(); //reset the time





            }

            //loop through every missile and check collision and remove
            for(int i=0;i<missiles.size();i++){


                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;

                }

                if(missiles.get(i).getX()<-100){
                    missiles.remove(i);
                    break;
                }




            }




            //add smokepuffs on timer
            long elapsed=(System.nanoTime()-smokeStartTime)/1000000; //in millseconds


            if(elapsed>120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTime=System.nanoTime();

            }

            for(int i=0;i<smoke.size();i++){
                smoke.get(i).update();

                //if the smoke puff is off the screen, then we:
                if(smoke.get(i).getX()<-10)
                {
                    smoke.remove(i); //so that we don't waste resources
                }


            }


        }else{ //when the player dies

            player.resetDYA();
            if(!reset) {
                newGameCreated = false;
                startReset=System.nanoTime();

                reset=true;
                disappear=true;

                explosion=new Explosion(BitmapFactory.decodeResource(getResources(),
                        R.drawable.explosion),player.getX(),player.getY()-30,100,100,25);






            }

            explosion.update();
            long resetElapsed=(System.nanoTime()-startReset)/1000000;




            if(resetElapsed>2500 && !newGameCreated)
                newGame();







        }



    }

    //create collision method
    public boolean collision (GameObject g1, GameObject g2){

        if(Rect.intersects(g1.getRectangle(), g2.getRectangle())){
            return true;
        }
        return false;



    }





    @Override
    public void draw(Canvas canvas){

        final float scaleFactorX=getWidth()/(WIDTH*1.f);

        final float scaleFactorY=getHeight()/(HEIGHT*1.f);



        if(canvas!=null) {

            //for some reasons this scaling doesn't work
            //final int savedState=canvas.save();

           // canvas.save();
            //canvas.scale(scaleFactorX,scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            //draw missiles
            for(Smokepuff sp:smoke){
                sp.draw(canvas);

            }


            //draw smokes
            for(Missile m:missiles){
                m.draw(canvas);
            }

            for(TopBorder tb:topborder){
                tb.draw(canvas);
            }

            for(BottomBorder bb:botborder){
                bb.draw(canvas);
            }

            //draw explosion
            if(started){
                explosion.draw(canvas);
            }


            drawText(canvas);



            //canvas.restoreToCount(savedState);
            //canvas.restore();

        }
    }



    public void updateTopBorder(){
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50==0){
            topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    topborder.get(topborder.size()-1).getX()+20,0,(int)((rand.nextDouble()*
                    (maxBorderHeight))+1)));



        }

        for(int i=0;i<topborder.size();i++){
            topborder.get(i).update();
            if(topborder.get(i).getX()<-20){
                topborder.remove(i);
                //remove element of arraylist, replace it by adding a new one

                //calculate topdown which determines the direction the border is moving
                if(topborder.get(topborder.size()-1).getHeight()>=maxBorderHeight){ //the last element (topborder.size()-1)
                    topDown=false;

                }

                if(topborder.get(topborder.size()-1).getHeight()<=minBorderHeight){
                    topDown = true;

                }


                //new border added will have larger height
                if(topDown){
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,0,topborder.get(topborder.size()-1).getHeight()+1));
                }else{
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,0,topborder.get(topborder.size()-1).getHeight()-1)); //now we want the height to be smaller
                }


            }

        }



    }

    public void updateBottomBorder(){

        //every 40 points


        if(player.getScore()%40==0){

            botborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botborder.get(botborder.size()-1).getX()+20,(int)((rand.nextDouble()*
                    maxBorderHeight)+(HEIGHT-maxBorderHeight))));

        }


        for(int i=0;i<botborder.size();i++){

            botborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(botborder.get(i).getX()<-20) {
                botborder.remove(i);


                //calculate topdown which determines the direction the border is moving
                if (botborder.get(botborder.size() - 1).getHeight() >= maxBorderHeight) { //the last element (topborder.size()-1)
                    botDown = false;

                }

                if (botborder.get(botborder.size() - 1).getHeight() <= minBorderHeight) {
                    botDown = true;

                }


                if (botDown) {

                    botborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick), botborder.get(botborder.size() - 1).getX() + 20,
                            botborder.get(botborder.size() - 1).getY() + 1));


                } else {

                    botborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick), botborder.get(botborder.size() - 1).getX() + 20,
                            botborder.get(botborder.size() - 1).getY() - 1));


                }
            }

        }




    }


    //we want this to be called whenever the player dies
    public void newGame(){

        disappear=false;

        botborder.clear();
        topborder.clear();


        missiles.clear();
        smoke.clear();


        minBorderHeight=5;
        maxBorderHeight=30;

        player.resetDYA();


        player.resetScore();
        player.setY(HEIGHT/2);

        if(player.getScore()>bestScore){
            bestScore=player.getScore();
        }


        //craete borders until it goes off the screen slightly
        // initial top border
        for(int i=0; i*20<WIDTH+40;i++){

            if(i==0){
                topborder.add(new TopBorder(BitmapFactory.decodeResource
                        (getResources(),R.drawable.brick),i*20,0,10));
            }

            else{
                topborder.add(new TopBorder(BitmapFactory.decodeResource
                        (getResources(),R.drawable.brick),i*20,0,topborder.get(i-1).getHeight()+1));

            }



        }


        //initial bottom border
        for(int i=0;i*20<WIDTH+40;i++){

            if(i==0){
                botborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,HEIGHT-minBorderHeight));
            }


            //adding borders until the initial screen is filled
            else{
                botborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,botborder.get(i-1).getY()-1));

            }

        }

        newGameCreated=true;


    }


    public void drawText(Canvas canvas){


        Paint paint=new Paint();
        paint.setColor(Color.BLACK);

        paint.setTextSize(30);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        canvas.drawText("Distance:  " + (player.getScore() * 2), 10, HEIGHT - 10, paint);


        canvas.drawText("Best: "+bestScore,WIDTH-215,HEIGHT-10,paint);

        if(!player.getPlaying() && newGameCreated && reset){
            Paint paint1=new Paint();

            paint1.setTextSize(40);

            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


            canvas.drawText("Press to start",WIDTH/2-70,HEIGHT/2,paint1);
            paint1.setTextSize(20);
            canvas.drawText("Press and hold to go up, and let loose to go down",
                    0,HEIGHT/2+62,paint1);




        }

    }


}
