package jeopardy_data;

import game_logic.GameData;
import other_gui.QuestionGUIElement;

/**
 * Created by Suveena on 11/7/16.
 */
public class FJSignal implements JeopardyData {

    public int X;
    public int Y;

    public FJSignal (int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    @Override
    public String getData() {
        return "FJ";
    }
}
