package xyz.brianf.infosessionbingo;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by brian on 09/05/16.
 */
public class BingoOptions {
    public static ArrayList<String> options;
    private static void initOptionList(){
        //for now, just letters.
        options = new ArrayList<>();
        for (char i = 'a'; i <= 'z'; i++){
            options.add(String.valueOf(i));
        }

    }

    public static ArrayList<String> getRandomizedOtions(int n){
        if (options == null){
            initOptionList();
        }
        Collections.shuffle(options);
        ArrayList<String> RandomizedOptions = new ArrayList<>();
        for (int i = 0; i<n; i++){
            RandomizedOptions.add(options.get(i));
        }
        return RandomizedOptions;
    }

}
