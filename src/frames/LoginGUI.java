package frames;

// Database
import java.sql.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import game_logic.User;
import game_logic.UserTable;
import listeners.TextFieldFocusListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;

public class LoginGUI extends JFrame{
	
	private JButton loginButton;
	private JButton createAccount;
	private JTextField username;
	private JTextField password;
	private JLabel alertLabel;
	private UserTable userTable;
	public String message;
	//users map
	//could have use <String, String> instead of User object, but chose not to
	//private HashMap<String, User> existingUsers;
	//the file that contains user account info
	private File file;

	public LoginGUI(UserTable userTable) {
		//file = new File("users.txt");
		//existingUsers = new HashMap<>();
		//reads in stored users from file and populates existingUsers
		//readFromFile();
		this.userTable = userTable;
		initializeComponents();
		createGUI();
		addListeners();
	}

	private void initializeComponents(){
		
		loginButton = new JButton("Login");
		createAccount = new JButton("Create Account");
		username = new JTextField("username");
		password = new JTextField("password");
		alertLabel = new JLabel();
	}
	
	private void createGUI(){
		
		JPanel mainPanel = new JPanel();
		JPanel textFieldOnePanel = new JPanel();
		JPanel textFieldTwoPanel = new JPanel();
		JLabel welcome = new JLabel("login or create an account to play", JLabel.CENTER);
		JLabel jeopardyLabel = new JLabel("Jeopardy!", JLabel.CENTER);
		JPanel alertPanel = new JPanel();
		JPanel textFieldsPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JPanel welcomePanel = new JPanel(new GridLayout(2,1));
		
		//set mass component appearances
		AppearanceSettings.setForeground(Color.lightGray, createAccount, loginButton, password, username);
		AppearanceSettings.setSize(300, 60, password, username);
		
		AppearanceSettings.setSize(200, 80, loginButton, createAccount);
		AppearanceSettings.setBackground(Color.darkGray, loginButton, createAccount);
		
		AppearanceSettings.setOpaque(loginButton, createAccount);
		AppearanceSettings.unSetBorderOnButtons(loginButton, createAccount);
		
		AppearanceSettings.setTextAlignment(welcome, alertLabel, jeopardyLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, password, alertLabel, username, loginButton, createAccount);
		
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, mainPanel, welcome, alertLabel, jeopardyLabel, alertPanel, textFieldsPanel, 
				buttonsPanel, welcomePanel, textFieldOnePanel, textFieldTwoPanel);
		
		//other appearance settings
		welcome.setFont(AppearanceConstants.fontMedium);
		jeopardyLabel.setFont(AppearanceConstants.fontLarge);
		
		loginButton.setEnabled(false);
		loginButton.setBackground(AppearanceConstants.mediumGray);
		createAccount.setBackground(AppearanceConstants.mediumGray);
		createAccount.setEnabled(false);
		
		//add components to containers
		welcomePanel.add(welcome);
		welcomePanel.add(jeopardyLabel);
		
		alertPanel.add(alertLabel);
		textFieldOnePanel.add(username);
		textFieldTwoPanel.add(password);
		
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		//adds components to the panel with glue in between each
		AppearanceSettings.addGlue(buttonsPanel, BoxLayout.LINE_AXIS, true, loginButton, createAccount);
		
		AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, welcomePanel);
		//don't want glue in between the following two panels, so they are not passed in to addGlue
		mainPanel.add(alertPanel);
		mainPanel.add(textFieldOnePanel);
		AppearanceSettings.addGlue(mainPanel, BoxLayout.PAGE_AXIS, false, textFieldTwoPanel);
		mainPanel.add(buttonsPanel);
		
		add(mainPanel, BorderLayout.CENTER);
		setSize(600, 600);
	}
	
	//returns whether the buttons should be enabled
	private boolean canPressButtons(){
		return (!username.getText().isEmpty() && !username.getText().equals("username") && 
				!password.getText().equals("password") && !password.getText().isEmpty());
	}
	//reads in users map from the file
//	private void readFromFile(){
//
//		FileInputStream fileStream = null;
//		ObjectInputStream inputStream = null;
//
//		try {
//			//if the file exists in my directory, then read users from it
//			if (file.exists()){
//				fileStream = new FileInputStream(file);
//				inputStream = new ObjectInputStream(fileStream);
//
//				try {
//					existingUsers = (HashMap<String, User>) inputStream.readObject();
//				}
//				catch (ClassNotFoundException e) {}
//			}
//		}
//		catch (IOException e) {}
//		finally{
//
//			try {
//				if (fileStream != null) fileStream.close();
//				if (inputStream != null) inputStream.close();
//			}
//			catch (IOException e) {}
//		}
//	}
	
	//writes the hashmap to the file
//	private User writeToFile(String user, String pass){
//		User userObj = new User(user, pass);
//		existingUsers.put(user, userObj);
//
//		FileOutputStream fileStream = null;
//		ObjectOutputStream outputStream = null;
//
//		try {
//			fileStream = new FileOutputStream(file);
//			outputStream = new ObjectOutputStream(fileStream);
//			outputStream.writeObject(existingUsers);
//			//flush!
//			outputStream.flush();
//		}
//		catch (IOException e) {}
//		finally{
//
//			try {
//				if (fileStream != null)fileStream.close();
//				if (outputStream != null) outputStream.close();
//			}
//			catch (IOException e) {}
//		}
//
//		return userObj;
//	}
	
	private void addListeners(){
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//focus listeners
		username.addFocusListener(new TextFieldFocusListener("username", username));
		password.addFocusListener(new TextFieldFocusListener("password", password));
		//document listeners
		username.getDocument().addDocumentListener(new MyDocumentListener());
		password.getDocument().addDocumentListener(new MyDocumentListener());
		//action listeners

		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameString = username.getText();
				String passwordString = password.getText();
				User user = new User(usernameString, passwordString);
				userTable.check(usernameString, passwordString);
				if (userTable.check(usernameString, passwordString) == true) {
					alertLabel.setText("Logged in");
					new StartWindowGUI(user, userTable).setVisible(true);
					dispose();
				} else {
					alertLabel.setText("Invalid login");
				}
			}
			
		});
		
		createAccount.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameString = username.getText();
				String passwordString = password.getText();
				User user = new User(usernameString, passwordString);
				message = userTable.insert(usernameString, passwordString);
				alertLabel.setText(message);
				if (message.equals("Account created")) {
					new StartWindowGUI(user, userTable).setVisible(true);
					dispose();
				}

			}
			
		});
	}

	//sets the buttons enabled or disabled
	private class MyDocumentListener implements DocumentListener{
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			createAccount.setEnabled(canPressButtons());
			loginButton.setEnabled(canPressButtons());
		}
	}
}
