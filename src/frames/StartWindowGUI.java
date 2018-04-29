package frames;

import game_logic.GameData;
import game_logic.User;
import game_logic.UserTable;
import listeners.ExitWindowListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;
import jeopardy_client.*;
import jeopardy_server.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import jeopardy_data.*;

/**
 * Created by Suveena on 11/1/16.
 */
public class StartWindowGUI extends JFrame {

	private JPanel mainPanel;
	private JPanel currentPanel;
	private JFileChooser fileChooser;
	private JButton fileChooserButton;
	private java.util.List<JTextField> teamNameTextFields;
	private java.util.List<JLabel> teamNameLabels;
	private static final int MAX_NUMBER_OF_TEAMS = 4;
	public int numberOfTeams;
	public JButton startGameButton;
	public JButton clearDataButton;
	public JButton exitButton;
	private JSlider slider;
	private JLabel fileNameLabel;
	private JLabel networkLabel;
	public JButton logoutButton;
	public JCheckBox quickPlay;
	// Networking Panel Radio Buttons
	private JRadioButton SWGNotNetworked;
	public JRadioButton SWGHost;
	private JRadioButton SWGJoin;
	private Boolean haveNames;
	private Boolean haveValidFile;
	private GameData gameData;
	//logged in user
	public User loggedInUser;
	public UserTable userTable;
	private JLabel numberOfTeamsLabel;
	private JLabel chooseGameFileLabel;
	// Port/Host
	private JPanel hostAndPortPanel;
	private JTextField portField;
	private JTextField IPAddressField;
	// Connection
	boolean connected = false;
	// Bottom Panel
	private JPanel bottomPanel;
	public JLabel statusMessage;
	public JeopardyServer jeopardyServer;
	private JeopardyClient jeopardyClient;
	public TeamName teamNameObject;

	public StartWindowGUI(User user, UserTable userTable){

		super("Jeopardy Menu");
		loggedInUser = user;
		numberOfTeams = -1;
		haveNames = false;
		haveValidFile = false;
		this.userTable = userTable;
		initializeComponents();
		createGUI();
		addListeners();
	}

	//private methods
	private void initializeComponents(){
		mainPanel = new JPanel(new GridLayout(4, 1));
		currentPanel = mainPanel;
		fileChooser = new JFileChooser();
		teamNameTextFields = new ArrayList<>(4);
		teamNameLabels = new ArrayList<>(4);
		fileNameLabel = new JLabel("");
		networkLabel = new JLabel("Choose whether you are joining or hosting a game or playing a non-networked", SwingConstants.CENTER);
		logoutButton = new JButton("Logout");
		gameData = new GameData();
		//fileRating = new JLabel("");
		quickPlay = new JCheckBox("Quick Play?");
		// Initialize 3 RadioButtons
		SWGNotNetworked = new JRadioButton("Not Networked");
		SWGHost = new JRadioButton("Host Game");
		SWGJoin = new JRadioButton("Join Game");
		// Bottom Panel
		bottomPanel = new JPanel();
		statusMessage = new JLabel();
		for (int i = 0; i<MAX_NUMBER_OF_TEAMS; i++){
			teamNameTextFields.add(new JTextField());
			teamNameLabels.add(new JLabel("Please name Team "+(i+1)));
		}

		startGameButton = new JButton("Start Jeopardy");
		clearDataButton = new JButton("Clear Choices");
		exitButton = new JButton("Exit");
		fileChooserButton = new JButton("Choose File");
		slider = new JSlider();
		// New Components
		hostAndPortPanel = new JPanel();
		portField = new JTextField("1111");
		IPAddressField = new JTextField("localhost");

	}

