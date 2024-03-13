package com.ilm.babosametlica;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;


public class Colision {
    private final static int X= 0;
    private final static int Y= 1;
    public static boolean hayColision(Bitmap bitmap1, int x1, int y1,
                                      Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth()/15, y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect limitesColision = limitesColision(bounds1, bounds2);
            for (int i = limitesColision.left; i < limitesColision.right; i++) {
                for (int j = limitesColision.top; j < limitesColision.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean colisionDrop(Bitmap bitmap1, int x1, int y1,
                                      Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect limitesColision = limitesColision(bounds1, bounds2);
            for (int i = limitesColision.left; i < limitesColision.right; i++) {
                for (int j = limitesColision.top; j < limitesColision.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean hayColision2(Bitmap bitmap1, int x1, int y1,
                                      Bitmap bitmap2, int x2, int y2) {


            Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
            Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

            if (Rect.intersects(bounds1, bounds2)) {
                Rect limitesColision = limitesColision(bounds1, bounds2);
                for (int i = limitesColision.left; i < limitesColision.right; i++) {
                    for (int j = limitesColision.bottom; j < limitesColision.top; j--) {
                        int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                        int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                        if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                            return true;
                        }
                    }
                }
            }
            return false;
    }



    public static boolean hayColision3(Bitmap bitmap1, int x1, int y1,
                                      Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth()/7, y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect limitesColision = limitesColision(bounds1, bounds2);
            for (int i = limitesColision.left; i < limitesColision.right; i++) {
                for (int j = limitesColision.top; j < limitesColision.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hayColision4(Bitmap bitmap1, int x1, int y1,
                                       Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth()/7, y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect limitesColision = limitesColision(bounds1, bounds2);
            for (int i = limitesColision.left; i < limitesColision.right; i++) {
                for (int j = limitesColision.top; j < limitesColision.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static float[][] posicionesConColor(Bitmap bitmap1, int x1, int y1,
                                                        Bitmap bitmap2, int x2, int y2,int maxX, int maxY) {
        float[][] sueloPosiciones = new float[maxX][maxY];

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());
        if (Rect.intersects(bounds1, bounds2)) {
            Rect limitesColision = limitesColision(bounds1, bounds2);
            for (int i = limitesColision.left; i < limitesColision.right; i++) {
                for (int j = limitesColision.top; j < limitesColision.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);  //mapa
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2); //pixel
                    if (tieneRelleno(bitmap1Pixel) && tieneRelleno(bitmap2Pixel)) {
                        sueloPosiciones[i][j]=1;

                    }
                }
            }
        }
        return sueloPosiciones;
    }

    private static Rect limitesColision(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }
    private static boolean tieneRelleno(int pixel) {
        return pixel != Color.TRANSPARENT;
    }


}

