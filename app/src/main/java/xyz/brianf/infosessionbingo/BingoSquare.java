package xyz.brianf.infosessionbingo;

/**
 * Created by brian on 09/05/16.
 */
public class BingoSquare {
    private boolean checked = false;


    private String message;

    public BingoSquare(String message){
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
