package com.developer.sgupta20.minesweeper;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Picture;
        import android.graphics.Rect;
        import android.graphics.drawable.Drawable;
        import android.graphics.drawable.PictureDrawable;
        import android.support.v4.app.ActivityCompat;
        import android.view.GestureDetector;
        import android.view.Gravity;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.TextView;

        import java.util.Random;

/**
 * Created by sgupta20 on 10/29/14.
 */
public class MinesweeperView extends View  {

    private float width;    // width of one tile
    private float height;   // height of one tile
    int[][] mineGrid = new int[16][16];
    int selX, selY;
    int noOfMines = (int)(0.2*16*16);
    boolean gameStarted =  false,mineHit = false;
    GestureDetector gestureDetector;
    Minesweeper minesweeper = new Minesweeper();
    public MinesweeperView(Context context) {

        super(context);
        gestureDetector = new GestureDetector(context,new GestureDoubleTap());


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth()/16f;
        height = getHeight()/16f;
        Paint background = new Paint();

        background.setColor(getResources().getColor(
                R.color.grid_background));

        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        Paint lineColor = new Paint();
        lineColor.setColor(getResources().getColor(R.color.line_color));
        for (int i = 0; i < 16; i++) {
            canvas.drawLine(0, i * height, getWidth(), i * height,
                    lineColor);

            canvas.drawLine(i * width, 0, i * width, getHeight(),
                    lineColor);

        }

        if(gameStarted==false){

            for(int i=0;i<16;i++)
                for(int j=0;j<16;j++){
                    mineGrid[i][j]=-10;
                }
            setMines();
            gameStarted =true;

        }
        showNumbers(canvas);
        showFlag(canvas);
        if(mineHit == true){
            showMines(canvas);
           showAlert("Game Over...You Lose !!!");


        }





    }
    public void showAlert(String message) {

        TextView title = new TextView(this.getContext());
        title.setText("Minesweeper");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
         builder.setTitle("Minesweeper");
        //builder.setCustomTitle(title);
       // builder.setIcon(R.drawable.abc_ic_go);

        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                System.exit(0);

            }



        });
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gameStarted = false;
                mineHit = false;
                invalidate();

            }



        });


        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //System.out.println(event.getX()+"   "+event.getY());
             return gestureDetector.onTouchEvent(event);
    }
    private class GestureDoubleTap extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {

            int x = (int)(event.getX()/width);
            int y = (int) (event.getY()/height);
            System.out.println("Single Tap Event : "+x+"   "+y + mineGrid[x][y]);

            if(mineGrid[x][y]==-10){
                mineGrid[x][y]=-9;

            }
            else if(mineGrid[x][y] == -1){
                mineGrid[x][y]=-2;
            }
            else if(mineGrid[x][y]==-9){
                mineGrid[x][y]=-10;

            }
            else if(mineGrid[x][y] == -2){
                mineGrid[x][y]=-1;
            }

            if(checkForWinningCondition())
            {

                showAlert("Congratulations !!! You Win");


            }
            else
            {

                invalidate();
            }

            return super.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            System.out.println("Double Tap Event :"+event.getX()+"   "+event.getY());
           boolean mineHit =  checkForMineHit((int)(event.getX()/width),(int)(event.getY()/height));
            if(!mineHit){

                displayNumbers((int)(event.getX()/width),(int)(event.getY()/height));
                invalidate();
                if(checkForWinningCondition())
                {

                    showAlert("Congratulations !!! You Win");


                }






            }
            return super.onDoubleTap(event);
        }

    }

    boolean checkForMineHit(int x, int y){
        System.out.println("Tap at" +x+" "+y);
        if(mineGrid[x][y]==-1){
            System.out.println("Mine Hit");
            mineHit = true;
            selX = x;
            selY =y;
            invalidate();
            //showMines(x, y);
            return true;
        }
        return false;

    }
    void showFlag(Canvas canvas){

        Drawable picture = getResources().getDrawable(R.drawable.flag);
        Rect rect =  new Rect();
        for(int i =0;i<16;i++) {
            for (int j = 0; j < 16; j++) {
                if (mineGrid[i][j] == -2 || mineGrid[i][j] == -9) {
                    rect.set((int)(i*width),(int)(j*height),(int)(i*width+width),(int)(j*height+height));
                    picture.setBounds(rect);
                    picture.draw(canvas);



                }
            }
        }


    }
    void showNumbers(Canvas canvas){
        Paint background = new Paint();
        Rect rect =  new Rect();

        background.setColor(getResources().getColor(
        R.color.grid_background));
        background.setTextSize(15);
        Drawable drawable = getResources().getDrawable(R.drawable.mine);
        Drawable drawable1 = getResources().getDrawable(R.drawable.number1);
        Drawable drawable2 = getResources().getDrawable(R.drawable.number2);
        Drawable drawable3 = getResources().getDrawable(R.drawable.number3);
        Drawable drawable4 = getResources().getDrawable(R.drawable.number4);
        Drawable drawable5 = getResources().getDrawable(R.drawable.number5);
        Drawable drawable6 = getResources().getDrawable(R.drawable.number6);
        Drawable drawable7 = getResources().getDrawable(R.drawable.number7);
        Drawable drawable8 = getResources().getDrawable(R.drawable.number8);


        for(int i =0;i<16;i++){
            for(int j=0;j<16;j++){
                if(mineGrid[i][j]>=0){
                    String text = String.valueOf(mineGrid[i][j]);
                    rect.set((int)(i*width),(int)(j*height),(int)(i*width+width),(int)(j*height+height));
                    if(mineGrid[i][j]==0){

                        canvas.drawRect(rect,background);
                    }
                    if(mineGrid[i][j]==1){
                        drawable1.setBounds(rect);
                        drawable1.draw(canvas);
                    }
                    if(mineGrid[i][j]==2){
                        drawable2.setBounds(rect);
                        drawable2.draw(canvas);
                    }
                    if(mineGrid[i][j]==3){
                        drawable3.setBounds(rect);
                        drawable3.draw(canvas);
                    }
                    if(mineGrid[i][j]==4){
                        drawable4.setBounds(rect);
                        drawable4.draw(canvas);
                    }
                    if(mineGrid[i][j]==5){
                        drawable5.setBounds(rect);
                        drawable5.draw(canvas);
                    }
                    if(mineGrid[i][j]==6){
                        drawable6.setBounds(rect);
                        drawable6.draw(canvas);
                    }
                    if(mineGrid[i][j]==7){
                        drawable7.setBounds(rect);
                        drawable7.draw(canvas);
                    }
                    if(mineGrid[i][j]==8){
                        drawable8.setBounds(rect);
                        drawable8.draw(canvas);
                    }
                    //canvas.drawText(text,rect.exactCenterX(),rect.exactCenterY(),background);
                    //canvas.drawText(text,i*width-1,j*height+1,background);


                }

            }
        }


    }
    void showMines(Canvas canvas){
            Drawable picture = getResources().getDrawable(R.drawable.mine);
        Rect rect =  new Rect();

            for(int j=0;j<16;j++)
                for(int k =0;k<16;k++){
                    if(mineGrid[j][k]==-1){
                        rect.set((int)(j*width),(int)(k*height),(int)(j*width+width),(int)(k*height+height));
                        picture.setBounds(rect);
                        picture.draw(canvas);

                    }


                }






    }
    boolean checkForWinningCondition(){

        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){

                if( mineGrid[i][j]==-10|| mineGrid[i][j]==-9)
                    return false;
            }
        }
        return true;

    }



    void displayNumbers(int xPos , int yPos)
    {
        int start_x,start_y, end_x,end_y;
        int minesDetected=0;

        if(mineGrid[xPos][yPos]!=-1 && mineGrid[xPos][yPos]<0 && mineGrid[xPos][yPos]!=-2 && mineGrid[xPos][yPos]!=-9)// && gameGrid[xPos][yPos]<25 && gameGrid[xPos][yPos]!=-2)
        {

            if(xPos==0)
                start_x = xPos;
            else
                start_x = xPos-1;
            if(yPos==0)
                start_y = yPos;
            else
                start_y = yPos-1;

            if(xPos==15)
                end_x = xPos;
            else
                end_x = xPos+1;

            if(yPos==15)
                end_y = yPos;
            else
                end_y = yPos+1;

            for (int i=start_x; i<=end_x; i++) {
                for(int j= start_y ; j<=end_y ; j++)
                {
                    if(mineGrid[i][j]== -1 && mineGrid[xPos][yPos]!=-2){
                        minesDetected++;
                    }

                }
            }
            if(minesDetected==0)
            {
                mineGrid[xPos][yPos]=0;
                for(int i=start_x;i<=end_x;i++)
                {
                    for(int j=start_y; j<=end_y;j++)
                    {
                        displayNumbers(i,j);
                    }
                }
            }
            else
            {
                mineGrid[xPos][yPos]=minesDetected;
            }

        }

    }

    void setMines(){


        for(int i =0;i<noOfMines;i++)
        {
              Random rn=new Random();
                int x=rn.nextInt(Integer.MAX_VALUE)%16;
                int y = rn.nextInt(Integer.MAX_VALUE)%16;
                System.out.println("Mine at : "+x+ " "+ y);
                mineGrid[x][y]= -1; // Mines are set
        }



    }

}