	private void createGUI(){
		//setting appearance of member variable gui components
		//setting background colors
		AppearanceSettings.setBackground(Color.darkGray, exitButton, logoutButton, clearDataButton, startGameButton, slider,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton);

		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2),
				teamNameTextFields.get(3));

		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, fileNameLabel, mainPanel, bottomPanel);

		//setting fonts
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2), teamNameTextFields.get(3),
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3),
				fileChooserButton, fileNameLabel, exitButton, clearDataButton, logoutButton, startGameButton);

		//other
		AppearanceSettings.setForeground(Color.lightGray, exitButton, logoutButton, clearDataButton, startGameButton,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton,
				fileNameLabel, slider);

		AppearanceSettings.setOpaque(exitButton, clearDataButton, logoutButton, startGameButton, slider,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton);

		AppearanceSettings.setSize(180, 70, exitButton, clearDataButton, startGameButton, logoutButton);
		AppearanceSettings.setSize(150, 80,
				teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2), teamNameTextFields.get(3));

		AppearanceSettings.setSize(250, 100, teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3));

		AppearanceSettings.unSetBorderOnButtons(exitButton, logoutButton, clearDataButton, startGameButton, fileChooserButton);

		AppearanceSettings.setTextAlignment(teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3),
				fileNameLabel);

		setAllInvisible(teamNameTextFields, teamNameLabels);

		// Networking Label
		networkLabel.setFont(AppearanceConstants.fontSmallest);

		// Radio Buttons & Panel
		SWGNotNetworked.setSelected(true);
		SWGNotNetworked.setFont(AppearanceConstants.fontSmallest);
		SWGNotNetworked.setPreferredSize(new Dimension(200, 30));

		SWGHost.setFont(AppearanceConstants.fontSmallest);
		SWGHost.setPreferredSize(new Dimension(200, 30));

		SWGJoin.setFont(AppearanceConstants.fontSmallest);
		SWGJoin.setPreferredSize(new Dimension(200, 30));

		//check box settings
		quickPlay.setFont(AppearanceConstants.fontSmallest);
		quickPlay.setHorizontalTextPosition(SwingConstants.LEFT);
		quickPlay.setPreferredSize(new Dimension(200, 30));

		//file chooser settings
		fileChooser.setPreferredSize(new Dimension(400, 500));
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));

		//slider settings
		AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
		slider.setSnapToTicks(true);
		slider.setPreferredSize(new Dimension(500, 50));
		startGameButton.setEnabled(false);

		// Status message
		statusMessage.setFont(AppearanceConstants.fontSmallest);

		createMainPanel();

		setBackground(AppearanceConstants.darkBlue);
		add(mainPanel, BorderLayout.CENTER);
		setSize(800, 825);
	}

	//sets the label and textField visible again
	private void setVisible(JLabel label, JTextField textField){
		//the first text field is always shown so we can use their border
		Border border = teamNameTextFields.get(0).getBorder();

		textField.setBackground(AppearanceConstants.lightBlue);
		textField.setForeground(Color.black);
		textField.setBorder(border);

		label.setBackground(Color.darkGray);
		label.setForeground(Color.lightGray);
	}

	public JeopardyClient getJC() {
		return this.jeopardyClient;
	}

	public JeopardyServer getJS() {
		return this.jeopardyServer;
	}

	//I wanted to user BoxLayout for resizability but if you simply set a components invisble with
	// setVisible(false), it changes the size of the components that are visible. This is my way aroung that
	private void setInvisible(JLabel label, JTextField textField){
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, textField, label);
		AppearanceSettings.setForeground(AppearanceConstants.darkBlue, textField, label);
		textField.setBorder(AppearanceConstants.blueLineBorder);
	}
	//used in the constructor to set everything invisible (except the first label and text field)
	private void setAllInvisible(java.util.List<JTextField> teamNameTextFields, java.util.List<JLabel> teamNameLabels){

		for (int i = 1; i<4; i++) setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
	}

	private void createMainPanel(){
		//initialize local panels
		JPanel teamNamesPanel = new JPanel();
		JPanel teamLabelsPanel1 = new JPanel();
		JPanel teamLabelsPanel2 = new JPanel();
		JPanel teamTextFieldsPanel1 = new JPanel();
		JPanel teamTextFieldsPanel2 = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel teamsAndFilePanel = new JPanel();
		JPanel numberOfTeamsPanel = new JPanel();
		JPanel fileChooserPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel welcomePanel = new JPanel(new BorderLayout());
		// Networking Radio Button Panel
		JPanel networkWrapper = new JPanel();
		JPanel labelWrapper = new JPanel();
		JPanel networkPanel = new JPanel(); // Not flow
		JPanel titlePanel = new JPanel(new BorderLayout());
		//initialize labels
		JLabel welcomeLabel = new JLabel("Welcome to Jeopardy!");
		//JLabel explainContentLabel = new JLabel("Choose the game file, number of teams, and team names before starting the game.");
		numberOfTeamsLabel = new JLabel("Please choose the number of teams that will be playing on the slider below.");
		chooseGameFileLabel = new JLabel("Please choose a game file.");

		//set appearances on local variables
		//AppearanceSettings.setBackground(AppearanceConstants.mediumGray, networkLabel);
		//AppearanceSettings.setBackground(AppearanceConstants.darkBlue, networkPanel);
		//AppearanceSettings.setBackground(Color.magenta, networkWrapper);
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, welcomeLabel, labelWrapper, networkLabel, welcomePanel, titlePanel, networkPanel, networkWrapper);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, chooseGameFileLabel, numberOfTeamsLabel, portField, IPAddressField);
		AppearanceSettings.setTextAlignment(welcomeLabel, chooseGameFileLabel, numberOfTeamsLabel);

		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, chooseGameFileLabel, numberOfTeamsLabel, numberOfTeamsPanel, fileChooserPanel, teamsAndFilePanel,
				buttonPanel, teamNamesPanel, teamLabelsPanel1, teamLabelsPanel2, teamTextFieldsPanel1, teamTextFieldsPanel2, hostAndPortPanel);
		AppearanceSettings.setForeground(Color.lightGray, chooseGameFileLabel, numberOfTeamsLabel);

		AppearanceSettings.setSize(800, 60, welcomePanel);
		AppearanceSettings.setSize(800, 150, buttonPanel, numberOfTeamsPanel);
		AppearanceSettings.setSize(800, 80, fileChooserPanel);

		welcomeLabel.setFont(AppearanceConstants.fontLarge);
		numberOfTeamsLabel.setSize(700, 65);

		//setting box layouts of panels
		// Change this --
		AppearanceSettings.setBoxLayout(BoxLayout.LINE_AXIS, buttonPanel, bottomPanel, fileChooserPanel, teamLabelsPanel1, teamLabelsPanel2, teamTextFieldsPanel1, teamTextFieldsPanel2);
		AppearanceSettings.setBoxLayout(BoxLayout.PAGE_AXIS, northPanel, teamNamesPanel, numberOfTeamsPanel, teamsAndFilePanel);
