package com.ilm.babosametlica;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Juego extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    //como crear una carpeta en drawable
    private SurfaceHolder holder;
    public BucleJuego bucle;

    private Bitmap mapa,mapaCollider,fondo;

    public float mapaW,mapaH,maxX,maxY ;

    private final int IZQUIERDA=0;
    private final int DERECHA=1;
    private final int SALTO=2;
    private final int DISPARO=3;
    private final int AGACHADO=4;

    public float deltaT;

    public float[] posicionMapa = new float[2];
    public float suelo;

    private ArrayList<float[]> posicionesSuelo = new ArrayList<>();

    //Matriz de las dimensiones del mapa para guardar las posiciones del suelo
    public float[][] posicionesSueloMatriz = new float[(int) mapaW][(int) maxY];

    private float[] posicionSuelo = new float[2];
    public Marco marco;

    public Control[]  controles= new Control[5];
    private static final String TAG = Juego.class.getSimpleName();
    private float[] posicionAux = new float[2];
    private final int X= 0;
    private final int Y= 1;
    private ArrayList<Toque> toques= new ArrayList<>();

    public boolean sueloGuardado ;
    private boolean hayToque;

    private final int FRAMES_ASALTAR=7;

    private int contFramesAsaltar=0;
    private int contFramesAsaltarDer=0;
    private int contFramesAsaltarIzq=0;

    private boolean inicializado=false;

    public ArrayList<Disparo> listaDisparos= new ArrayList<Disparo>();
    public ArrayList<Disparo> listaDisparosSoldados= new ArrayList<Disparo>();
    public ArrayList<Soldado> listaSoldados= new ArrayList<Soldado>();
    public ArrayList<Drop> listaDrops= new ArrayList<Drop>();
    //enemigos

    //comprueba si se puede mover el mapa
    public boolean avanzar;

    //arma marco
    public int arma;
    private final int PISTOLA =0;
    private final int ESCOPETA=1;

    private float LIMITE_ESCENA2;


    //NIVEL:FRAMES A SALTAR PARA GENERAR UN NUEVO ENEMIGO
    private final int NIVEL1=160;
    private final int NIVEL2=100;
    private int NIVEL=NIVEL1;

    //CONTADOR DE ENEMIGOS A MATAR POR NIVEL
    private final int CONT_MUERTOS1=5;
    private final int CONT_MUERTOS2=10;
    private  int CONT_MUERTOS=CONT_MUERTOS1;

    //CUENTA LOS SOLDADOS GENERADOS
    private int contSoldadosNivelActual;

    //CUENTA LOS ENEMIGOS MUERTOS
    private int contadorMuertos=0;

    private boolean derrota;
    private boolean victoria;
    MediaPlayer mediaPlayer;

    Activity actividad;

    public Juego(Activity context) {
        super(context);
        actividad=context;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);


        inicializar();

        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //agregar el touch listener
        setOnTouchListener(this);
        //comenzar el bucle

        bucle.start();

    }

    //cuenta cuentas iteraciones hasta crear un nuevo soldado
    private int contGeneradorSoldados;
    private Bitmap spriteAvanzar;
    private int spriteAvanzarW,spriteAvanzarH;
    private int estadoGo;
    public void inicializar(){

        spriteAvanzar= BitmapFactory.decodeResource(getResources(),R.drawable.go) ;
        spriteAvanzarW=spriteAvanzar.getWidth()/15;
        spriteAvanzarH=spriteAvanzar.getHeight();


            //Bitmaps
            fondo=BitmapFactory.decodeResource(getResources(),R.drawable.nubes);
            sueloGuardado=false;
            mapa= BitmapFactory.decodeResource(getResources(),R.drawable.mapa);
            mapaCollider =BitmapFactory.decodeResource(getResources(),R.drawable.collider);

            mapaH = mapa.getHeight();
            mapaW = mapa.getWidth();
            LIMITE_ESCENA2=mapaW*0.50f;

            Canvas c = getHolder().lockCanvas();
            maxY=c.getHeight();
            maxX=c.getWidth();

            getHolder().unlockCanvasAndPost(c);

            deltaT= 1.0f/BucleJuego.MAX_FPS;

            posicionMapa[X]=0;
            posicionMapa[Y]=(maxY-mapaH)/2;


            //MARCO
            marco= new Marco(this);
            marco.posicionRelativaMarco[X]=0;

            marco.posicionAbsolutaMarco[X]=20;
            marco.velocidadMarco[X] = maxX/marco.tiempoRecorrerPantalla ;
            marco.velocidadMarco[Y] =  -marco.velocidadMarco[X]/2;
            marco.gravedadMarco[Y] =   marco.velocidadMarco[X];
            marco.arma=PISTOLA;

            controles[DERECHA]=new Control(getContext(), maxX*0.30f, maxY*0.78f);
            controles[DERECHA].cargar(R.drawable.flecha_dcha);
            controles[DERECHA].nombre="DERECHA";

            controles[SALTO]=new Control(getContext(), maxX*0.15f, maxY*0.78f);
            controles[SALTO].cargar(R.drawable.flecha_up);
            controles[SALTO].nombre="SALTO";

            controles[IZQUIERDA]=new Control(getContext(), 0, maxY*0.78f);
            controles[IZQUIERDA].cargar(R.drawable.flecha_izda);
            controles[IZQUIERDA].nombre="IZQUIERDA";


            controles[AGACHADO]=new Control(getContext(), maxX*0.65F, maxY*0.78f);
            controles[AGACHADO].cargar(R.drawable.flecha_abajo);
            controles[AGACHADO].nombre="ABAJO";


            controles[DISPARO]=new Control(getContext(), maxX*0.85f, maxY*0.78f);
            controles[DISPARO].cargar(R.drawable.botondisparo);
            controles[DISPARO].nombre="DISPARO";


           // posicionInicialMapa=posicionMapa[Y];



            crearSuelo();
           //Primer suelo
           suelo=maxY*0.465f+marco.marcoH;

            contGeneradorSoldados =0;

    }
    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */



    public void actualizar() {


        Log.d("mapaWidth", String.valueOf(LIMITE_ESCENA2));

        if(victoria || derrota)
            fin();
        else{
            if (sueloGuardado)
                contGeneradorSoldados++;

            //al llegar a X bajas el mapa puede avanzar y se avanza de nivel
            if(contadorMuertos>=CONT_MUERTOS){
                avanzar=true;
                contadorMuertos=0;
                //Cambio el limite del contador de muertos al de la segunda fase
                CONT_MUERTOS=CONT_MUERTOS2;
                if(NIVEL==NIVEL2)
                    victoria=true;
            }


            //GENERADOR SOLDADOS
            generarSoldados();

            if (!hayToque && !marco.morir){
                marco.direccion = marco.ultimaDireccionEjeX;
                marco.estadoMarco=0;
            }

            //Comprobar si marco esta en el suelo, si esta en el aire y no es por saltar, cayendo pasa a ser true

            if (sueloGuardado && posicionesSueloMatriz[(int) marco.posicionAbsolutaMarco[X]][(int) (marco.posicionRelativaMarco[Y] + marco.marcoH)] == 0 && !marco.saltando) {
                marco.direccion = SALTO;
                marco.cayendo = true;
                marco.saltando = true;

            }


            if(!marco.morir){
                //salto
                if (marco.saltando) {

                    marco.direccion = SALTO;
                    //detectar suelo
                    if (marco.posicionRelativaMarco[Y] > (suelo - marco.marcoH)) {
                        marco.velocidadMarco[Y] = -marco.velocidadMarco[X] / 2;
                        marco.posicionRelativaMarco[Y] = suelo - marco.marcoH;
                        marco.saltando = false;
                        marco.cayendo = false;

                    }//mientras esta en el aire
                    else {
                        //si esta en el aire por caerse

                        if (marco.cayendo) {

                            marco.posicionRelativaMarco[Y] += marco.gravedadMarco[Y] * deltaT;
                        } else {
                            //si esta en el aire por saltar
                            marco.velocidadMarco[Y] += marco.gravedadMarco[Y] * deltaT;
                            marco.posicionRelativaMarco[Y] += marco.velocidadMarco[Y] * deltaT;
                        }

        //dasad
                        marco.estadoMarco++;
                        if (marco.estadoMarco > 10)
                            marco.estadoMarco = 2;
                    }

                } else {
                    marco.posicionRelativaMarco[Y] = suelo - marco.marcoH;
                }
                //PAMTALLA1 PANTALLA2
                //MOVIMIENTO--MOVER MARCO

                if (controles[SALTO].pulsado && !marco.saltando) {
                    marco.saltando = true;
                    marco.posicionRelativaMarco[Y] += marco.velocidadMarco[Y] * deltaT;
                    marco.estadoMarco=0;
                }
                Log.d("PosAbsMarco", String.valueOf(marco.posicionAbsolutaMarco[X]));
                Log.d("PosAbsMapa", String.valueOf(posicionMapa[X]));

                if (controles[DERECHA].pulsado) {
                    marco.ultimaDireccionEjeX = DERECHA; //PARA DAR UNA DIRECCION AL TERMINAR UN SALTO

                    marco.direccion=DERECHA;

                    //Si llega al limite mientras avanza
                    if( posicionMapa[X]>LIMITE_ESCENA2){
                        NIVEL=NIVEL2;
                        avanzar=false;
                    }

                        if (marco.posicionRelativaMarco[X] > 0.8 * maxX && avanzar) {
                            posicionMapa[X] += marco.velocidadMarco[X] * deltaT;
                            marco.posicionAbsolutaMarco[X] += marco.velocidadMarco[X] * deltaT;
                   
                        } else {
                            marco.posicionRelativaMarco[X] += marco.velocidadMarco[X] * deltaT;
                            marco.posicionAbsolutaMarco[X] += marco.velocidadMarco[X] * deltaT;
                        }
                         if(contFramesAsaltarDer==FRAMES_ASALTAR){        

                             contFramesAsaltarDer=0;
                             marco.estadoMarco++;
                   
                        if (marco.estadoMarco > 14)
                            marco.estadoMarco = 3;
                    }
                    contFramesAsaltarDer++;


                }

                if (controles[IZQUIERDA].pulsado) {
                    marco.ultimaDireccionEjeX = IZQUIERDA;

                    if (!marco.saltando)
                        marco.direccion = IZQUIERDA;

                    //si la posicion a actualizar es valida
                    contFramesAsaltarIzq++;


                        marco.posicionRelativaMarco[X] -= marco.velocidadMarco[X] * deltaT;
                        marco.posicionAbsolutaMarco[X] -= marco.velocidadMarco[X] * deltaT;
                        if(contFramesAsaltarIzq==FRAMES_ASALTAR) {
                           contFramesAsaltarIzq = 0;
                           marco.estadoMarco++;

                        if (marco.estadoMarco > 14)
                            marco.estadoMarco = 3;
                    }
                }

                if (controles[AGACHADO].pulsado){
                    marco.agachado=true;
                    marco.estadoMarco=0;

                } else
                    marco.agachado=false;


                try {
                    marco.actualizar();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //DISPARO

                Log.d("CAYENDO", String.valueOf(marco.cayendo));
                Log.d("SALTANDA", String.valueOf(marco.saltando));
                if (marco.disparando) {


                    contFramesAsaltar++;

                    if (contFramesAsaltar == FRAMES_ASALTAR) {
                        marco.estadoDisparo++;
                        contFramesAsaltar = 0;

                    }

                    if (marco.estadoDisparo == 3) {
                        marco.disparando = false;
                        marco.estadoDisparo = 0;
                    }

                }
                if (controles[DISPARO].pulsado && !marco.disparando) {
                    marco.disparando = true;
                    marco.disparar();

                }
            }

           //COLISIONES

            for (Iterator<Disparo> it_disparos = listaDisparos.iterator(); it_disparos.hasNext(); ) {
                Disparo d = it_disparos.next();
                if (posicionValida(d.posicionDiparo[X],d.posicionDiparo[Y]))
                    d.actualizarPos();
                else
                    it_disparos.remove();
            }

            for (Iterator<Disparo> it_disparos = listaDisparosSoldados.iterator(); it_disparos.hasNext(); ) {
                Disparo d = it_disparos.next();

                if(!marco.morir){
                    if ( posicionValida(d.posicionDiparo[X],d.posicionDiparo[Y])){

                        if (colisionMarco(d)) {
                            //marco.armaMuerte=d.arma;
                            marco.estadoMarco=0;
                            Log.d("morir", String.valueOf(marco.morir));

                            marco.morir=true;

                        }

                        d.actualizarPos();
                    }else{
                        try {
                            it_disparos.remove();
                        } catch (Exception ex) {}
                    }
                }

            }
            posicionAux[X] = marco.posicionRelativaMarco[X];
            posicionAux[Y] = marco.posicionRelativaMarco[Y];

            //ENEMIGOS

            for (Iterator<Soldado> it_soldados = listaSoldados.iterator(); it_soldados.hasNext(); ) {
                Soldado s = it_soldados.next();

                //Colocar soldado en el suelo
                s.actualizar();
                if (s.posicion[X] > maxX || s.posicion[X] < 0 || s.muerto) {
                    if(s.dropea){
                        Drop drop= new Drop(this,s.posicion,s.soldadoH);
                        listaDrops.add(drop);
                    }
                    it_soldados.remove();
                }


            }

            //Colision disparo-soldado
            for (Iterator<Disparo> it_disparos = listaDisparos.iterator(); it_disparos.hasNext(); ) {
                Disparo d = it_disparos.next();
                for (Iterator<Soldado> it_soldados = listaSoldados.iterator(); it_soldados.hasNext(); ) {
                    Soldado s = it_soldados.next();
                    if (!s.morir) {
                        if (colisionDisparo(s, d)) {
                            s.estado = 0;
                            contadorMuertos++;
                            s.armaMuerte = d.arma;

                            Log.d("Muertox", String.valueOf(s.posicion[X]));
                            Log.d("MuertoxDisparo", String.valueOf(d.posicionDiparo[X]));

                            s.morir = true;

                            if(d.arma!=ESCOPETA){
                                try {
                                    it_disparos.remove();

                                } catch (Exception ex) {
                                }
                            }


                        }
                    } else {
                        if (s.muerto) {
                            try {
                                it_soldados.remove();
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
            }

            //colision marco drop
            for (Iterator<Drop> it_drops = listaDrops.iterator(); it_drops.hasNext(); ) {
                Drop drop = it_drops.next();

                //Si marco coge el drop
                if(colisionDrop(drop)){
                    mediaPlayer= MediaPlayer.create(getContext(), R.raw.flameshot);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                    mediaPlayer.start();
                    Log.d("tocado","true");
                    //drop.dibujar=false;
                    it_drops.remove();
                    marco.arma=ESCOPETA;
                }
                //Si la pantalla puede avanzar, todos los drops desaparecen

                if(avanzar){
                    it_drops.remove();

                }

            }

        }


        //se actualiza la animacion de go->->
        contGo++;
        if(avanzar && contGo>=FRAMES_ASALTAR){
            estadoGo++;
            contGo=0;
            if(estadoGo>=15)
                estadoGo=0;
        }


        //ACTUALIZO
        try {
            marco.actualizar();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(marco.muerto)
            derrota=true;
        Log.d("posMarcox", String.valueOf(marco.posicionRelativaMarco[X]));



    }

    private int contGo=0;
    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {

        canvas.drawColor(Color.BLACK);

        //pintar mensajes que nos ayudan
        Paint p=new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.RED);
        p.setTextSize(50);

        deltaT=1.0f/BucleJuego.MAX_FPS;


        canvas.drawBitmap(fondo,
                new Rect((int) 0, 0,  fondo.getWidth(),  fondo.getHeight()),
                new Rect(0,0, (int) maxX, (int) maxY),
                p);
        canvas.drawBitmap(mapa,
                new Rect((int) posicionMapa[X], 0, (int) (maxX+posicionMapa[X]), (int) (mapaH)),
                new Rect(0, (int)posicionMapa[Y], (int) maxX, (int) (posicionMapa[Y]+mapaH)),
                p);

        marco.dibujarMarco(canvas,p);

        for (Control c : controles)
            c.dibujar(canvas,p);

        if(marco.disparando )
            marco.animDisparando();

        for(Disparo d : listaDisparos)
            d.dibujarDisparo(canvas,p);

        for(Soldado s: listaSoldados)
            s.dibujar(canvas,p);
        for(Disparo d : listaDisparosSoldados){
            d.dibujarDisparo(canvas,p);
        }
        for(Drop drop: listaDrops)
            drop.dibujar(canvas,p);

        if(avanzar)
            dibujarGo(canvas,p);

        if(victoria){
            p.setAlpha(0);
            p.setColor(Color.WHITE);
            p.setTextSize(maxX /15);
            float textWidth = p.measureText("VICTORIA!");
            canvas.drawText("VICTORIA!", (maxX/2)-(textWidth/2), maxY /2, p);

        }
        else if(derrota){
            p.setAlpha(0);
            p.setColor(Color.WHITE);
            p.setTextSize(maxX /15);
            String texto="GAME OVER";
            float textWidth = p.measureText(texto);
            canvas.drawText(texto,(maxX/2)-(textWidth/2), maxY /2, p);


        }else {
            p.setAlpha(0);
            p.setColor(Color.WHITE);
            p.setTextSize(maxX /30);
            canvas.drawText("Enemigos restantes:", 50, maxY /5, p);
            p.setTextSize(maxX /30);
            canvas.drawText(String.valueOf(CONT_MUERTOS-contadorMuertos), 50+(maxX /2), maxY /5, p);
        }



    }


    //GENERA SOLDADOS DEPENDIENDO DEL NIVEL
    private void generarSoldados(){
        if (sueloGuardado && contGeneradorSoldados>=NIVEL && !avanzar ) {
            contSoldadosNivelActual++;
            contGeneradorSoldados=0;
            if(contSoldadosNivelActual>=NIVEL){
                NIVEL=NIVEL2;

            }else{
                Log.d("Soldado creado", "Soldado creado");
                Random r1 = new Random();
                int direccion = r1.nextInt(2);

                listaSoldados.add(new Soldado(this, direccion));
                Log.d("Soldado creado", String.valueOf(listaSoldados.size()));
            }

        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }


    //COLISION DISPARO-SOLDADO
    public boolean colisionDisparo(Soldado s, Disparo d){
        Bitmap soldado=s.bitmap;
        Bitmap disparo;

        //SOLDADO DERECHA
        if(s.posicion[X]>d.posicionDiparo[X]) {
            if (d.arma == PISTOLA) {
                disparo = d.disparoDer;
                return Colision.hayColision(soldado, (int) s.posicion[X], (int) s.posicion[Y],
                        disparo, (int) d.posicionDiparo[X] , (int) d.posicionDiparo[Y]
                );
            } else {
                disparo = d.disparoLlamas;
                //Le resto el width porque empieza a
                return Colision.hayColision3(  soldado, (int) (s.posicion[X] ), (int) s.posicion[Y],
                        disparo, (int) (d.posicionDiparo[X]), (int) d.posicionDiparo[Y]

                );
            }
            //SOLDADO IZQUIERDA
        }else{
            if(d.arma==PISTOLA){
                disparo = d.disparoIzq;

                return Colision.hayColision2(soldado, (int) s.posicion[X], (int) s.posicion[Y],
                        disparo, (int) (d.posicionDiparo[X]), (int) d.posicionDiparo[Y]
                );
            }
            else{
                disparo = d.disparoLlamas;
                return Colision.hayColision4(soldado, (int) s.posicion[X], (int) s.posicion[Y],
                        disparo, (int) (d.posicionDiparo[X]+d.disparoW*7), (int) d.posicionDiparo[Y]
                );
            }

        }

    }
    //COLISION DISPARO-MARCO
    public boolean colisionMarco( Disparo d){

        Bitmap disparo = d.disparoSoldado;;

            return Colision.hayColision(marco.bitmap, (int)(marco.posicionRelativaMarco[X]), (int)(marco.posicionRelativaMarco[Y]),
                    disparo, (int) d.posicionDiparo[X], (int) d.posicionDiparo[Y]
            );

    }

    //COLISION MARCO-DROP
    public boolean colisionDrop( Drop d){

        Bitmap drop = d.bitmap;;

            return Colision.hayColision(marco.bitmap, (int)(marco.posicionRelativaMarco[X]), (int)(marco.posicionRelativaMarco[Y]),
                    drop, (int) d.posicion[X], (int) d.posicion[Y]);

    }

    public void dibujarGo(Canvas canvas, Paint p){
        canvas.drawBitmap(spriteAvanzar,
                new Rect((estadoGo*spriteAvanzarW), 0, (estadoGo*spriteAvanzarW+spriteAvanzarW), spriteAvanzarH),
                new Rect((int) (maxX*0.9), (int) (posicionMapa[Y]+posicionMapa[Y]/2),
                        (int) ( (maxX*0.9)+spriteAvanzarW), (int)(posicionMapa[Y]+posicionMapa[Y]/2)+spriteAvanzarH),
                p);
    }
    //Comprueba si la posicion esta dentro de la pantalla
    public boolean posicionValida(float x, float y){

        if(x<maxX && x>0 && y<maxY && y>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index;
        int x,y;

        // Obtener el pointer asociado con la acción
        index = event.getActionIndex();


        x = (int) event.getX(index);
        y = (int) event.getY(index);

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                hayToque=true;

                synchronized(this) {
                    toques.add(index, new Toque(index, x, y));
                }

                //se comprueba si se ha pulsado
                for(int i=0;i<controles.length;i++)
                    controles[i].compruebaPulsado(x,y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                synchronized(this) {
                    toques.remove(index);
                }

                //se comprueba si se ha soltado el botón
                for(int i=0;i<controles.length;i++)
                    controles[i].compruebaSoltado(toques);
                break;

            case MotionEvent.ACTION_UP:
                synchronized(this) {
                    toques.clear();
                }
                hayToque=false;
                //se comprueba si se ha soltado el botón
                for(int i=0;i<controles.length;i++)
                    controles[i].compruebaSoltado(toques);
                break;
        }

        return true;
    }
    public void crearSuelo(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                posicionesSueloMatriz=Colision.posicionesConColor( mapa, 0, (int)posicionMapa[Y],mapaCollider, (int) 0, (int) posicionMapa[Y], (int) (mapaW), (int) (maxY));
                sueloGuardado=true;

            }
        });
        thread.start();

    }
    public void fin(){


        try {
            bucle.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<listaSoldados.size();i++)
            listaSoldados.get(i).bitmap.recycle();

        marco.bitmap.recycle();

    }


}
