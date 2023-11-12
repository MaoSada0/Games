package com.example.games;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class MineSweeper extends AppCompatActivity {

    Button[][] cells;

    static int WIDTH = 9;
    static int HEIGHT = 9;
    static int numMines = 10;
    int ct = 0;
    int numFlugBefore10 = 0;

    boolean isFirst = true;
    static boolean isEnd = false;

    static int[][] mp = GetRandomMap();
    static boolean[][] wasClicked = new boolean[HEIGHT][WIDTH];
    static boolean[][] wasDoubleClicked = new boolean[HEIGHT][WIDTH];
    static boolean [][] wasFlag = new boolean[HEIGHT][WIDTH];

    public static String flagship = "\uD83D\uDEA9";
    public static String bombIcon = "\uD83D\uDCA3";
    public static String bombExplodedIcon = "\uD83D\uDCA5";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fillInWasClicked(false);
        fillInWasDoubleClicked(false);
        fillInWasFlag(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenmines);

        Button back = (android.widget.Button) findViewById(R.id.backFromMines);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineSweeper.this, fisrtScreen.class);
                startActivity(intent);
            }
        });

        TextView flagsCount = (TextView) findViewById(R.id.flags);
        TextView endTv = (TextView) findViewById(R.id.end);

        flagsCount.setText(flagship + ": " + (10 - numFlugBefore10));

        Button reBtn = (Button) findViewById(R.id.reBtn);
        reBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillInWasClicked(false);
                fillInWasDoubleClicked(false);
                fillInWasFlag(false);

                numFlugBefore10 = 0;

                flagsCount.setText(flagship + ": " + (10 - numFlugBefore10));

                endTv.setText("");

                isFirst = true;
                isEnd = false;
                generate();
            }
        });

        generate();
    }

    public void generate(){
        TextView endTv = (TextView) findViewById(R.id.end);
        TextView flagsCount = (TextView) findViewById(R.id.flags);
        mp = GetRandomMap();

        cells = new Button[HEIGHT][WIDTH];

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GridLayout cellsLayout = (GridLayout) findViewById(R.id.Grid);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(WIDTH);

        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[i].length; j++){
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
            }
        }

        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[i].length; j++){
                int finalI = i;
                int finalJ = j;

                cells[i][j].setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view) {

                        if(!wasClicked[finalI][finalJ] && !isEnd){

                            if(isFirst){
                                while (mp[finalI][finalJ] != 0){
                                    mp = GetRandomMap();
                                }
                                printMap(mp);
                                isFirst = false;
                            }

                            ((Button) view).setText(String.valueOf(mp[finalI][finalJ]));
                            wasClicked[finalI][finalJ] = true;

                            if(mp[finalI][finalJ] == 0){
                                openNums(mp, finalI, finalJ, cells);
                            }

                            if(mp[finalI][finalJ] == -1){
                                isEnd = true;
                                showBombs(cells, mp);
                                ((Button) view).setText(bombExplodedIcon);
                                //printTest(wasClicked);
                                endTv.setText("You lose!");
                            }

                            if(isWon(cells, mp)){
                                isEnd = true;
                                endTv.setText("You won!");
                            }

                        }

                        /////////////////////////////////////////////
                        if(wasClicked[finalI][finalJ] && !isEnd && !isFirst) { // && !wasDoubleClicked[finalI][finalJ]

                            ct++;
                            Handler handler = new Handler();
                            Runnable runn = new Runnable() {
                                @Override
                                public void run() {
                                    ct = 0;
                                }
                            };

                            if(ct == 1){
                                handler.postDelayed(runn, 400);
                            } else if (ct == 2 && !wasFlag[finalI][finalJ]) {
                                wasDoubleClicked[finalI][finalJ] = true;
                                openNear(mp, finalI, finalJ, cells);

                                if(isWon(cells, mp)){
                                    isEnd = true;
                                    endTv.setText("You won!");
                                }

                                ct = 0;
                            }
                        }
                    }
                });

                cells[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onLongClick(View v) {
                        if(!isEnd && !isFirst){
                            if(((Button) v).getText().equals(flagship)){
                                ((Button) v).setText("");
                                wasClicked[finalI][finalJ] = false;
                                wasFlag[finalI][finalJ] = false;
                                numFlugBefore10--;
                                flagsCount.setText(flagship + ": " + (10 - numFlugBefore10));
                            }
                            else {
                                if(!wasClicked[finalI][finalJ] && numFlugBefore10 < 10){
                                    ((Button) v).setText(flagship);
                                    numFlugBefore10++;
                                    wasClicked[finalI][finalJ] = true;
                                    wasFlag[finalI][finalJ] = true;
                                    flagsCount.setText(flagship + ": " + (10 - numFlugBefore10));
                                }
                            }
                            if(isWon(cells, mp)){
                                isEnd = true;
                                endTv.setText("You won!");
                            }
                        }
                        return true;
                    }
                });



                cellsLayout.addView(cells[i][j]);
            }
        }
    }

    public static boolean isWon(Button[][] cells, int[][] map){
        int k = 0;
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                if(cells[i][j].getText().equals(flagship) && map[i][j] == -1){
                    k++;
                }
            }
        }
        Log.d("k", String.valueOf(k) + " " + String.valueOf(isAll(wasClicked)));

        if(k == numMines && isAll(wasClicked)){
            return true;
        }
        return false;
    }

    public static boolean isAll(boolean[][] bl){
        int k = 0;
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                if (wasClicked[i][j]){
                    k++;
                }
            }
        }
        //Log.d("k1", String.valueOf(k));
        if(k == HEIGHT * WIDTH){
            return true;
        }
        return false;
    }

    public static void showBombs(Button[][] cells, int[][] map){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                if(map[i][j] == -1){
                    cells[i][j].setText(bombIcon);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void helpFunc(int[][] map, int tempY, int tempX, Button[][] cells){
        TextView endTv = (TextView) findViewById(R.id.end);
        if(tempY >= 0 && tempY < HEIGHT && tempX >= 0 && tempX < WIDTH && !isEnd){
            if(map[tempY][tempX] == -1 && !wasFlag[tempY][tempX]){
                isEnd = true;
                showBombs(cells, mp);
                cells[tempY][tempX].setText(bombExplodedIcon);
                endTv.setText("You lose!");
            } else if (map[tempY][tempX] != -2 && map[tempY][tempX] != -1 && !wasFlag[tempY][tempX]) {
                wasClicked[tempY][tempX] = true;
                if(map[tempY][tempX] == 0){
                    openNums(map, tempY, tempX, cells);
                } else {
                    cells[tempY][tempX].setText(String.valueOf(map[tempY][tempX]));
                }
            }
        }
    }

    public void openNear(int[][] map, int y, int x, Button[][] cells){
        helpFunc(map, y - 1, x, cells);
        helpFunc(map, y + 1, x, cells);
        helpFunc(map, y, x - 1, cells);
        helpFunc(map, y, x + 1, cells);
        helpFunc(map, y - 1, x - 1, cells);
        helpFunc(map, y + 1, x - 1, cells);
        helpFunc(map, y + 1, x + 1, cells);
        helpFunc(map, y - 1, x + 1, cells);
    }

    public void openNums(int[][] map, int y, int x, Button[][] cells){
        if(y < HEIGHT  && y >= 0 && x >= 0 && x < WIDTH && map[y][x] == 0){
            cells[y][x].setText("");
            cells[y][x].setBackgroundResource(R.drawable.btn_bg);

            wasClicked[y][x] = true;
            wasDoubleClicked[y][x] = true;

            map[y][x] = -2;
            if(y - 1 >= 0){
                openNums(map, y - 1, x, cells);
            }
            if(y + 1 < HEIGHT){
                openNums(map, y + 1, x, cells);
            }
            if(x - 1 >= 0){
                openNums(map, y, x - 1, cells);
            }
            if(x + 1 < WIDTH){
                openNums(map, y, x + 1, cells);
            }

            if(y - 1 > 0 && x - 1 > 0){
                openNums(map, y - 1, x - 1, cells);
            }
            if(y + 1 < HEIGHT && x - 1 > 0){
                openNums(map, y + 1, x - 1, cells);
            }
            if(y + 1 < HEIGHT && x + 1 < WIDTH){
                openNums(map, y + 1, x + 1, cells);
            }
            if(y - 1 > 0 && x + 1 < WIDTH){
                openNums(map, y - 1, x + 1, cells);
            }

        } else if (y < HEIGHT  && y >= 0 && x >= 0 && x < WIDTH && map[y][x] != 0) {
            if(map[y][x] != -2){
                wasClicked[y][x] = true;
                cells[y][x].setText(String.valueOf(map[y][x]));
                map[y][x] = -2;
            }
        }
    }

    public static void printTest(boolean[][] arr){
        String[][] ans = new String[arr.length][arr.length];
        StringBuilder temp = new StringBuilder();
        Log.d("test", " ");
        for (boolean[] booleans : arr) {
            temp = new StringBuilder();
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    temp.append("| 1");
                } else {
                    temp.append("| 0");
                }
            }
            Log.d("test", temp.toString());
        }
    }

    public static void printIssAllClicked(boolean[][] map){
        String[][] ans = new String[map.length][map.length];
        StringBuilder temp = new StringBuilder();
        Log.d("map", " ");
        for(int i = 0; i < map.length; i++){
            temp = new StringBuilder();
            for(int j = 0; j < map[i].length; j++){
                if(!map[i][j]){
                    ans[i][j] = "|0";
                }
                else {
                    ans[i][j] = "|1";
                }
                temp.append(ans[i][j]);
            }
            Log.d("mapIsAll", temp.toString());
        }
    }

    public static void printMap(int[][] map){
        String[][] ans = new String[map.length][map.length];
        StringBuilder temp = new StringBuilder();
        Log.d("map", " ");
        for(int i = 0; i < map.length; i++){
            temp = new StringBuilder();
            for(int j = 0; j < map[i].length; j++){
                if(map[i][j] == -1){
                    ans[i][j] = "B | ";
                }
                else {
                    ans[i][j] = String.valueOf(map[i][j] + " | ");
                }
                temp.append(ans[i][j]);
            }
            Log.d("map", temp.toString());
        }

    }



    public static int[][] GetRandomMap(){
        int[][] map = new int[HEIGHT][WIDTH];

        int nMs = numMines;
        HashSet<Integer> randNums = new HashSet<>();

        while(nMs > 0){
            int randomNum = 0;
            randomNum = ThreadLocalRandom.current().nextInt(0, 80);
            if(randNums.contains(randomNum)){
                continue;
            }
            else {
                randNums.add(randomNum);
                nMs--;
            }
        }

        int k = 0;

        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map.length; j++){
                if(randNums.contains(k)){
                    map[i][j] = -1;
                }
                else {
                    map[i][j] = 0;
                }
                k++;
            }
        }

        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map.length; j++){
                if(map[i][j] != -1){
                    map[i][j] = whatNum(map, i, j);
                }
            }
        }

        return map;
    }

    public static int whatNum(int[][] map, int y, int x){
        int numMines = 0;

        if(y - 1 >= 0 && y - 1 < HEIGHT && x >= 0 && x < WIDTH){
            if(map[y - 1][x] == -1){
                numMines++;
            }
        }
        if(y + 1 < HEIGHT && y + 1 >= 0 && x >= 0 && x < WIDTH){
            if(map[y + 1][x] == -1){
                numMines++;
            }
        }
        if(x - 1 >= 0 && x - 1 < WIDTH && y >= 0 && y < HEIGHT){
            if(map[y][x - 1] == -1){
                numMines++;
            }
        }
        if(x + 1 < WIDTH && x + 1 >= 0 && y >= 0 && y < HEIGHT){
            if(map[y][x + 1] == -1){
                numMines++;
            }
        }

        if(y - 1 >= 0 && y - 1 < HEIGHT && x - 1 >= 0 && x - 1 < WIDTH){
            if(map[y - 1][x - 1] == -1){
                numMines++;
            }
        }
        if(y + 1 < HEIGHT && y + 1 >= 0 && x - 1 >= 0 && x - 1 < WIDTH){
            if(map[y + 1][x - 1] == -1){
                numMines++;
            }
        }
        if(y + 1 < HEIGHT && y + 1 >= 0 && x + 1 >= 0 && x + 1 < WIDTH){
            if(map[y + 1][x + 1] == -1){
                numMines++;
            }
        }
        if(y - 1 < HEIGHT && y - 1 >= 0 && x + 1 >= 0 && x + 1 < WIDTH){
            if(map[y - 1][x + 1] == -1){
                numMines++;
            }
        }


        return numMines;

    }

    public static void fillInWasClicked(boolean bl){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                wasClicked[i][j] = bl;
            }
        }
    }
    public static void fillInWasDoubleClicked(boolean bl){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                wasDoubleClicked[i][j] = bl;
            }
        }
    }
    public static void fillInWasFlag(boolean bl){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                wasFlag[i][j] = bl;
            }
        }
    }

}