//		teamsAndFilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
//		teamsAndFilePanel.setLayout(new GridLayout(1, 4));

		//method iterates through components and add glue after each one is added, bool indicates whether glue should be added at the initially as well
		AppearanceSettings.addGlue(teamLabelsPanel1, BoxLayout.LINE_AXIS, true, teamNameLabels.get(0), teamNameLabels.get(1));
		AppearanceSettings.addGlue(teamLabelsPanel2, BoxLayout.LINE_AXIS, true, teamNameLabels.get(2), teamNameLabels.get(3));
		AppearanceSettings.addGlue(teamTextFieldsPanel1, BoxLayout.LINE_AXIS, true, teamNameTextFields.get(0), teamNameTextFields.get(1));
		AppearanceSettings.addGlue(teamTextFieldsPanel2, BoxLayout.LINE_AXIS, true, teamNameTextFields.get(2), teamNameTextFields.get(3));
		AppearanceSettings.addGlue(teamNamesPanel, BoxLayout.PAGE_AXIS, true, teamLabelsPanel1, teamTextFieldsPanel1, teamLabelsPanel2, teamTextFieldsPanel2);

		//don't want to pass in fileNameLabel since I don't want glue after it
		AppearanceSettings.addGlue(fileChooserPanel, BoxLayout.LINE_AXIS, true, chooseGameFileLabel, fileChooserButton);
		fileChooserPanel.add(fileNameLabel);

		//don't want to pass in fileChooserPanel since I don't want glue after it
		AppearanceSettings.addGlue(teamsAndFilePanel, BoxLayout.PAGE_AXIS, true, numberOfTeamsPanel);

		// Add to teamsAndFilePanel
		teamsAndFilePanel.add(hostAndPortPanel);
		teamsAndFilePanel.add(fileChooserPanel);

		AppearanceSettings.addGlue(buttonPanel, BoxLayout.LINE_AXIS, true, startGameButton, clearDataButton, logoutButton, exitButton);

		//add other components to other containers

		// Network Wrapper
		networkWrapper.setLayout(new BoxLayout(networkWrapper, 1));

		// Adding to Network Panel
		networkPanel.add(Box.createGlue());
		networkPanel.setLayout(new BoxLayout(networkPanel, 0));
		networkPanel.add(Box.createGlue());
		networkPanel.add(SWGNotNetworked);//, FlowLayout.LEADING);
		networkPanel.add(Box.createGlue());
		networkPanel.add(SWGHost);//, FlowLayout.CENTER);
		networkPanel.add(Box.createGlue());
		networkPanel.add(SWGJoin);//, FlowLayout.TRAILING);
		networkPanel.add(Box.createGlue());

		labelWrapper.add(networkLabel, SwingConstants.CENTER);
		networkLabel.setVerticalAlignment(SwingConstants.CENTER);

		networkWrapper.add(Box.createGlue());
		networkWrapper.add(labelWrapper);
		networkWrapper.add(networkPanel);

		welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
		welcomePanel.add(quickPlay, BorderLayout.EAST);

		titlePanel.add(welcomePanel, BorderLayout.NORTH);
		titlePanel.add(networkWrapper, BorderLayout.CENTER);

		northPanel.add(titlePanel);

		// Create hostAndPortPanel
		hostAndPortPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		portField.setPreferredSize(new Dimension(150, 40));
		IPAddressField.setPreferredSize(new Dimension(150, 40));
		hostAndPortPanel.add(portField);
		hostAndPortPanel.add(Box.createGlue());
		hostAndPortPanel.add(IPAddressField);
		portField.setVisible(false);
		IPAddressField.setVisible(false);

		// Wrapper panel for NoTL - LOCAL
		JPanel NoTLWrapper = new JPanel();
		NoTLWrapper.add(numberOfTeamsLabel, CENTER_ALIGNMENT);
		NoTLWrapper.setPreferredSize(new Dimension(800, 50));
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, NoTLWrapper);

		numberOfTeamsPanel.add(hostAndPortPanel);
		numberOfTeamsPanel.add(NoTLWrapper);
		numberOfTeamsPanel.add(slider);

