package other_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import frames.MainGUI;
import frames.NetworkMG;
import frames.WinnersAndRatingGUI;
import game_logic.GameData;
import game_logic.QuestionAnswer;
import jeopardy_client.JeopardyClient;
import jeopardy_data.*;
import listeners.TextFieldFocusListener;
import sun.nio.ch.Net;

public class QuestionGUIElement extends QuestionAnswer {

	//the JButton displayed on the game board grid on MainGUI associated with this question
	private JButton gameBoardButton;
	//the panel that will be switched out with the main panel when the gameBoardButton is clicked
	public JPanel questionPanel;
	//the label that would show whether there was a format problem with the submitted answer
	public JLabel announcementsLabel;
	//components for the questionPanel
	private JLabel categoryLabel;
	private JLabel pointLabel;
	private JLabel teamLabel;
	private JTextPane questionLabel;
	public JTextField answerField;
	public JButton submitAnswerButton;
	public JButton passButton;
	public int states = 0;
	public int numTurns = 1;
	public boolean hadTurn = false;
	public boolean allHadTurns = false;


	private static ImageIcon disabledIcon;
	private static ImageIcon enabledIcon;

	public QuestionGUIElement(String question, String answer, String category, int pointValue, int indexX, int indexY) {
		super(question, answer, category, pointValue, indexX, indexY);

		initializeComponents();
		createGUI();
		addActionListeners();
	}

	//set disabled icon member variable, called from game data
	public static void setDisabledIcon(String filePath){
		Image grayImage = new ImageIcon(filePath).getImage();
		disabledIcon = new ImageIcon(grayImage.getScaledInstance(400, 400,  java.awt.Image.SCALE_SMOOTH));
	}
	//called from game data when clearing the parsed file
	public static void clearIcons(){
		disabledIcon = null;
		enabledIcon = null;
	}

	public static ImageIcon getEnabledIcon(){
		return enabledIcon;
	}

	public static ImageIcon getDisabledIcon(){
		return disabledIcon;
	}
	//set enabled icon member varibale, called from game data
	public static void setEnabledIcon(String filePath){
		Image blueImage = new ImageIcon(filePath).getImage();
		enabledIcon = new ImageIcon(blueImage.getScaledInstance(400, 400,  java.awt.Image.SCALE_SMOOTH));
	}


	// Disable all buttons visibly
	public void disableGUIButtons() {
		answerField.setEnabled(false);
		submitAnswerButton.setEnabled(false);
		passButton.setEnabled(false);
	}

	// Disable all buttons visibly
	public void enableGUIButtons() {
		answerField.setEnabled(true);
		submitAnswerButton.setEnabled(true);
		passButton.setEnabled(true);
	}



	//public methods
	public JButton getGameBoardButton(){
		return gameBoardButton;
	}
	//this method is called from the MainGUI; cannot add the action listeners until then
	public void addActionListeners(MainGUI mainGUI, GameData gameData){
		//AnsweringLogic contains all the logic for passing, switching turns, add/deduct points, determine if its FJ time
		AnsweringLogic answeringLogic = new AnsweringLogic(mainGUI, gameData);
		//we want all the buttons to be modifying the same answering logic data
		submitAnswerButton.addActionListener(new SubmitAnswerActionListener(answeringLogic));
		gameBoardButton.addActionListener(new GameBoardActionListener(mainGUI, gameData, answeringLogic));
		passButton.addActionListener(new PassActionListener(answeringLogic));
	}


	//////////////////////////////// HERE IS THE GUI FOR QUESTION PANEL //////////////////////////////////
	public void addActionListeners(NetworkMG NMG, GameData gameData){ // Here we visibly change
		//AnsweringLogic contains all the logic for passing, switching turns, add/deduct points, determine if its FJ time
		NAL answeringLogic = new NAL(NMG, gameData);
		//we want all the buttons to be modifying the same answering logic data
		submitAnswerButton.addActionListener(new NSAAL(answeringLogic));
		gameBoardButton.addActionListener(new NGBAL(NMG, gameData, answeringLogic));
		passButton.addActionListener(new NPAL(NMG, answeringLogic));
	}

