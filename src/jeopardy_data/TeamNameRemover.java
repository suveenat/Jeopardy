package jeopardy_data;

/**
 * Created by Suveena on 11/5/16.
 */
public class TeamNameRemover implements JeopardyData {

    TeamName teamNameObject;

    public TeamNameRemover(TeamName teamNameObject) {
        this.teamNameObject = teamNameObject;
    }

    @Override
    public TeamName getData() {
        return this.teamNameObject;
    }

}