//		// Bottom panel
		bottomPanel.setLayout(new BoxLayout(bottomPanel, 1));
		bottomPanel.add(Box.createGlue());
		// Status Message Wrapper
		JPanel sMWrapper = new JPanel();
		sMWrapper.add(statusMessage, CENTER_ALIGNMENT);
		sMWrapper.setBackground(AppearanceConstants.darkBlue);
		bottomPanel.add(sMWrapper);
		bottomPanel.add(buttonPanel);
		statusMessage.setForeground(Color.lightGray);
		statusMessage.setVisible(true);

		mainPanel.add(northPanel);
		mainPanel.add(teamsAndFilePanel);
		mainPanel.add(teamNamesPanel);
		mainPanel.add(bottomPanel);
//		mainPanel.add(buttonPanel);
	}

	//determines whether the chosen file is valid
	private void setHaveValidFile(File file){

		//if they had already chosen a valid file, but want to replace it, need to clear stored data
		if (haveValidFile) gameData.clearData();

		try{
			//try parsing this file; the parseFile method could throw an exception here, in which case we know it was invalid
			gameData.parseFile(file.getAbsolutePath());
			haveValidFile = true;

			if (gameData.getGameFile().getNumberOfRatings() == -1) fileNameLabel.setText(file.getName() + "    no rating");

			else fileNameLabel.setText(file.getName() + "    average rating: "+gameData.getGameFile().getAverageRating()+"/5");
			//check if the user can start the game
			startGameButton.setEnabled(haveValidFile && haveNames());
		}

		catch (Exception e){
			haveValidFile = false;
			startGameButton.setEnabled(false);
			fileNameLabel.setText("");
			//pop up with error message
			JOptionPane.showMessageDialog(this,
					e.getMessage(),
					"File Reading Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean compareNumberTeams(int numTeams) {
//		System.out.println("numTeams = " + numTeams + ", numberOfTeams = " + numberOfTeams);
		return (numTeams == numberOfTeams);
	}

	private void addListeners(){

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//		addWindowListener(new ExitWindowListener(this));

		// Not Networked
		SWGNotNetworked.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				startGameButton.setText("Start Jeopardy");

				fileNameLabel.setVisible(true);
				
				quickPlay.setVisible(true);
				quickPlay.setEnabled(true);

				statusMessage.setText("");
				statusMessage.setVisible(true);
				clearDataButton.setVisible(true);
				clearDataButton.setEnabled(true);

				SWGNotNetworked.setSelected(true);
				SWGJoin.setSelected(false);
				SWGHost.setSelected(false);
				// Networking Text Fields
				portField.setVisible(false);
				IPAddressField.setVisible(false);
				// FC Message
				numberOfTeamsLabel.setVisible(true);
				chooseGameFileLabel.setVisible(true);
				// File Chooser
				fileChooser.setEnabled(true);
				fileChooser.setVisible(true);
				fileChooserButton.setVisible(true);
				// Slider
				//slider settings
				AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
				slider.setValue(1);
				slider.setSnapToTicks(true);
				slider.setPreferredSize(new Dimension(500, 50));
				startGameButton.setEnabled(false);
				slider.setEnabled(true);
				slider.setVisible(true);
				// Reset label
				statusMessage.setText("");
				
				teamNameTextFields.get(0).setText("");	
				// Show changes
				repaint();
				validate();
			}
		});

		// Hosting
		SWGHost.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				fileNameLabel.setVisible(true);
				
				quickPlay.setVisible(true);
				quickPlay.setEnabled(true);

				statusMessage.setText("");
				startGameButton.setText("Start Game");

