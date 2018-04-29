package jeopardy_data;

/**
 * Created by Suveena on 11/8/16.
 */
public class AnnouncementLabel implements JeopardyData{

    public static final long serialVersionUID = 1;

    public int X;
    public int Y;
    public String message;

    public AnnouncementLabel(int X, int Y, String message) {
        this.X = X;
        this.Y = Y;
        this.message = message;
    }

    public String getData() {
        return this.message;
    }
}
