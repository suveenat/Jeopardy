package jeopardy_data;

import game_logic.GameData;

import javax.swing.*;

/**
 * Created by Suveena on 11/7/16.
 */
public class DisableQuestion implements JeopardyData {

    public int X;
    public int Y;

    public DisableQuestion (int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    @Override
    public String getData() {
        return "Disabling";
    }
}
