package jeopardy_data;

import game_logic.GameData;

/**
 * Created by Suveena on 11/7/16.
 */
public class ResetTurn implements JeopardyData {

    public int X;
    public int Y;

    public ResetTurn (int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    @Override
    public String getData() {
        return "Reset";
    }
}