	//method used to reset the data of this question to it's default
	//called from MainGUI when user chooses 'Restart This Game' option on the menu
	public void resetQuestion(){
		submitAnswerButton.setEnabled(true);
		asked = false;
		teamLabel.setText("");
		gameBoardButton.setEnabled(true);
		gameBoardButton.setBackground(Color.darkGray);
		answerField.setText("Enter your answer");
		passButton.setEnabled(true);
		passButton.setVisible(false);
	}

	//private methods
	//initialize member variables
	private void initializeComponents(){

		questionPanel = new JPanel(new GridLayout(3, 1));
		gameBoardButton = new JButton("$"+pointValue, enabledIcon);
		pointLabel = new JLabel("$"+ pointValue);
		categoryLabel = new JLabel(category);
		questionLabel = new JTextPane();
		announcementsLabel = new JLabel("");
		answerField = new JTextField("Enter your answer.");
		submitAnswerButton = new JButton("Submit Answer");
		teamLabel = new JLabel("");
		passButton = new JButton("Pass");
	}

	private void createGUI(){

		//local variables
		JPanel infoPanel = new JPanel(new GridLayout(1, 3));

		JPanel answerPanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel formatErrorPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel passPanel = new JPanel();
		//appearance settings
		AppearanceSettings.setBackground(Color.darkGray, passPanel, gameBoardButton, questionPanel, announcementsLabel, answerPanel, formatErrorPanel, southPanel, passPanel);
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, teamLabel, pointLabel, categoryLabel, infoPanel);
		AppearanceSettings.setForeground(Color.lightGray, teamLabel, pointLabel, categoryLabel, announcementsLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontLarge, questionLabel, teamLabel, pointLabel, categoryLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontMedium, gameBoardButton, announcementsLabel, submitAnswerButton, answerField, passButton);
		AppearanceSettings.setTextAlignment(teamLabel, pointLabel, categoryLabel, announcementsLabel);

		questionLabel.setText(question);
		questionLabel.setEditable(false);
		//need to do this so the text shows up on the button
		gameBoardButton.setHorizontalTextPosition(JButton.CENTER);
		gameBoardButton.setVerticalTextPosition(JButton.CENTER);
		passButton.setVisible(false);
		// sourced from: http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
		//centers the text in the question pane
		StyledDocument doc = questionLabel.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		gameBoardButton.setBorder(BorderFactory.createLineBorder(AppearanceConstants.darkBlue));
		gameBoardButton.setOpaque(true);
		answerField.setForeground(Color.gray);
		questionLabel.setBackground(AppearanceConstants.lightBlue);

