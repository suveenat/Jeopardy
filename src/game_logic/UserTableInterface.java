package game_logic;

import java.util.*;
import java.sql.*;

/**
 * Created by Suveena on 10/31/16.
 */
public interface UserTableInterface {
    void createUserTable();
    String insert(String username, String password);
    boolean check(String username, String password);
    void update(String username, String password);
}
