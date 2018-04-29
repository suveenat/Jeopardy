package jeopardy_data;

import game_logic.GameData;

/**
 * Created by Suveena on 11/7/16.
 */
public class AnsweredSignal implements JeopardyData {

    public int X;
    public int Y;

    public AnsweredSignal (int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    @Override
    public String getData() {
        return "Answered";
    }
}