		//components that need their size set
		gameBoardButton.setPreferredSize(new Dimension(200, 200));
		questionLabel.setPreferredSize(new Dimension(800, 400));
		answerField.setPreferredSize(new Dimension(600, 100));
		infoPanel.setPreferredSize(new Dimension(900, 80));
		formatErrorPanel.setPreferredSize(new Dimension(800, 100));

		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));
		//add components to the panels
		infoPanel.add(teamLabel);
		infoPanel.add(categoryLabel);
		infoPanel.add(pointLabel);

		answerPanel.add(answerField, BorderLayout.CENTER);
		answerPanel.add(submitAnswerButton, BorderLayout.EAST);

		formatErrorPanel.add(announcementsLabel);

		northPanel.add(infoPanel);
		northPanel.add(formatErrorPanel);

		southPanel.add(answerPanel, BorderLayout.NORTH);
		southPanel.add(passPanel, BorderLayout.SOUTH);

		passPanel.add(passButton);

		questionPanel.add(northPanel);
		questionPanel.add(questionLabel);
		questionPanel.add(southPanel);
	}

	//add focus listener to answer text field, and the rest of the action listeners will be added later from a call from MainGUI
	private void addActionListeners(){
		answerField.addFocusListener(new SubmitAnswerFocusListener("Enter your answer", answerField));
	}

	//custom focus listener extends TextFieldFocusListener so we can disable the pass button, and still have default text
	private class SubmitAnswerFocusListener extends TextFieldFocusListener{

		public SubmitAnswerFocusListener(String defaultText, JTextField thisTextField) {
			super(defaultText, thisTextField);
		}

		@Override
		public void focusGained(FocusEvent fe){
			super.focusGained(fe);
			passButton.setEnabled(false);
		}
	}

	//private listener classes
	//action listener for gameBoardButton
	private class GameBoardActionListener implements ActionListener{

		private MainGUI mainGUI;
		private GameData gameData;
		private AnsweringLogic answeringLogic;

		public GameBoardActionListener(MainGUI mainGUI, GameData gameData, AnsweringLogic answeringLogic){
			this.mainGUI = mainGUI;
			this.gameData = gameData;
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//set game board button disabled
			gameBoardButton.setEnabled(false);
			gameBoardButton.setDisabledIcon(disabledIcon);
			//change panel to the question panel
			mainGUI.changePanel(questionPanel);
			//set the original and current team in answering logic
			answeringLogic.setTeamVariables();
			//set the label of which team chose the question
			teamLabel.setText(gameData.getCurrentTeam().getTeamName());
		}

	}

	// Networked GAME BUTTON
	private class NGBAL implements ActionListener{

		private NetworkMG NMG;
		private GameData gameData;
		private NAL answeringLogic;

		public NGBAL(NetworkMG NMG, GameData gameData, NAL answeringLogic){
			this.NMG = NMG;
			this.gameData = gameData;
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(NMG.jeopardyClient.isTurn) {

				//set game board button disabled
				gameBoardButton.setEnabled(false);
				NMG.jeopardyClient.sendMessage(new DisableQuestion(getX(), getY()));
				gameBoardButton.setDisabledIcon(disabledIcon);
				//change panel to the question panel
				NMG.changePanel(questionPanel);
//				NMG.jeopardyClient.sendMessage("NAVIGATING TO NEW SCREEN");
				NMG.jeopardyClient.sendMessage(new QuestionGUIXY(questionPanel.getX(), questionPanel.getY()));
				//set the original and current team in answering logic
				answeringLogic.setTeamVariables();
				//set the label of which team chose the question
				teamLabel.setText(gameData.getCurrentTeam().getTeamName());
			} else {

			}
		}

	}

	//action listener for submitAnswerButton
	private class SubmitAnswerActionListener implements ActionListener{

		private AnsweringLogic answeringLogic;

		public SubmitAnswerActionListener(AnsweringLogic answeringLogic){
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//check whether their answer was correct
			Boolean isAnsweredCorrectly = answeringLogic.checkAnswer();
			//if the answer was right, we need to either navigate back to the mainGUI or to FinalJeopardyGUI
			if (isAnsweredCorrectly) answeringLogic.checkReadyForFinalJeopardy();
		}
	}

	//action listener for submitAnswerButton
	private class NSAAL  implements ActionListener{ // NSAAL AL CHANGE

		private NAL answeringLogic;

		public NSAAL (NAL answeringLogic){
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(answeringLogic.NWG.jeopardyClient.isTurn) {
				//check whether their answer was correct
				Boolean isAnsweredCorrectly = answeringLogic.checkAnswer();
				//if the answer was right, we need to either navigate back to the mainGUI or to FinalJeopardyGUI
				if (isAnsweredCorrectly) answeringLogic.NCheckReadyForFinalJeopardy();
			} else {
//				System.out.println("QGUIE: It's not my (" + answeringLogic.NWG.jeopardyClient.getName() + ") turn!");
			}
//			answeringLogic.NWG.jeopardyClient.sendMessage(new AnsweredSignal(getX(), getY()));
		}
	}

	//action listener for the pass button
	private class PassActionListener implements ActionListener{

		private AnsweringLogic answeringLogic;

		public PassActionListener(AnsweringLogic answeringLogic){
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//add an update to the game progress and swicth turns to next team
			answeringLogic.mainGUI.addUpdate(answeringLogic.currentTeam.getTeamName()+" chose to pass.");
			answeringLogic.changeTurns();
		}
	}

	// Networked
	private class NPAL implements ActionListener{ // NPAL AL CHANGE

		private NetworkMG NMG;
		private NAL answeringLogic;

		public NPAL(NetworkMG NMG, NAL answeringLogic){
			this.NMG = NMG;
			this.answeringLogic = answeringLogic;
		}

		@Override
		public void actionPerformed(ActionEvent e) { // THIS IS PASS
			hadTurn = true;
			NMG.jeopardyClient.sendMessage(new BuzzedIn(getX(), getY(), NMG.jeopardyClient.SWG.teamNameObject.getData()));
			String message = NMG.gameData.getCurrentTeam().getTeamName() + " buzzed in first!";
			NMG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), message));
		}
	}


	// NOT NETWORKED
	//private class that handles all the logic of changing turns, cheking correctness of answers, determining if a
	private class AnsweringLogic{

		protected MainGUI mainGUI;
		private GameData gameData;
		//team that chose the question
		private TeamGUIComponents originalTeam;
		//team that is currently answering
		public TeamGUIComponents currentTeam;
		private Boolean hadSecondChance;

		public AnsweringLogic(MainGUI mainGUI, GameData gameData){
			this.mainGUI = mainGUI;
			this.gameData = gameData;
			hadSecondChance = false;
		}
		//called from game board button action listener
		private void setTeamVariables(){
			originalTeam = gameData.getCurrentTeam();
			currentTeam = originalTeam;
		}
		//checks the answer taken from answerField and determines whether the player gets a second chance to answer
		//returns boolean of whether the question was answered correctly
		private Boolean checkAnswer(){

			String givenAnswer = answerField.getText();
			//valid format
			if (gameData.validAnswerFormat(givenAnswer)){
				//team got the answer right
				Boolean correctAnswer = QuestionAnswer.correctAnswer(givenAnswer, answer);

				if (correctAnswer){
					//add points, send an update to the main gui, and update number of chosen questions
					currentTeam.addPoints(pointValue);
					mainGUI.addUpdate(currentTeam.getTeamName()+" got the answer right! $"+pointValue+" will be added to their total. ");
					mainGUI.addUpdate("Since "+currentTeam.getTeamName()+" got the answer right, they get to choose the next question!");
					gameData.updateNumberOfChosenQuestions();
					return true;
				}
				//team got the answer wrong
				else{
					//deduct points, send an update to the main gui, and change turns to next team
					deductFromCurrentTeam(currentTeam.getTeamName()+" got the answer wrong! $"+pointValue+" will be deducted from their total. ");
				}
			}
			//invalid format
			else{
				//if the user already had a second chance, deduct points
				if (hadSecondChance){
					announcementsLabel.setText("Your answer is still formatted incorrectly. $"+pointValue+" will be deducted from your total.");
					deductFromCurrentTeam(currentTeam.getTeamName()+", your answer is still formatted incorrectly. $"+pointValue+" will be deducted from their total. ");
				}
				//if user has not had second chance yet, so answered = false
				else{
					announcementsLabel.setText("Invalid format of your answer. Remember to pose it as a question");
					answerField.setText("");
					passButton.setEnabled(false);
					hadSecondChance = true;
					mainGUI.addUpdate(currentTeam.getTeamName()+" had a badly formatted answer. They will get a second chance to answer");
				}
			}
			return false;
		}

		//deduct points, send an update to the main gui, and change turns to next team
		//used both when they answer incorrectly and when they have an ill-formatted answer for the second time
		private void deductFromCurrentTeam(String update){
			currentTeam.deductPoints(pointValue);
			mainGUI.addUpdate(update);
			changeTurns();
		}

		private void changeTurns() {
			//change turn to next team in clockwise order
			gameData.nextTurn();
			//reset the currentTeam
			currentTeam = gameData.getCurrentTeam();
			//all teams have tried and failed at answering or passed
			if (currentTeam.equals(originalTeam)){
//				state = 1;
//				System.out.println("QGE: State is " + state);
				//set next turn to be team right next after originalTeam
				gameData.nextTurn();
//				currentTeam = gameData.getCurrentTeam();
				//increment the number of chosen questions
				gameData.updateNumberOfChosenQuestions();
				mainGUI.addUpdate("None of the teams who answered got it right! The answer was: "+answer);
				mainGUI.addUpdate("It's "+gameData.getCurrentTeam().getTeamName()+"'s turn to choose a question!");

				//either go back to mainGUI or to FJ panel
				checkReadyForFinalJeopardy();
			}
			else{ // Will have to change this for networking
//				state = 2;
//				System.out.println("QGE: State is " + state);
				//reset pass button so the team can choose to pass
				passButton.setVisible(true);
				passButton.setEnabled(true);
				answerField.setText("");
				hadSecondChance = false;
				//add updates
				mainGUI.addUpdate("It's "+currentTeam.getTeamName()+"'s turn to try to answer the question.");
				announcementsLabel.setText("It's "+currentTeam.getTeamName()+"'s turn to try to answer the question.");
				//update the team label to show current team
				teamLabel.setText(currentTeam.getTeamName());
			}
		}


		private void checkReadyForFinalJeopardy(){
			//have all the questions been asked? if so, time for final jeopardy
			if (gameData.readyForFinalJeoaprdy()){
				//in case we are playing a Quick Play game, disable remaining buttons
				gameData.disableRemainingButtons();
				mainGUI.addUpdate("It's time for Final Jeopardy!");
				//figure out the teams that qualified for final jeopardy
				gameData.determineFinalists();
				//if there are no qualifying teams, pop up a WinnersGUI (showing no one won)
				if (gameData.getFinalistsAndEliminatedTeams().getFinalists().size() == 0){
					mainGUI.showMainPanel();
					new WinnersAndRatingGUI(gameData).setVisible(true);
				}
				//if there are final teams, change the current panel to the final jeopardy view
				else{
					mainGUI.changePanel(new FinalJeopardyGUI(gameData, mainGUI));
				}
			}
			//not ready, go back to main GUI
			else{
				mainGUI.showMainPanel();
			}
		}
	}

	////////////////////////////////// NETWORKED //////////////////////////////////
	// Look back at this
	public class NAL{

		protected NetworkMG NWG;
		private GameData gameData;
		//team that chose the question
		private TeamGUIComponents originalTeam;
		//team that is currently answering
		protected TeamGUIComponents currentTeam;
		private Boolean hadSecondChance;

		public NAL(NetworkMG NWG, GameData gameData){
			this.NWG = NWG;
			this.gameData = gameData;
			hadSecondChance = false;
		}
		//called from game board button action listener
		private void setTeamVariables(){
			originalTeam = gameData.getCurrentTeam();
			currentTeam = originalTeam;
		}
		//checks the answer taken from answerField and determines whether the player gets a second chance to answer
		//returns boolean of whether the question was answered correctly
		private Boolean checkAnswer(){
//			System.out.println ("QGUIE: Checking answer");
			NWG.jeopardyClient.sendMessage(new AnnouncementLabel(getX(), getY(), "Checking answer"));
			String givenAnswer = answerField.getText();
			//valid format
			if (gameData.validAnswerFormat(givenAnswer)){
				//team got the answer right

				Boolean correctAnswer = true;

				String [] splitAnswer = givenAnswer.trim().toLowerCase().split("\\s+");
				if (splitAnswer.length == 3){
					if (splitAnswer[2].equals(answer.toLowerCase())){
						correctAnswer = true;
//						System.out.println("QGUIE: Answer right");
					} else {
						correctAnswer = false;
//						System.out.println("QGUIE: Answer wrong");
					}
				} else {
//					System.out.println("QGUIE: splitAnswer.length: " + splitAnswer.length);
					correctAnswer = true; // Temp
				}

//				System.out.println("QGUIE: Correct answer is \"" + answer + "\", your answer was \"" + givenAnswer + "\"");

				if (correctAnswer){
					currentTeam = gameData.getCurrentTeam();
					if (currentTeam == null) {
//						System.out.println("QGUIE: currentTeam null");
					} else {
//						System.out.println("QGUIE: currentTeam NOT null");
						currentTeam.addPoints(pointValue); //ADDPOINTS
//						System.out.println("QGUIE: " + currentTeam.getTeamName() + " 's answer is correct, pointValue = " + pointValue);
						//add points, send an update to the main gui, and update number of chosen questions
						String update = currentTeam.getTeamName() + " got the answer right! $" + pointValue + " will be added to their total. ";
						NWG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), update));
						NWG.jeopardyClient.sendMessage(new DeductMessage(getX(), getY(), currentTeam, pointValue/*, NWG, gameData*/));
					}

//					NWG.addUpdate(currentTeam.getTeamName()+" got the answer right! $"+pointValue+" will be added to their total. ");
//					NWG.addUpdate("Since "+currentTeam.getTeamName()+" got the answer right, they get to choose the next question!");
//					NWG.jeopardyClient.sendMessage(new UpdateMessage(currentTeam.getTeamName()+" got the answer right! $"+pointValue+" will be added to their total. "));
//					NWG.jeopardyClient.sendMessage(new UpdateMessage("Since "+currentTeam.getTeamName()+" got the answer right, they get to choose the next question!"));
					gameData.updateNumberOfChosenQuestions();
//					numTurns++;

					this.NWG.jeopardyClient.sendMessage(new AnsweredCorrectlySignal(getX(), getY()));
					changeTurns();
					return true;
				}
				//team got the answer wrong
				else{
					//deduct points, send an update to the main gui, and change turns to next team
//					System.out.println ("QGUIE: Answer is wrong");
					changeTurns();
					deductFromCurrentTeam(currentTeam.getTeamName()+" got the answer wrong! $"+pointValue+" will be deducted from their total. ");
				}
			}
			//invalid format
			else{
				//if the user already had a second chance, deduct points
				if (hadSecondChance){
//					announcementsLabel.setText("Your answer is still formatted incorrectly. $"+pointValue+" will be deducted from your total.");
					String announcement = "Your answer is still formatted incorrectly. $"+pointValue+" will be deducted from your total.";
					NWG.jeopardyClient.sendMessage(new AnnouncementLabel(getX(), getY(), announcement));
					// Editing deduction
//					numTurns++;
					changeTurns();
					deductFromCurrentTeam(currentTeam.getTeamName()+", your answer is still formatted incorrectly. $"+pointValue+" will be deducted from their total. ");
				}
				//if user has not had second chance yet, so answered = false
				else{
//					announcementsLabel.setText("Invalid format of your answer. Remember to pose it as a question");
					String announcement = "Invalid format of your answer. Remember to pose it as a question";
					NWG.jeopardyClient.sendMessage(new AnnouncementLabel(getX(), getY(), announcement));
					answerField.setText("");
					passButton.setEnabled(false);
					hadSecondChance = true;

					// CAUSING NPE
					String message = currentTeam.getTeamName() + " had a badly formatted answer. They will get a second chance to answer";
					NWG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), message));
				}
			}
			return false;
		}

		//deduct points, send an update to the main gui, and change turns to next team
		//used both when they answer incorrectly and when they have an ill-formatted answer for the second time
		private void deductFromCurrentTeam(String update){ // DEDUCT PART TWO
			currentTeam.deductPoints(pointValue);
			NWG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), update));
			NWG.jeopardyClient.sendMessage(new DeductMessage(getX(), getY(), currentTeam, pointValue/*, NWG, gameData*/));
		}

		private void changeTurns(){
			//change turn to next team in clockwise order
			gameData.nextTurn();
			numTurns++;
			//reset the currentTeam
			currentTeam = gameData.getCurrentTeam();
			//all teams have tried and failed at answering or passed
			if (currentTeam.equals(originalTeam)){
//				System.out.println("QGE: State is " + states);
				// Changing and sending message
				//set next turn to be team right next after originalTeam
				gameData.nextTurn();
				currentTeam = gameData.getCurrentTeam();
				//increment the number of chosen questions
				gameData.updateNumberOfChosenQuestions();

				String message = "None of the teams who answered got it right! The answer was: " + answer;
				NWG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), message));
