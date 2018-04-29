package jeopardy_data;
import jeopardy_server.*;

/**
 * Created by Suveena on 11/9/16.
 */
public class NewFileSignal implements JeopardyData{

    ServerThread st;

    public NewFileSignal() {

    }

    public String getData() {
        return "New File";
    }

    public void chosingNewFile(ServerThread st) {
        this.st = st;
        st.choseNewFile = true;
    }
}