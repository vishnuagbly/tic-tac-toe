package com.example.tictactoe;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tictactoe.logic.Logic;

import org.w3c.dom.Text;

import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private float mScreenDensity;
    private Logic mLogic;
    public View.OnClickListener onclick;
    private int compTurn;
    private boolean specialCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLogic = new Logic();

        setContentView(R.layout.activity_main);

        onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogic.printBoard();
                LinearLayout elem = (LinearLayout) v;
                LinearLayout img = new LinearLayout(MainActivity.this);
                img.setBackgroundResource(R.drawable.ic_tic_tac_o);
                v.setOnClickListener(null);
                elem.addView(img);
                GridLayout parent = (GridLayout) v.getParent();
                int x = parent.indexOfChild(v) / parent.getColumnCount();
                int y = parent.indexOfChild(v) % parent.getColumnCount();
                mLogic.setValue(x, y, false);
                Vector<Double> result = mLogic.check(false);
                Log.d("result[0]", String.valueOf(result.get(0)) + result.get(1) + result.get(2) + result.get(3));
                if(result.get(0) == -99.0) setCompTurn();
                else showWinner(result);
                mLogic.printBoard();
                Switch comments_switch = findViewById(R.id.comments_switch);
                TextView comments = findViewById(R.id.comment_view);
                if(comments_switch.isChecked()){
                    int[] list_id = new int[]{R.string.comment_1, R.string.comment_2, R.string.comment_3, R.string.comment_4, R.string.comment_5};
                    Random rand = new Random();
                    comments.setText(list_id[rand.nextInt(5)]);
                }
                else
                    comments.setText("");
                Log.d("Onclick", "here " + ((GridLayout) v.getParent()).indexOfChild(v));
            }
        };

        mScreenDensity = this.getResources().getDisplayMetrics().density;
        Log.d("Density: ", String.valueOf(mScreenDensity));

        GridLayout container = (GridLayout) findViewById(R.id.container);
        Log.d("container", String.valueOf(container.getColumnCount()));

        for (int i = 0; i < 3; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1.0f);
            for (int j = 0; j < 3; j++) {
                GridLayout.Spec columnSpec = GridLayout.spec(j, 1.0f);
                LinearLayout elem = new LinearLayout(this);
                elem.setOrientation(LinearLayout.HORIZONTAL);
                elem.setBackgroundColor(Color.parseColor("#303030"));
                int paddingDpinPx = (int) (10 * mScreenDensity);
                elem.setPadding(paddingDpinPx, paddingDpinPx, paddingDpinPx, paddingDpinPx);
                GridLayout.LayoutParams elemParam = new GridLayout.LayoutParams();
                elemParam.rowSpec = rowSpec;
                elemParam.columnSpec = columnSpec;
                int marginDpinPx = (int) (10 * mScreenDensity);
                elemParam.setMargins(marginDpinPx, marginDpinPx, marginDpinPx, marginDpinPx);
                elemParam.width = 0;
                elemParam.height = 0;
                elem.setOnClickListener(onclick);
                container.addView(elem, elemParam);
            }
        }


        Initialize();

        Switch border = this.findViewById(R.id.border_switch);
        border.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GridLayout elem = findViewById(R.id.container);
                if (isChecked) elem.setBackgroundResource(R.drawable.ic_background);
                else elem.setBackground(null);
            }
        });


    }

    public void Initialize(){
        mLogic.Initialize();

        GridLayout elem = findViewById(R.id.container);
        for(int i = 0 ; i < elem.getChildCount(); i++){
            ViewGroup v = (ViewGroup) elem.getChildAt(i);
            v.setBackgroundColor(Color.parseColor("#303030"));
            v.removeAllViews();
            v.setOnClickListener(onclick);
        }

        Switch firstMove = findViewById(R.id.FirstMove_switch);
        if(firstMove.isChecked()){
            specialCondition = true;
            setCompTurn();
        }

        TextView winner = findViewById(R.id.winner_view);
        winner.setText("");

        TextView comments = findViewById(R.id.comment_view);
        comments.setText("");
    }

    public void setCompTurn(){
        Vector<Double> result = new Vector<>();
        result.setSize(6);
        mLogic.printBoard();
        if(specialCondition){
            Random rand = new Random();
            int checker = rand.nextInt(22);
            Log.d("Checker", String.valueOf(checker));
            int location;
            if(checker < 9){
                int[] allLocations = new int[]{0, 2, 6, 8};
                location = rand.nextInt(4);
                location = allLocations[location];
            }
            else if(checker < 17) location = 4;
            else {
                location = rand.nextInt(4);
                location = (location * 2 + 1);
            }
            result.set(5, (double) location);
        }
        else{
            Log.d("AI", "here");
            result = mLogic.AI(true);
        }
        specialCondition = false;
        int cx = result.get(5).intValue() / 3;
        int cy = result.get(5).intValue() % 3;
        Log.d("X's turn", cx + " " + cy);
        mLogic.setValue(cx, cy, true);
        mLogic.printBoard();
        compTurn = result.get(5).intValue();
        Handler handler = new Handler();
        GridLayout parent = findViewById(R.id.container);
        for(int i = 0; i < parent.getChildCount(); i++){
            LinearLayout elem = (LinearLayout) parent.getChildAt(i);
            elem.setOnClickListener(null);
        }
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                GridLayout parent = findViewById(R.id.container);
                LinearLayout v1 = (LinearLayout) parent.getChildAt(compTurn);
                LinearLayout img1 = new LinearLayout(MainActivity.this);
                img1.setBackgroundResource(R.drawable.ic_tic_tac_x);
                v1.setOnClickListener(null);
                v1.addView(img1);
                Vector<Double> result = mLogic.check(true);
                for(int i = 0; i < parent.getChildCount(); i++){
                    LinearLayout elem = (LinearLayout) parent.getChildAt(i);
                    if(elem.getChildCount() == 0)
                        elem.setOnClickListener(onclick);
                }
                if(result.get(0) != -99.0)
                    showWinner(result);
            }
        }, 700);
    }

    private void showWinner(Vector<Double> result){
        String[] winner = new String[]{"YOU WIN", "TIE", "YOU LOSE"};
        GridLayout parent = findViewById(R.id.container);
        if(result.get(0) != 0){
            for(int i = 1; i <= 3; i++){
                Log.d("sheet", "sheet");
                LinearLayout child = (LinearLayout) parent.getChildAt(result.get(i).intValue());
                child.setBackgroundColor(Color.parseColor("#000000"));
            }
        }
        TextView winner_textView = findViewById(R.id.winner_view);
        winner_textView.setText(winner[result.get(0).intValue() + 1]);
        for(int i = 0; i < parent.getChildCount(); i++){
            LinearLayout elem = (LinearLayout) parent.getChildAt(i);
            elem.setOnClickListener(null);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "yes");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "yes");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", "yes");
    }

    public void reset(View view) {
        Initialize();
    }
}