//				statusMessage.setText("Waiting for teams to join");
				statusMessage.setVisible(true);
				clearDataButton.setEnabled(false);

				SWGHost.setSelected(true);
				SWGJoin.setSelected(false);
				SWGNotNetworked.setSelected(false);
				portField.setVisible(true);
				IPAddressField.setVisible(false);
				// FC Message
				numberOfTeamsLabel.setVisible(true);
				chooseGameFileLabel.setVisible(true);
				// File Chooser
				fileChooser.setEnabled(true);
				fileChooser.setVisible(true);
				fileChooserButton.setVisible(true);
				// Slider
				//slider settings
				slider.setValue(2);
				AppearanceSettings.setSliders(2, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
				slider.setSnapToTicks(true);
				slider.setPreferredSize(new Dimension(500, 50));
				startGameButton.setEnabled(false);
				slider.setEnabled(true);
				slider.setVisible(true);
				// Remove the FCButton
				// Tjem add the one textfield
				// Show changes
				teamNameTextFields.get(0).setText(StartWindowGUI.this.loggedInUser.username);	
				
				repaint();
				validate();
			}
		});

		// Joining
		SWGJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				for (int i = 1; i< MAX_NUMBER_OF_TEAMS; i++){
					setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					teamNameTextFields.get(i).setText("");
				}
				
				teamNameTextFields.get(0).setText(StartWindowGUI.this.loggedInUser.username);				
				fileNameLabel.setVisible(false);
				
				quickPlay.setVisible(false);
				quickPlay.setEnabled(false);

				statusMessage.setText("");
				startGameButton.setText("Join Game");
				clearDataButton.setEnabled(false);

