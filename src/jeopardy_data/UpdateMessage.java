package jeopardy_data;

/**
 * Created by Suveena on 11/8/16.
 */
public class UpdateMessage implements JeopardyData{

    public static final long serialVersionUID = 1;

    static String message;

    public UpdateMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return this.message;
    }
}
