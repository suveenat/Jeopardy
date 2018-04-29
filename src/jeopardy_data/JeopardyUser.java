package jeopardy_data;

import game_logic.*;
/**
 * Created by Suveena on 11/4/16.
 */
public class JeopardyUser implements JeopardyData{

    User user;

    public JeopardyUser(User user) {
        this.user = user;
    }

    @Override
    public User getData() {
        return this.user;
    }
}
