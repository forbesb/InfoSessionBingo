package xyz.brianf.infosessionbingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;

/**
 * Created by brian on 11/05/16.
 */
public class BingoActivity extends AppCompatActivity{
    //private static BingoBoard yourBoard; TODO: Save board between activity creations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int size = intent.getIntExtra(MainActivity.BINGO_NUMBER_MESSAGE, 5);

        GridLayout grid = new GridLayout(this);
        grid.setRowCount(size);
        grid.setColumnCount(size);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        //params.width = intent.getIntExtra(MainActivity.WIDTH_EXTRA, 0)/(size != 0 ? size : 1);
        //params.height = intent.getIntExtra(MainActivity.HEIGHT_EXTRA, 0)/(size != 0? size : 1);
        System.out.println(params.width);
        //System.out.println(params.height);

        BingoBoard board = new BingoBoard(size);

        for (BingoSquare[] i: board.getSquares()){
            for (BingoSquare sq: i){
                Button b = new Button(this);
                b.setText(sq.getMessage());
                grid.addView(b);
            }
        }

        setContentView(grid, params);



    }
}
