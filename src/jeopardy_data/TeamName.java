package jeopardy_data;

/**
 * Created by Suveena on 11/5/16.
 */
public class TeamName implements JeopardyData {

    String teamName = "";

    public TeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String getData() {
        return this.teamName;
    }

}
