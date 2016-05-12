package xyz.brianf.infosessionbingo;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by brian on 09/05/16.
 */
public class BingoBoard {


    private BingoSquare[][] squares;

    public BingoBoard(int n){
        initSquares(n);
    }

    public void initSquares(int sideLength){
        squares = new BingoSquare[sideLength][sideLength];
        ArrayList<String> RandomizedOptions = BingoOptions.getRandomizedOtions(sideLength*sideLength);
        for (int i = 0; i < 5; i++){
            for (int y = 0; y<5; y++ ){
                BingoSquare newsquare = new BingoSquare(RandomizedOptions.get(y+sideLength*i));
                squares[i][y] = newsquare;
            }
        }
    }


    public BingoSquare[][] getSquares() {
        return squares;
    }

    public void setSquares(BingoSquare[][] squares) {
        this.squares = squares;
    }
}
