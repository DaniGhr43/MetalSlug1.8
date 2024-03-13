package com.ilm.babosametlica;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;

public class Marco {


    //BITMAPS Y  DIMENSIONES MARCO
    public Bitmap marcoDer,marcoIzq,marcoDisparo1,marcoDisparo2,bitmap,marcoMuerte, marcoAgachado;
    public float marcoW, marcoH, marcoDisparoW1, marcoDisparoH1, marcoMuerteW,marcoMuerteH;
    public int arma;

    //POSICIONEAS
    private final int X= 0;
    private final int Y= 1;
    public float tiempoRecorrerPantalla= 5;

    public float[] velocidadMarco = new float[2];
    public float[] gravedadMarco = new float[2];
    public float[] posicionRelativaMarco = new float[2];    //Relativo a la pantalla
    public float[] posicionAbsolutaMarco = new float[2];    //Relativo a el ancho del mapa
    public float[] ultimaPosRelValida = new float[2];
    public float[] ultimaPosAbsValida = new float[2];

    //DIRECCIONES
    private final int IZQUIERDA=0;
    private final int DERECHA=1;
    public int direccion;
    public int ultimaDireccionEjeX;

    //ESTADOS
    public boolean agachado;
    public boolean puedeAgacharse=true;
    public int estadoMarco=0;
    public int estadoDisparo=0;

    public boolean saltando;
    public boolean cayendo;
    public boolean disparando;
    private Canvas canvas;
    private Paint p;
    private Juego juego;
    public boolean muerto;

    public boolean morir=false;
    public Marco(Juego juego){
        this.juego=juego;
        marcoDer = BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcoder);
        marcoIzq = BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcoizq);
        marcoH = marcoDer.getHeight();
        marcoW = marcoDer.getWidth()/15;


        // marcoSalto = BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcodisparoder);
        // marcoSaltoW=marcoSalto.getWidth()/10;
        // marcoSaltoH=marcoSalto.getHeight();

        //animaciones distintas al disparar
        marcoDisparo1= BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcodisparoder);
        marcoDisparo2= BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcodisparoizq);
        marcoDisparoW1 =marcoDisparo1.getWidth()/4;
        marcoDisparoH1 =marcoDisparo1.getHeight();


        marcoMuerte=BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcomuerte);
        marcoMuerteW =marcoMuerte.getWidth()/16;
        marcoMuerteH =marcoMuerte.getHeight();

        marcoAgachado = BitmapFactory.decodeResource(juego.getResources(),R.drawable.marcoagachado );
        bitmap=marcoDer;

    }

    public void dibujarMarco(Canvas canvas, Paint p){
        this.canvas=canvas;
        this.p=p;

        if(!disparando){
            canvas.drawBitmap(bitmap,
                    new Rect((int) ((estadoMarco*marcoW)), (int)0, (int) (estadoMarco*marcoW+marcoW), (int)marcoH),
                    new Rect((int) posicionRelativaMarco[X], (int) (posicionRelativaMarco[Y]),
                            (int) (posicionRelativaMarco[X]+marcoW), (int)(posicionRelativaMarco[Y]+marcoH)),
                    p);

        }

    }
        public void actualizar() throws InterruptedException {

            //ACTUALIZO SI ESTA EN UNA POSICION VALIDA
            if(juego.posicionValida(posicionRelativaMarco[X],posicionRelativaMarco[Y])){

                //SI LA POSICION ES VALIDA LA GUARDO POR SI EN LA SIGUIENTE ITERACION NO LO ES
                ultimaPosRelValida[X]=posicionRelativaMarco[X];
                ultimaPosAbsValida[X]=posicionAbsolutaMarco[X];

                //COLOCAR EN EL SUELO SI NO ESTA AGACHADO
                if (juego.sueloGuardado && !agachado) {

                    for (int j = (int) ( posicionRelativaMarco[Y]+marcoH/2); j < juego.posicionesSueloMatriz[(int) posicionAbsolutaMarco[X]].length; j++) {
                        if (juego.posicionesSueloMatriz[(int) posicionAbsolutaMarco[X]][j] == 1) {
                            juego.suelo = j;
                            j = juego.posicionesSueloMatriz[(int) posicionAbsolutaMarco[X]].length;
                        }
                    }
                }


                //SI ESTA AGACHADO
                if(agachado){
                    bitmap=marcoAgachado;
                    marcoW=marcoAgachado.getWidth();
                    marcoH=marcoAgachado.getHeight();
                    //si es la primera vez que se agacha despues de estar levantado actualizo la posicion en Y
                    if(puedeAgacharse){
                        posicionRelativaMarco[Y]+=marcoH;
                        puedeAgacharse=false;
                    }
                //SI NO ESTA AGACHADO
                }else{
                    marcoW=marcoDer.getWidth()/15;
                    marcoH=marcoDer.getHeight();
                }



                //SI ESTA VIVO
                if(!morir){
                    //DIRECCION
                    switch (direccion){
                        case IZQUIERDA:
                            bitmap=marcoIzq;
                            break;
                        case DERECHA:
                            bitmap=marcoDer;
                            break;

                    }

                    if(disparando && (direccion==DERECHA))
                        bitmap=marcoDisparo1;
                    else if(disparando)
                        bitmap=marcoDisparo2;


                //MURIENDO, NO MUERTO TODAVIA
                }else{
                    marcoW=marcoMuerteW;
                    marcoH=marcoMuerteH;
                    estadoMarco++;
                    bitmap=marcoMuerte;
                    if(estadoMarco>16){
                        muerto=true;
                    }
                }

                //SI LA POSICION NO ES VALIDA, LA CAMBIO POR LA ULTIMA VALIDA
            }else{
                posicionRelativaMarco[X]=ultimaPosRelValida[X];
                posicionAbsolutaMarco[X]=ultimaPosAbsValida[X];
            }



        }

        public void animDisparando(){

                canvas.drawBitmap(bitmap,
                        new Rect((int) ((estadoDisparo*marcoDisparoW1)), 0, (int) (estadoDisparo*marcoDisparoW1+marcoDisparoW1), (int)marcoDisparoH1),
                        new Rect((int) (posicionRelativaMarco[X]), (int) (posicionRelativaMarco[Y]),
                                (int) (posicionRelativaMarco[X]+marcoDisparoW1), (int)(marcoDisparoH1+ posicionRelativaMarco[Y])),
                        p);

    }

    public void disparar(){

        Log.d("direcc", String.valueOf(direccion));
        //creo un disparo, lo añado a una lista y recorro la lista en cada actualizacion  con un iterator y dibujo todos los diparos
        Disparo d = new Disparo(juego, direccion,arma);
        juego.listaDisparos.add(d);
    }
}
