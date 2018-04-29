package game_logic;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import frames.MainGUI;
import frames.NetworkMG;
import other_gui.QuestionGUIElement;
import other_gui.TeamGUIComponents;

public class GameData implements Serializable { //Ser.

	private transient BufferedReader br;
	private transient FileReader fr;
	//set of objects that contain information on a specific question (answer, question, x index, y index, ect.)
	//they also each contain GUI components that we will need in the MainGUI
	public Set<QuestionGUIElement> questions;
	//maps from the point values to their index that will then map the their locations on the MainGUI board
	protected Map<Integer, Integer> pointValuesMapToIndex;
	//maps from the lower cased category string to Category objects, that will then map the their locations on the MainGUI board
	protected Map<String, Category> categoriesMap;
	//list of each team with; contains all info in a TeamData object as well as GUI elements needed for the MainGUI
	private List<TeamGUIComponents> teamDataList;
	private Boolean haveFinalJeopardy;

	private int numberOfTeams;
	private GameFile gameFile;
	//number of questions is no longer static; it can be either 5 or 25
	private int numberOfQuestions;
	private String filePath;

	//QuestionAnswer class now contains logic to check the format of an answer
	private static Set<String> unmodifiableSetAnswerVerbs;
	private static Set<String> unmodifiableSetAnswerNouns;

	private FinalistsAndEliminatedTeams finalistsAndEliminatedTeams;

	//holds the index of the current team in teamData
	public int whoseTurn; // Changed from private
	//how many questions have been chosen
	public int numberOfChosenQuestions;

	protected String finalJeopardyQuestion;
	protected String finalJeopardyAnswer;
	//lines of the game file
	private List<String> fileLines;
	public int firstTeam;

	public GameData() {

		pointValuesMapToIndex = new HashMap<>();
		categoriesMap = new HashMap<>();
		questions = new HashSet<>();
		haveFinalJeopardy = false;
		numberOfChosenQuestions = 0;
		teamDataList = new ArrayList<>();
		fileLines = new ArrayList<>();
		initializeAnswerFormatSet();
	}
	//public methods

	//GETTERS
	public FinalistsAndEliminatedTeams getFinalistsAndEliminatedTeams(){
		return finalistsAndEliminatedTeams;
	}
	//get the game file object, called from the WinnersAndRatingGUI
	public GameFile getGameFile(){
		return gameFile;
	}

	public int getNumberOfTeams(){
		return numberOfTeams;
	}

	public List<TeamGUIComponents> getTeamDataList(){
		return teamDataList;
	}

	public Map<Integer, Integer> getPointValuesMap(){
		return pointValuesMapToIndex;
	}

	public Map<String, Category> getCategories(){
		return categoriesMap;
	}

	public String getFinalJeopardyQuestion(){
		return finalJeopardyQuestion;
	}

	public String getFinalJeopardyAnswer(){
		return finalJeopardyAnswer;
	}

	public Set<QuestionGUIElement> getQuestions(){
		return questions;
	}
	//set the number of questions; this method will be called from the StartWindowGUI
	public void setNumberOfQuestions(int numQuestions){
		numberOfQuestions = numQuestions;
	} // Changed

	//will be called from the action listeners of the game board buttons
	public void updateNumberOfChosenQuestions(){
		numberOfChosenQuestions++;
	}

	//will be called from the action listeners of the game board buttons
	public Boolean readyForFinalJeoaprdy(){
		return numberOfChosenQuestions == numberOfQuestions;
	}

	public void nextTurn(){
		whoseTurn = ((whoseTurn + 1) == numberOfTeams) ? 0 : whoseTurn + 1;
	}

	public void setNextTurn(int team){
		whoseTurn = team;
	}

	//called when the game ends, in case it was quick play
	public void disableRemainingButtons(){
		//if quick play
		if (numberOfQuestions == 5){
			//set all questions disabled
			questions.forEach(question -> {
				question.getGameBoardButton().setEnabled(false);
				question.getGameBoardButton().setDisabledIcon(QuestionGUIElement.getDisabledIcon());
			});
		}
	}