//				statusMessage.setText("Waiting for teams to join");
				statusMessage.setVisible(true);

				SWGJoin.setSelected(true);
				SWGHost.setSelected(false);
				SWGNotNetworked.setSelected(false);
				portField.setVisible(true);
				IPAddressField.setVisible(true);
				// FC Message
				numberOfTeamsLabel.setVisible(false);
				chooseGameFileLabel.setVisible(false);
				// File Chooser
				fileChooser.setEnabled(false);
				fileChooser.setVisible(false);
				fileChooserButton.setVisible(false);
				// Slider
				slider.setValue(1);
				slider.setEnabled(false);
				slider.setVisible(false);
				// Remove the FCButton

				// Show changes
				repaint();
				validate();
			}
		});


		//adding a document listener to each text field. This will allow us to determine if the user has entered team names
		for (int i = 0; i<MAX_NUMBER_OF_TEAMS; i++){
			teamNameTextFields.get(i).getDocument().addDocumentListener(new StartWindowGUI.MyDocumentListener());
		}

		fileChooserButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.showOpenDialog(StartWindowGUI.this);
				File file = fileChooser.getSelectedFile();

				if (file != null) setHaveValidFile(file);

				// ADD HERE
				if (SWGNotNetworked.isSelected()) {
					startGameButton.setEnabled(haveNames() && haveValidFile);
				} else if (SWGJoin.isSelected()) {
					startGameButton.setEnabled(hasName() && !portField.getText().equals("") && !IPAddressField.getText().equals(""));
				} else if (SWGHost.isSelected()) {
					startGameButton.setEnabled(hasName() && haveValidFile && !portField.getText().equals(""));
				}
			}

		});

		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				//sets appropriate text fields and labels invisible
				if (SWGNotNetworked.isSelected()) {
					for (int i = slider.getValue(); i < MAX_NUMBER_OF_TEAMS; i++) {
						setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
					//sets appropriate text fields and labels visible
					for (int i = 0; i < slider.getValue(); i++) {
						setVisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
					//check if the user can start the game
					startGameButton.setEnabled(haveNames() && haveValidFile);
				} else if (SWGHost.isSelected()){
					for (int i = 1; i < MAX_NUMBER_OF_TEAMS; i++) {
						setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
					//sets appropriate text fields and labels visible
					setVisible(teamNameLabels.get(0), teamNameTextFields.get(0));
					//check if the user can start the game
					startGameButton.setEnabled(haveNames() && haveValidFile);
				}
			}

		});

		// Problems
		startGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
