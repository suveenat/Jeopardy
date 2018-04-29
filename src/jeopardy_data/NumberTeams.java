package jeopardy_data;

/**
 * Created by Suveena on 11/8/16.
 */
public class NumberTeams implements JeopardyData {
    public static final long serialVersionUID = 1;

    public int numTeams;

    public NumberTeams(int numTeams) {
        this.numTeams = numTeams;
    }

    public Integer getData() {
        return this.numTeams;
    }
}
