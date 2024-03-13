package com.ilm.babosametlica;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

public class Soldado {
    private final int IZQUIERDA=0;
    private final int DERECHA=1;
    private final int X=0;
    private final int Y=1;
    private Juego juego;
    private float velocidad;
    private float tiempoRecorrerPantalla=8;
    public Bitmap bitmap,soldadomuerte,soldadomuertellamas,soldadoDer,soldadoIzq,soldadoParadoIzq,soldadoParadoDer;
    public float[] posicion= new float[2];
    public float posicionFinal;
    public float soldadoW,soldadoH;
    private int cont;
    //estados del soldado
    public boolean morir;
    public boolean muerto;
    public boolean parado=false;
    public int estado=0;
    private int direccion;
    public int armaMuerte;
    private final int PISTOLA =0;
    private final int ESCOPETA =1;
    private final int PISTOLASOLDADO =2;
    public boolean dropea;
    public Bitmap soldadoParado;
    private final int FRAMES_ASALTAR=15;
    private int contFrames;
    public Soldado(Juego juego,int direccion){
        this.juego=juego;
        velocidad=juego.maxX/tiempoRecorrerPantalla;
        this.direccion=direccion;
        //BITMAPS


        soldadoDer= BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadocorrerder);

        soldadoIzq= BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadocorrerizq);

        if(direccion==DERECHA)
            bitmap=soldadoDer;
        else
            bitmap=soldadoIzq;

        soldadoParadoIzq=BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadoparadoizq);
        soldadoParadoDer=BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadoparadoder);

        soldadoW=bitmap.getWidth()/12;
        soldadoH=bitmap.getHeight();

        soldadomuerte=BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadomuerte);
        soldadomuertellamas=BitmapFactory.decodeResource(juego.getResources(), R.drawable.soldadomuertellamas);

        Random r= new Random();
        posicionFinal = r.nextInt((int) juego.maxX);

        int re=r.nextInt(10);
        //10% de probabilidad de que dropee
        if(re==1)
            dropea=true;

        if(direccion==DERECHA)
            posicion[X]=0;
        else
            posicion[X]= juego.maxX;
        for(int i = (int) juego.posicionMapa[Y]; i<juego.posicionMapa[Y]+juego.mapaH; i++){
            if(juego.posicionesSueloMatriz[0][i]==1){
                posicion[Y]=i-soldadoH;
                i= (int) (juego.posicionMapa[Y]+juego.mapaH-1);
            }
        }

    }

    public void actualizar(){



        //SOLDADO VIVO Y EN MOVIMIENTO
        if(!morir && !parado){
            //andar
            if(direccion==DERECHA)
                bitmap=soldadoDer;
            else
                bitmap=soldadoIzq;

            soldadoW=bitmap.getWidth()/12;
            soldadoH=bitmap.getHeight();

            //Si se desplaza a la derecha
            if(direccion==DERECHA){
                posicion[X]+=velocidad* juego.deltaT;

                //Parar soldado
                if(posicionFinal<(int)posicion[X]){

                    //Comprobar en que direccion apuntar
                    if(posicion[X]>juego.marco.posicionRelativaMarco[X])  {//Si esta a la derecha de marco
                        bitmap=soldadoParadoIzq;
                        direccion=IZQUIERDA;

                    }else   {
                        bitmap=soldadoParadoDer;                            //Si esta a la izquierda de marco
                        direccion=DERECHA;
                    }

                    parado=true;
                    disparar();
                    bitmap=soldadoParadoDer;
                    soldadoW=bitmap.getWidth();
                    soldadoH=bitmap.getHeight();
                    estado=0;
                }
            //Si se desplaza a la izquierda
            }else{
                posicion[X]-=velocidad* juego.deltaT;
                //Parar soldado
                if(posicionFinal>(int)posicion[X]){

                    //Comprobar en que direccion apuntar
                    if(posicion[X]>juego.marco.posicionRelativaMarco[X])  {//Si esta a la derecha de marco
                        bitmap=soldadoParadoIzq;
                        direccion=IZQUIERDA;

                    }else   {
                        bitmap=soldadoParadoDer;                            //Si esta a la izquierda de marco
                        direccion=DERECHA;
                    }


                    parado=true;
                    disparar();

                    soldadoW=bitmap.getWidth();
                    soldadoH=bitmap.getHeight();
                    estado=0;
                }
            }

            //animacion
            if(!parado){
                estado++;
                if(estado>11)
                    estado=0;
            }

            //Saltar FRAMES
            cont++;
            if(cont==2)
                cont=0;

                                                                                                    //SOLDADO VIVO PARADO
        }else if(!morir){
            contFrames++;
            //Si esta parado, el soldado espera a moverse
            if(contFrames==FRAMES_ASALTAR){
                Random r= new Random();
                posicionFinal = r.nextInt((int) juego.maxX);

                if(posicionFinal>posicion[X])
                    direccion=DERECHA;
                else
                    direccion=IZQUIERDA;

                parado=false;
                contFrames=0;
            }

                                                                                                    //SOLDADO MURIENDO
        }else if(morir){
            //cambio la direccion para que este acorde a la animacion de la muerte
            direccion=DERECHA;
            if(armaMuerte==PISTOLA){
                soldadoW=soldadomuerte.getWidth()/12;
                soldadoH=soldadomuerte.getHeight();

                estado++;
                bitmap=soldadomuerte;
                if(estado>11)
                    muerto=true;
            }
            if(armaMuerte==ESCOPETA) {
                bitmap=soldadomuertellamas;
                soldadoW = soldadomuertellamas.getWidth() / 8;
                soldadoH = soldadomuertellamas.getHeight();

                Log.d("soldadoH", String.valueOf(soldadoH));
                Log.d("posY", String.valueOf(posicion[Y]));


                estado++;
                if (estado == 8)
                    muerto = true;
            }

        }


        //poner en suelo
        //(posicion[Y]+soldadoH/2) divido entre dos porque sino al morir la animacion es erronea
        for (int j = (int) (posicion[Y]+soldadoH/2); j < juego.posicionesSueloMatriz[(int) (posicion[X]+juego.posicionMapa[X])].length; j++) {
            //si es suelo
            if (juego.posicionesSueloMatriz[(int) (posicion[X]+juego.posicionMapa[X])][j] == 1) {
                posicion[Y]=j-soldadoH;
                j=juego.posicionesSueloMatriz[(int) (posicion[X]+juego.posicionMapa[X])].length-1;

            }
        }

    }
    public void dibujar(Canvas c, Paint p){

                c.drawBitmap(bitmap,
                        new Rect((int) (soldadoW*estado), 0, (int)  (soldadoW*estado+soldadoW), (int)soldadoH),
                        new Rect((int)posicion[X],  (int) (posicion[Y]),
                                (int) (posicion[X]+soldadoW), (int)( posicion[Y]+soldadoH)),p);



    }

    public void disparar(){
        Log.d("direcc", String.valueOf(direccion));
        Disparo d = new Disparo(juego, direccion,posicion[X], posicion[Y],PISTOLASOLDADO,juego.marco.posicionRelativaMarco);
        juego.listaDisparosSoldados.add(d);
    }

}