	//initialize the teams, this method will be called from the StartWindowGUI
	public void setTeams(List<String> teams, int numTeams){ // CHANGE THIS - TEAMS
		//set number of teams
		this.numberOfTeams = numTeams;
		//for each team name, create a new TeamGUIElement; provide location of the team in the teamDataList (i)
		//and pass in 0 as their current total
		for (int i = 0; i<numberOfTeams; i++){
			teamDataList.add(new TeamGUIComponents(i, 0, teams.get(i)));
		}

		chooseFirstTeam();
	}

	//randomly choose the first team to play - ADD HERE
	public void chooseFirstTeam(){

		Random rand = new Random();
		this.firstTeam = rand.nextInt(numberOfTeams);
		whoseTurn = this.firstTeam;
	}

	//reset all the necessary game data to restart the game; this will be called from the MainGUI
	public void restartGame(){

		//reset the necessary info in each question
		for (QuestionGUIElement question : questions){
			question.resetQuestion();
		}
		//reset the necessary info in each team
		for (TeamGUIComponents team : teamDataList){
			team.resetTeam();
		}

		numberOfChosenQuestions = 0;
	}

	//create a FinalistsAndEliminatedTeams object when ready for final jeopardy
	//this will be called from MainGUI/ a QuestionGUIElement's action listener
	public void determineFinalists(){
		finalistsAndEliminatedTeams = new FinalistsAndEliminatedTeams(teamDataList);
	}

	//get the TeamGUIComponents of the current team; called from MainGUI/ a QuestionGUIElement's action listener
	public TeamGUIComponents getCurrentTeam(){
		return teamDataList.get(whoseTurn);
	}

	//called from FinalJeopardyGUI when all teams have answer the fj question
	public void addOrDeductTeamBets(MainGUI mainGUI){

		for (TeamGUIComponents teamData : finalistsAndEliminatedTeams.getFinalists()){
			String update = teamData.addOrDeductBet();
			mainGUI.addUpdate(update);
		}
	}

	//called from FinalJeopardyGUI when all teams have answer the fj question
	public void addOrDeductTeamBets(NetworkMG NMG){

		for (TeamGUIComponents teamData : finalistsAndEliminatedTeams.getFinalists()){
			String update = teamData.addOrDeductBet();
			NMG.addUpdate(update);
		}
	}

	//check whether the answer is in the format of a question
	public boolean validAnswerFormat(String answer){

		//if the answer is the empty string, it must be invalid
		if (answer.length() < 1) return false;

		//split the answer delineated by white space
		String[] splitAnswer = answer.trim().split("\\s+");
		//if the splitAnswer has length less than 2, we already know it cannot have both the noun and verb of the question format, and must be invalid
		if (splitAnswer.length < 2) return false;

		//now check whether our set of verbs contains the second element in the array, and whether the set of nouns contains the first element in the array
		return unmodifiableSetAnswerVerbs.contains(splitAnswer[1].toLowerCase()) && unmodifiableSetAnswerNouns.contains(splitAnswer[0].toLowerCase());
	}

	//add the appropriate verbs and nouns to our sets
	private void initializeAnswerFormatSet(){
		Set<String> nounsModifiableSet = new HashSet<>();
		Set<String> verbsModifiableSet = new HashSet<>();
		nounsModifiableSet.add("who");
		nounsModifiableSet.add("where");
		nounsModifiableSet.add("when");
		nounsModifiableSet.add("what");

		verbsModifiableSet.add("is");
		verbsModifiableSet.add("are");

		//this makes the sets unmodifiable
		unmodifiableSetAnswerNouns = Collections.unmodifiableSet(nounsModifiableSet);
		unmodifiableSetAnswerVerbs = Collections.unmodifiableSet(verbsModifiableSet);
	}

