package jeopardy_data;
import jeopardy_server.*;

/**
 * Created by Suveena on 11/9/16.
 */
public class ExitSignal implements JeopardyData{

    ServerThread st;

    public ExitSignal() {

    }

    public String getData() {
        return "New File";
    }

    public void exiting(ServerThread st) {
        this.st = st;
        st.exited = true;
    }
}