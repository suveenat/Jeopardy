package jeopardy_data;

/**
 * Created by Suveena on 11/5/16.
 */
public class TurnChange implements JeopardyData {

    String teamName;

    public TurnChange(String teamName) {
        this.teamName = teamName;
    }

//    public TurnChange() {}

    @Override
    public String getData() {
        return this.teamName;
    }

}
