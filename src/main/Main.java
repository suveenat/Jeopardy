package main;

import frames.LoginGUI;
import frames.StartWindowGUI;
import game_logic.UserTable;
import game_logic.UserTableInterface;
import game_logic.User;
import other_gui.NetworkFJG;

import java.sql.*;

public class Main {

	public static void main(String[] args) {
		UserTable userTable = new UserTable();
		userTable.createUserTable();
		new LoginGUI(userTable).setVisible(true);
	}

}
