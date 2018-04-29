package jeopardy_data;
import jeopardy_server.*;

/**
 * Created by Suveena on 11/9/16.
 */
public class LogoutSignal implements JeopardyData{

    ServerThread st;

    public LogoutSignal() {

    }

    public String getData() {
        return "Logged Out";
    }

    public void loggingOut(ServerThread st) {
        this.st = st;
        st.loggedOut = true;
    }
}