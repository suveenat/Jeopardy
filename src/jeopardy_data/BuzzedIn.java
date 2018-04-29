package jeopardy_data;

/**
 * Created by Suveena on 11/8/16.
 */
public class BuzzedIn implements JeopardyData {
    public static final long serialVersionUID = 1;

    public int X;
    public int Y;
    public String name;

    public BuzzedIn(int X, int Y, String name) {
        this.X = X;
        this.Y = Y;
        this.name = name;
    }

    public String getData() {
        return this.name;
    }
}