//				if (SWGHost.isSelected() && quickPlay.isSelected()) {
//					gameData.setNumberOfQuestions(5);
//					System.out.println("SWG: 5 Questions");
//					jeopardyClient.sendMessage("5 Questions");
//				} else

				if (SWGNotNetworked.isSelected() && quickPlay.isSelected()){
					gameData.setNumberOfQuestions(5);
					System.out.println("SWG: 5 Questions");
//				} else if (SWGNotNetworked.isSelected() && !quickPlay.isSelected()){
//					gameData.setNumberOfQuestions(25);
//					System.out.println("SWG: 25 Questions");
//				} else if (SWGJoin.isSelected()){
//					// Ignore
				} else if (SWGNotNetworked.isSelected() && !quickPlay.isSelected()){
					gameData.setNumberOfQuestions(25);
					System.out.println("SWG: 25 Questions");
				}

				if (SWGNotNetworked.isSelected()) {
					startGameButton.setEnabled(true);
					numberOfTeams = slider.getValue();
					java.util.List<String> teamNames = new ArrayList<>(numberOfTeams);
					//getting the text in each of the visible text fields and storing them in a list
					for (int i = 0; i < numberOfTeams; i++) {
						teamNames.add(teamNameTextFields.get(i).getText());
					}
					//initializing TeamGUIComponents objects
					gameData.setTeams(teamNames, numberOfTeams);
					new MainGUI(gameData, loggedInUser, userTable).setVisible(true);
					dispose();
				} else if (SWGJoin.isSelected()) {
					startGameButton.setEnabled(false);
					jeopardyClient = new JeopardyClient(IPAddressField.getText(), Integer.parseInt(portField.getText()), StartWindowGUI.this);
					teamNameObject = new TeamName(teamNameTextFields.get(0).getText());
					jeopardyClient.sendMessage(teamNameObject);
				} else if (SWGHost.isSelected()) {
					startGameButton.setEnabled(false);
					numberOfTeams = slider.getValue();
					System.out.println("SWG: " + numberOfTeams + " teams");

					jeopardyServer = new JeopardyServer(Integer.parseInt(portField.getText()), slider.getValue(), gameData, StartWindowGUI.this);
					teamNameObject = new TeamName(teamNameTextFields.get(0).getText());
					jeopardyServer.teamNameVector.add(teamNameObject);
//					System.out.println("SWG: " + "Adding " + (teamNameTextFields.get(0).getText()));

					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						System.out.println("SWG: " + ie.getStackTrace());
					}
					jeopardyClient = new JeopardyClient(IPAddressField.getText(), Integer.parseInt(portField.getText()), StartWindowGUI.this);

					boolean allClientsConnected = false;

					// Busy looping -- problem?
//					while (!allClientsConnected) {
//						 Checks if all the clients are connected
//						int nOfT = jeopardyServer.connectionCount;
//						allClientsConnected = compareNumberTeams(jeopardyServer.connectionCount);

//						// Changing the statement
//						if (numberOfTeams - (jeopardyServer.connectionCount) > 1) {
//							statusMessage.setText("Waiting for " + (numberOfTeams - (jeopardyServer.connectionCount)) + " teams to join");
//							statusMessage.setVisible(true);
//							validate();
//							repaint();
//						} else {
//							statusMessage.setText("Waiting for " + (numberOfTeams - (jeopardyServer.connectionCount)) + " team to join");
//							statusMessage.setVisible(true);
//							validate();
//							repaint();
//						}

//						System.out.println(("numberOfTeams: " + numberOfTeams + ", jeopardyServer.connectionCount: " + newServer.connectionCount));
						// Trying to send the message
//						Message message = new Message("Message is being received");
//						jeopardyServer.sendMessageToAllClients(message);

//						validate();
//						repaint();
//					}

				}
