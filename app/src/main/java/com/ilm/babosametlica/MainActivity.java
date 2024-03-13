package com.ilm.babosametlica;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mediaPlayer= MediaPlayer.create(this, R.raw.cancion);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.setVolume(0.05f,0.05f);
        mediaPlayer.start();

        ConstraintLayout layout = findViewById(R.id.cl);

        layout.setBackgroundResource(R.drawable.fondo0);

        ImageView letras0 = findViewById(R.id.letras1);
        letras0.setVisibility(ImageView.VISIBLE);

        ImageView letras= findViewById(R.id.letras1);
        int letrasW= letras.getWidth();

        ImageView letras1 = findViewById(R.id.letras2);

        Animation animacion = AnimationUtils.loadAnimation(this,R.anim.intro);
        Animation animletras1 = AnimationUtils.loadAnimation(this,R.anim.letras1);
        Animation boton = AnimationUtils.loadAnimation(this,R.anim.delaybalas);

        animacion.setFillAfter(true);
        animletras1.setFillAfter(true);


        letras0.startAnimation(animacion);
        letras1.startAnimation(animletras1);

        ImageButton btn = findViewById(R.id.btn);
        btn.startAnimation(boton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActividadJuego.class);
                startActivity(intent);
            }
        });


    }



}