package com.ilm.babosametlica;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.math.MathUtils;

public class Disparo  {

    public Bitmap disparoDer,disparoIzq, disparoSoldado, disparoLlamas;


    public float disparoW,disparoH;
    private final int X=0;
    private final int Y=1;

    public float[] posicionDiparo = new float[2];

    private final int IZQUIERDA=0;
    private final int DERECHA=1;
    public int direccion;
    private Juego juego;

    private float velocidad;
    private float tiempoRecorrerPantalla= 1.5f;

    public final int PISTOLA =0;
    public final int ESCOPETA=1;

    public final int PISTOLASOLDADO =2;
    public int arma ;

    public float soldadoX,soldadoY;
    private float[] posicionMarco;
    int[] velocidadXY=new int[2];

    public int estado;

    //DISPARO MARCO
    private MediaPlayer mediaPlayer;
    public Disparo(Juego juego , int direccion,int arma){
        this.arma=arma;
        this.juego=juego;


        if (arma ==PISTOLA) {
            mediaPlayer= MediaPlayer.create(juego.getContext(), R.raw.pistolshhot);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();

            if(direccion==DERECHA)
                posicionDiparo[X]=juego.marco.posicionRelativaMarco[X]+juego.marco.marcoW;
            else
                posicionDiparo[X]=juego.marco.posicionRelativaMarco[X]-juego.marco.marcoW;

            posicionDiparo[Y]=juego.marco.posicionRelativaMarco[Y]+juego.marco.marcoH/2.5f;

            disparoDer= BitmapFactory.decodeResource(juego.getResources(), R.drawable.animbala);
            disparoIzq= BitmapFactory.decodeResource(juego.getResources(), R.drawable.animbalaizq);

            disparoW=disparoDer.getWidth();
            disparoH=disparoDer.getHeight();


        }else{
            mediaPlayer= MediaPlayer.create(juego.getContext(), R.raw.shotgun);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();

            if(direccion==DERECHA)
                disparoLlamas= BitmapFactory.decodeResource(juego.getResources(), R.drawable.animbalallama);
            else{
                estado=7;
                disparoLlamas= BitmapFactory.decodeResource(juego.getResources(), R.drawable.animbalallamaizq);

            }

            disparoW=disparoLlamas.getWidth()/7;
            disparoH=disparoLlamas.getHeight();

            posicionDiparo[X]=juego.marco.posicionRelativaMarco[X]+juego.marco.marcoW-disparoW;
            posicionDiparo[Y]=juego.marco.posicionRelativaMarco[Y]+juego.marco.marcoH/4;



        }
        this.direccion=direccion;


        velocidad=juego.maxX/tiempoRecorrerPantalla;
    }

    //DISPARO SOLDADO
    public Disparo(Juego juego, int direccion, float soldadoX, float soldadoY,int arma, float[] posicionMarco){
        this.posicionMarco=posicionMarco;
        this.arma=arma;
        this.juego=juego;

        mediaPlayer= MediaPlayer.create(juego.getContext(), R.raw.pistolshhot);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();

        posicionDiparo[X]=soldadoX;
        posicionDiparo[Y]=soldadoY;
        disparoSoldado= BitmapFactory.decodeResource(juego.getResources(), R.drawable.balasoldado);
        this.soldadoX=soldadoX;
        this.soldadoY=soldadoY;
        this.direccion=direccion;
        velocidad=juego.maxX/tiempoRecorrerPantalla ;

        actualizarPosDis();

    }

    //Metodo para calcular la velocidad en X e Y
    public void actualizarPosDis() {
        //distancias en X e Y
        double distanciaX = posicionMarco[X]-posicionDiparo[X] ;
        double distanciaY = posicionMarco[Y]-posicionDiparo[Y] ;
        // Ecuacion para calcular distancia entre dos puntos
        double distancia = Math.sqrt(Math.pow( distanciaX , 2) + Math.pow(distanciaY , 2));

        velocidadXY[X] = (int)((distanciaX / distancia)* velocidad);
        velocidadXY[Y] = (int)((distanciaY / distancia)* velocidad);

        Log.d(" velocidadXY[X]", String.valueOf(distanciaX));
        Log.d(" velocidadXY[Y]", String.valueOf(distanciaY));
    }
    public void actualizarPos(){


        if(arma==PISTOLA){                                       //DISPARA MARCO CON PISTOLA
            if(direccion==DERECHA)
                posicionDiparo[X] +=velocidad* juego.deltaT;
            else if(direccion== IZQUIERDA)
                posicionDiparo[X] -=velocidad* juego.deltaT;
        }else if (arma==PISTOLASOLDADO){                        //DISPARA SOLDADO
            posicionDiparo[X] +=velocidadXY[X]/2* juego.deltaT;
            posicionDiparo[Y] += velocidadXY[Y]/2* juego.deltaT;

        }else if(arma==ESCOPETA){                               //DISPARA MARCO CON ESCOPETA
            if(direccion==DERECHA){
                posicionDiparo[X] +=velocidad* juego.deltaT;
            }
            else if(direccion== IZQUIERDA){
                posicionDiparo[X] -=velocidad* juego.deltaT;
            }


            if(direccion==DERECHA){
                estado++;
                if(estado==7)
                    estado=5;
            }else if(direccion==IZQUIERDA){
                estado--;
                if(estado==0)
                    estado=3;
            }


        }
        Log.d("estadoSoldado", String.valueOf(estado));

    }

    public void dibujarDisparo(Canvas canvas , Paint p){

        if(arma==PISTOLA ){
            if(direccion==DERECHA)
                canvas.drawBitmap(disparoDer, posicionDiparo[X],  posicionDiparo[Y],   p);
            else if(direccion== IZQUIERDA)
                canvas.drawBitmap(disparoIzq, posicionDiparo[X],  posicionDiparo[Y],   p);
        }
        else if(arma==PISTOLASOLDADO)
            canvas.drawBitmap(disparoSoldado, posicionDiparo[X],  posicionDiparo[Y],   p);
        else if(arma==ESCOPETA){
            canvas.drawBitmap(disparoLlamas,
                    new Rect((int) (disparoW*estado), 0, (int)  (disparoW*estado+disparoW), (int)disparoH),
                    new Rect((int) posicionDiparo[X],  (int) ( posicionDiparo[Y]),
                            (int) (posicionDiparo[X]+disparoW), (int)( posicionDiparo[Y]+disparoH)),
                    p);
        }


    }


}
