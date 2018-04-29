package jeopardy_data;

import game_logic.UserTable;

/**
 * Created by Suveena on 11/4/16.
 */
public class JeopardyUserTable {

    UserTable userTable;

    public JeopardyUserTable (UserTable userTable) {
        this.userTable = userTable;
    }

    public UserTable getData() {
        return this.userTable;
    }
}