//				NWG.addUpdate("None of the teams who answered got it right! The answer was: "+answer);

				String nextMessage = "It's " + gameData.getCurrentTeam().getTeamName() + "'s turn to choose a question!";
				NWG.jeopardyClient.sendMessage(new UpdateLabel(getX(), getY(), nextMessage));

//				NWG.addUpdate("It's "+gameData.getCurrentTeam().getTeamName()+"'s turn to choose a question!");
				NWG.jeopardyClient.sendMessage(new TurnChange(gameData.getCurrentTeam().getTeamName()));
				//either go back to mainGUI or to FJ panel
				NCheckReadyForFinalJeopardy();
			}
			else {
				// SEND MESSAGE
				NWG.jeopardyClient.sendMessage(new Buzz(getX(), getY(), "Buzz in to answer!"));
				//reset pass button so the team can choose to pass
				passButton.setVisible(true);
				passButton.setEnabled(true);
				answerField.setText("");
				hadSecondChance = false;
				//add updates
				// IF I AM NOT CURRENTTEAM.GETTEAMNAME, disable my buttons
				//update the team label to show current team
				teamLabel.setText(currentTeam.getTeamName());
			}

		}

		public void returnNMG () {
//			numTurns = 0;
			NWG.jeopardyClient.sendMessage("Return to mainPanel");
		}

		private void checkReturnNMG () {
			if (numTurns == NWG.jeopardyClient.numTeams) {
				// Change to main panel
				returnNMG();
//				System.out.println("QGUIE: numTurns = " + numTurns + ", numTeams = " + NWG.jeopardyClient.numTeams);
				// Update all scores
			} else {
				// Back to buzzing in
//				System.out.println("QGUIE: numTurns = " + numTurns + ", numTeams = " + NWG.jeopardyClient.numTeams);
			}
		}

		private void NCheckReadyForFinalJeopardy(){
			//have all the questions been asked? if so, time for final jeopardy
			if (gameData.readyForFinalJeoaprdy()){
				//in case we are playing a Quick Play game, disable remaining buttons
				gameData.disableRemainingButtons();
				NWG.addUpdate("It's time for Final Jeopardy!");
				//figure out the teams that qualified for final jeopardy
				gameData.determineFinalists();
				//if there are no qualifying teams, pop up a WinnersGUI (showing no one won)
				if (gameData.getFinalistsAndEliminatedTeams().getFinalists().size() == 0){
					NWG.showMainPanel();
					new WinnersAndRatingGUI(gameData).setVisible(true);
				}
				//if there are final teams, change the current panel to the final jeopardy view
				else{
					NWG.changePanel(new NetworkFJG(gameData, NWG));
				}
			}
			//not ready, go back to main GUI
			else{
				NWG.showMainPanel();
			}
		}
	}


}