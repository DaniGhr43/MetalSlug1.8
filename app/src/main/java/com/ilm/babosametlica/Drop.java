package com.ilm.babosametlica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Drop {

    private final int X=0;
    private final int Y=1;
    public float[] posicion= new float[2];

    public int dropW,dropH;

    public Bitmap bitmap;

    public boolean dibujar;
    public Drop(Juego juego,float[] posicion,float soldadoH){

        bitmap= BitmapFactory.decodeResource(juego.getResources(), R.drawable.drop1);
        dropW=bitmap.getWidth();
        dropH=bitmap.getHeight();

        this.posicion[X]= posicion[X];
        this.posicion[Y]= posicion[Y]+soldadoH-dropH;
        dibujar=true;
    }


    public void dibujar(Canvas canvas, Paint p){
        //dibujar no funciona
        if(dibujar){
            canvas.drawBitmap(bitmap,
                    new Rect((int) (0), (int)0, (int) (dropW), (int)dropH),
                    new Rect((int) posicion[X], (int) posicion[Y],
                            (int) posicion[X]+dropW, (int)(posicion[Y]+dropH)),p);

        }

    }

}
