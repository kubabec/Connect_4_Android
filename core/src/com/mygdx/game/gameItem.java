package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class gameItem {
    public float x;
    public float y;
    public float targetX;
    public float targetY;
    public Color myColor;

    public gameItem(int k, float windowWidth, float windowHeight, float playAreaWidth, int separator, float ballX, float ballY, Color color){
        getCoords(k, windowWidth, windowHeight, playAreaWidth, separator);
        targetY = ballY;
        targetX = ballX;
        myColor = color;
    }



    private void getCoords(int k, float windowWidth, float windowHeight, float playAreaX, int separator){
        k = k+ 1; // Kolumna, do ktorej wrzucamy pilke
        x = playAreaX + k*(float)separator - separator/2; // Ustaw wspolrzedna x pilki na podstawie kolumny
        y = windowHeight -  windowHeight/6; // ustaw wspolrzedna y
    }

    public boolean fallDown(){

        if(y >targetY ){ // Jesli obecne 'y' pilki jest > docelowe 'y' , to ..
            if(y - 20 < targetY){
                y = targetY;
            }else {
                y -= 20; // spadaj w dol
            }
            return false; // zwroc falsz (ta pilka jeszcze nie doleciala do konca)
        }else { // Jesli y >= docelowa wspolrzedna y, oznacza to ze pilka doleciala na wskazane miejsce
            return true; // zwroc prawde
        }

    }


}
