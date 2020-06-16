package com.example.tictactoe.logic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;

import java.util.Vector;

public class Logic {

    private int[][] board;
    private Activity main;
    private MainActivity mainActivity = new MainActivity();

    public Logic() {
        board = new int[3][3];
    }

    public void printBoard(){
        for(int i = 0; i < 3; i++)
            Log.d("Check", String.valueOf(board[i][0]) + board[i][1] + board[i][2]);
    }

    private int filled() {
        int result = 0;
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(board[i][j] != 0) result++;
        return result;
    }

    private boolean check_pair(boolean computer, int i, int j) {
        board[i][j] = computer ? 1 : 2;
        Vector<Double> result = check(computer);
        board[i][j] = 0;
        return result.get(0) == (computer ? 1.0 : -1.0);
    }

    public Vector<Double> check(boolean computer) {
        Vector<Double> result = new Vector<>();

//        printBoard();

        boolean comp_win0 = true, player_win0 = true;
        boolean comp_win4 = true, player_win4 = true;
        for(int i = 0; i < 3; i++){
            boolean comp_win = true, player_win = true;
            boolean comp_win1 = true, player_win1 = true;
            for(int j = 0; j < 3; j++){
                if(board[i][j] == 1) player_win = false;
                else comp_win = false;
                if(board[j][i] == 1)player_win1 = false;
                else comp_win1 = false;
                if(board[i][j] == 0) {
                    player_win = false;
                    comp_win = false;
                }
                if(board[j][i] == 0) {
                    player_win1 = false;
                    comp_win1 = false;
                }
            }
            if(comp_win || player_win){
                if(comp_win)
                    result.add(1.0);
                else
                    result.add(-1.0);
                for(int j = 0; j < 3; j++)
                    result.add((double) i*3 + j);
                return result;
            }
            if(comp_win1 || player_win1){
                if(comp_win1)
                    result.add(1.0);
                else
                    result.add(-1.0);
                for(int j = 0; j < 3; j++)
                    result.add((double) j*3 + i);
                return result;
            }
            if(board[i][i] == 1)player_win0 = false;
            else comp_win0 = false;
            if(board[i][i] == 0) {
                player_win0 = false;
                comp_win0 = false;
            }
            if(board[i][2 - i] == 1)player_win4 = false;
            else comp_win4 = false;
            if(board[i][2 - i] == 0) {
                player_win4 = false;
                comp_win4 = false;
            }
        }
        if(comp_win0 || player_win0){
            if(comp_win0)
                result.add(1.0);
            else
                result.add(-1.0);
            for(int j = 0; j < 3; j++)
                result.add((double) j*3 + j);
            return result;
        }
        if(comp_win4 || player_win4){
            if(comp_win4)
                result.add(1.0);
            else
                result.add(-1.0);
            for(int j = 0; j < 3; j++)
                result.add((double) j*3 + 2 - j);
            return result;
        }

        boolean empty_space = false;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == 0)
                    empty_space = true;
            }
        }
        if(!empty_space){
            result.add(0.0);
            result.add(0.0);
            result.add(0.0);
            result.add(0.0);
            return result;
        }

        result.add(-99.0);
        result.add(-99.0);
        result.add(-99.0);
        result.add(-99.0);
        return result;
    }

    public Vector<Double> AI(boolean computer) {
        Vector<Vector<Double>> result = new Vector<>();
        int max_index = -1, max_win = -2;
        double total_score = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    if(computer) board[i][j] = 1;
                    else board[i][j] = 2;
                    Vector<Double> temp;
                    temp = check(computer);
                    if(temp.get(0) == -99.0){
                        temp = AI(!computer);
                        temp.set(5, (double) i*3 + j);
                        if(temp.get(0) == 1.0 && computer && (max_win == -2 || result.get(max_win).get(0) == 0))
                            max_win = result.size();
                        else if(temp.get(0) == -1.0 && !computer && (max_win == -2 || result.get(max_win).get(0) == 0))
                            max_win = result.size();
                        board[i][j] = 0;
                        if(temp.get(0) == 0.0 && max_win == -2 && check_pair(!computer, i, j))
                            max_win = result.size();
                        board[i][j] = computer ? 1 : 2;
                    }
                    else {
                        if(temp.get(0) == 1.0 && computer)
                            max_win = result.size();
                        else if(temp.get(0) == -1.0 && !computer)
                            max_win = result.size();
                        else if(max_win == -2 && temp.get(0) == 0)
                            max_win = result.size();
                        temp.add(temp.get(0));
                        temp.add((double) i*3 + j);
                    }
                    if(max_index != -1){
                        if(temp.get(4) > result.get(max_index).get(4) & computer)
                            max_index = result.size();
                        else if(temp.get(4) < result.get(max_index).get(4) & !computer)
                            max_index = result.size();
                    }
                    else
                        max_index = result.size();
                    total_score += temp.get(4);
                    if(temp.get(5) == 4)
                        Log.d("here", "here");
                    result.add(temp);
                    board[i][j] = 0;
                }
            }

        if(max_win != -2){
            result.get(max_win).set(4, result.get(max_win).get(0));
            return result.get(max_win);
        }
        if(filled() == 0){
            for(int i = 0; i < result.size(); i++)
                Log.d("result " + filled() + ": ", String.valueOf(result.get(i).get(4)));
            printBoard();
        }
        result.get(max_index).set(4, total_score / result.size());
        return result.get(max_index);
    }

    public void setValue(int x, int y, boolean computer) {
        if (computer) board[x][y] = 1;
        else board[x][y] = 2;
    }

    public void Initialize() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = 0;
        printBoard();
    }

}
