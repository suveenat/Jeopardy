package jeopardy_data;

import frames.NetworkMG;
import game_logic.GameData;
import other_gui.NetworkFJG;
import other_gui.QuestionGUIElement;
import other_gui.TeamGUIComponents;
/**
 * Created by Suveena on 11/7/16.
 */
public class DeductMessage implements JeopardyData {

    public int X;
    public int Y;
    public TeamGUIComponents currentTeam;
    public int points;
//    public NetworkMG nmg;
//    public GameData gd;

    public DeductMessage (int X, int Y, TeamGUIComponents currentTeam, int points/*, NetworkMG nmg, GameData gd*/){
        this.X = X;
        this.Y = Y;
        this.currentTeam = currentTeam;
        this.points = points;
//        this.nmg = nmg;
//        this.gd = gd;
    }

    @Override
    public String getData() {
        return "DeductMessage";
    }
}
