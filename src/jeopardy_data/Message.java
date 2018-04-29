package jeopardy_data;

import java.io.Serializable;

/**
 * Created by Suveena on 11/4/16.
 */
public class Message implements JeopardyData {

    public static final long serialVersionUID = 1;

    static String message;

    public Message(String message) {
        this.message = message;
    }

    public String getData() {
        return this.message;
    }
}