	//PARSING METHODS

	//clear the data if we found an error in the file
	public void clearData(){
		pointValuesMapToIndex.clear();
		categoriesMap.clear();
		questions.clear();
		haveFinalJeopardy = false;
		//clear the lines of the game file and the game file object
		fileLines.clear();
		gameFile = null;
		//clear the static icons
		Category.clearIcon();
		QuestionGUIElement.clearIcons();
	}

	//filename is no longer passed in the GameData constructor like it did in Assignment 1 solution
	//it now is passed in a class method
	public void parseFile(String fileName) throws Exception{
		this.filePath = fileName;
		openFile();
	}

	//private methods
	//must close our BufferedReader and FileReader!
	private void close() throws IOException{

		if (fr != null) fr.close();
		if (br != null) br.close();
	}

	//returns a String containing an error message, or, if no error, the empty string
	private void openFile() throws Exception{

		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			//returns a possible error from parsing the categories and point values
			parseCategoriesAndPoints();

			//now error contains the error string returned from parsing all the questions from the file, including final jeopardy
			parseQuestions();
		}
		catch (FileNotFoundException e) {
			throwException("File not found");
		}
		catch (IOException ioe){
			throwException("IOException");
		}
		finally{
			try {
				close();
			} catch (IOException e) {
				throwException("Issue with Closing the File");
			}
		}
	}

	private void throwException(String message) throws Exception{
		clearData();
		throw new Exception(message);
	}

	//parse the categories and point values from the file
	private void parseCategoriesAndPoints() throws IOException, Exception{

		String categories = br.readLine();
		//store the categories line
		fileLines.add(categories);
		String[] parsedCategories = categories.split("::");

		//if the split string has an array of size other than 6, too much or not enough info on categories
		if (parsedCategories.length != 6){
			throwException("Too many or too few categories provided.");
		}

		//iterate through each category and make sure it is not whitespace
		for (String str : parsedCategories){

			if (str.trim().equals("")){
				throwException("One of the categories is whitespace.");
			}
		}

		String pointValues = br.readLine();
		//store the point Values line
		fileLines.add(pointValues);
		String[] parsedPointValues = pointValues.split("::");

		//if the split string has an array of size other than 7, too much or not enough info on point values
		if (parsedPointValues.length != 7){
			throwException("Too many or too few dollar values provided.");
		}

		//iterate from 0 to 4, and added the categories and point values to their associated maps
		for (int i = 0; i<5; i++){
			//key of the map is the lowercased category, value is a Cateogry object
			categoriesMap.put(parsedCategories[i].toLowerCase().trim(), new Category(parsedCategories[i].trim(), i));

			try{
				//key of the map is the point value
				pointValuesMapToIndex.put(Integer.parseInt(parsedPointValues[i].trim()), i);
			}
			catch (NumberFormatException nfe){
				throwException( "One of the point values is a string.");
			}
		}
		//setting the icons of the Category class and the QuestionGUIElement class
		Category.setIcon(parsedCategories[5]);
		QuestionGUIElement.setEnabledIcon(parsedPointValues[5]);
		QuestionGUIElement.setDisabledIcon(parsedPointValues[6]);
	}

	//this parses all the questions from the file, including final jeopardy, and return an error message if needed
	private void parseQuestions() throws IOException, Exception{

		//stores the current line we have read
		String currentLine = "";
		//stores the current concatenated question information
		String fullQuestion = "";
		int questionCount = 0;

		while(questionCount != 26){

			currentLine = br.readLine();
			//we have not reached 26 questions, so if currentLine is null, there are not enough questions
			if (currentLine == null){
				throwException( "Not enough questions in the file");
			}
			//if the line does not start with '::', it is a continuation of the previous question and
			//should be concatenated onto fullQuestion
			if (!currentLine.startsWith("::")){
				fullQuestion += currentLine;
			}
			else{

				//if it does start with '::', then we must first parse the fullQuestion, and reset it to this new question
				//note that if the questionCount is 0, this is the first question in the file, so fullQuestion equals null
				//and does not need to be parsed
				if (questionCount != 0){
					//returns an error message associated with the parsing of this question
					parseQuestion(fullQuestion);
					//store the question, add back on the '::'
					fileLines.add("::"+fullQuestion);
				}
				//substring currentLine so it does not start with '::' anymore
				fullQuestion = currentLine.substring(2);
				questionCount++;
			}
		}
		//after the loop, the question stored in fullQuestion has not been parsed. If we skipped this next line
		//we would be missing the last question
		parseQuestion(fullQuestion);
		//store the last question
		fileLines.add("::"+fullQuestion);
		//create a new gamefile object with the game file liness, the path, and the rating data
		gameFile = new GameFile(fileLines, Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), filePath);

		//if the next line is not null, there are too many questions/lines in the file
		if (br.readLine() != null){
			throwException("Two many questions provided.");
		}

		//if we never found the final jeopardy question, it is missing!
		if (!haveFinalJeopardy){
			throwException("This game file does not have a final jeopardy question.");
		}
	}

	//parse a particular question into either a normal question, or the final jeopardy question
	private void parseQuestion(String question) throws Exception{

		if(question.toLowerCase().startsWith("fj")){

			//if we already have a final jeopardy question, we cannot have another and this must be an error
			if (haveFinalJeopardy){
				throwException ("Cannot have more than one final jeopardy question.");
			}
			else{
				haveFinalJeopardy = true;
				parseFinalJeopardy(question);
			}

		}
		else{
			parseNormalQuestion(question);
		}
	}

	//parse the final jeopardy question data and return an error message if needed
	private void parseFinalJeopardy(String finalJeopardyString) throws Exception{

		String [] questionData = finalJeopardyString.split("::");

		//error checking for null values, and for too much data
		if (questionData.length != 3) throwException( "Too much or not enough data provided for the final jeopardy question.");

		if (questionData[1].trim().equals("")) throwException( "The Final Jeopardy question cannot be whitespace");

		if (questionData[2].trim().equals("")) throwException( "The Final Jeopardy answer cannot be whitespace");

		finalJeopardyQuestion = questionData[1].trim();
		finalJeopardyAnswer = questionData[2].trim();
	}

	//does not check whether there is a duplicate category/point value question
	private void parseNormalQuestion(String question) throws Exception{

		String [] questionData = question.split("::");

		if (questionData.length != 4) throwException( "Too much or not enough data provided for this question");
			//if we were not provided with too much data, we parse the data in the string
		else{
			String category = questionData[0].trim();

			//if our map of categories does not contain this category (lowercased), then it does not exist
			if (!categoriesMap.containsKey(category.toLowerCase())) throwException("This category does not exist: "+category);

			Integer pointValue = -1;

			//try to parse the point value, surround with try catch in case it is a string
			try{
				pointValue = Integer.parseInt(questionData[1].trim());
			}
			catch (NumberFormatException nfe){
				throwException("The point value cannot be a String.");
			}

			//if the point values map does not contain this point value as a key, it does not exist
			if (!pointValuesMapToIndex.containsKey(pointValue)) throwException("This point value does not exist: "+pointValue);

			//get the x and y index based on the category and point value, which the QuestionGUIElement will need in the constructor
			int indexX = categoriesMap.get(category.toLowerCase().trim()).getIndex();
			int indexY = pointValuesMapToIndex.get(pointValue);

			//make sure the question and answer are not empty/white space
			if (questionData[2].trim().isEmpty()) throwException("The question cannot be whitespace.");

			if (questionData[3].trim().isEmpty()) throwException("The answer cannot be whitespace.");

			//add a new QuestionGUIElement object to our list of questions!
			questions.add(new QuestionGUIElement(questionData[2].trim(), questionData[3].trim(), category, pointValue, indexX, indexY));
		}

	}

}