package jeopardy_data;

import game_logic.GameData;

/**
 * Created by Suveena on 11/4/16.
 */
public class JeopardyGameData implements JeopardyData{

    GameData gameData;

   public JeopardyGameData (GameData gameData){
        this.gameData = gameData;
    }

    @Override
    public GameData getData() {
        return this.gameData;
    }
}