//				if (!SWGNotNetworked.isSelected()) {
//					// Changing the statement
//					if (numberOfTeams - (jeopardyServer.connectionCount) > 1) {
//						statusMessage.setText("Waiting for " + (numberOfTeams - (jeopardyServer.connectionCount)) + " teams to join");
//						statusMessage.setVisible(true);
//						validate();
//						repaint();
//					} else {
//						System.out.println("Waiting for " + (numberOfTeams - (jeopardyServer.connectionCount)) + " team to join");
//						statusMessage.setText("Waiting for " + (numberOfTeams - (jeopardyServer.connectionCount)) + " team to join");
//						statusMessage.setVisible(true);
//						validate();
//						repaint();
//					}
//				}
			}

		});


		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (SWGNotNetworked.isSelected()) {
					int answer = JOptionPane.showConfirmDialog(StartWindowGUI.this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AppearanceConstants.exitIcon);
					if (answer == JOptionPane.YES_OPTION){
						System.exit(0);
					}
				} else if (SWGHost.isSelected()) {
					System.out.println("Host is exiting");
					StartWindowGUI.this.dispose();
					jeopardyServer.sendMessageToAllClients("HOST EXITED");
				} else if (SWGJoin.isSelected()) {
					StartWindowGUI.this.dispose();
					jeopardyClient.sendMessage(new TeamNameRemover(StartWindowGUI.this.teamNameObject));
				}
			}
		});

		exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (SWGNotNetworked.isSelected()) {
					dispose();
					System.exit(0);
				} else if (SWGHost.isSelected()) {
					System.out.println("Host is exiting");
					StartWindowGUI.this.dispose();
					jeopardyServer.sendMessageToAllClients("HOST EXITED");
				} else if (SWGJoin.isSelected()) {
					StartWindowGUI.this.dispose();
					jeopardyClient.sendMessage(new TeamNameRemover(StartWindowGUI.this.teamNameObject));
				}
			}

		});

		clearDataButton.addActionListener(new ActionListener(){

			//reseting all data
			@Override
			public void actionPerformed(ActionEvent e) {
				haveNames = false;
				haveValidFile = false;
				gameData.clearData();
				quickPlay.setSelected(false);
				//start index at 1, we still was to show the 0th elements (team 1)
				for (int i = 1; i<MAX_NUMBER_OF_TEAMS; i++){
					setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					teamNameTextFields.get(i).setText("");
				}

				startGameButton.setEnabled(false);
				teamNameTextFields.get(0).setText("");
				slider.setValue(1);
				fileNameLabel.setText("");
			}

		});

		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (SWGNotNetworked.isSelected()) {
					new LoginGUI(userTable).setVisible(true);
					dispose();
				} else if (SWGHost.isSelected()) {
					StartWindowGUI.this.dispose();
					jeopardyServer.sendMessageToAllClients("HOST LOGGED OUT");
					LoginGUI newLG = new LoginGUI(StartWindowGUI.this.userTable);
					newLG.setVisible(true);
				} else if (SWGJoin.isSelected()) {
					StartWindowGUI.this.dispose();
					jeopardyClient.sendMessage(new TeamNameRemover(StartWindowGUI.this.teamNameObject));
					LoginGUI newLG = new LoginGUI(StartWindowGUI.this.userTable);
					newLG.setVisible(true);
				}
			}

		});

	}

	//updates and returns the haveNames member variable of whether all teams have been named
	private boolean haveNames(){

		haveNames = true;
		//check to see if all relevant text fields have text in them
		for (int i = 0; i<slider.getValue(); i++){

			if (teamNameTextFields.get(i).getText().trim().equals("")) haveNames = false;
		}

		return haveNames;
	}

	private boolean hasName(){
		haveNames = true;
		//check to see if all relevant text fields have text in them
		if (teamNameTextFields.get(0).getText().trim().equals("")) {
			haveNames = false;
		}
		return haveNames;
	}

	//document listener; in each method, simply checking whether the user can start the game
	private class MyDocumentListener implements DocumentListener{

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (SWGNotNetworked.isSelected()) {
				startGameButton.setEnabled(haveNames() && haveValidFile);
			} else if (SWGJoin.isSelected()) {
				startGameButton.setEnabled(hasName() && !portField.getText().equals("") && !IPAddressField.getText().equals(""));
			} else if (SWGHost.isSelected()) {
				startGameButton.setEnabled(hasName() && haveValidFile && !portField.getText().equals(""));
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			startGameButton.setEnabled(haveNames() && haveValidFile);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			startGameButton.setEnabled(haveNames() && haveValidFile);
		}
	}

	JPanel getPanel() {
		return currentPanel;
	}

}
