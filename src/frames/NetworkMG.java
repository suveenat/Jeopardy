package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.*;

import game_logic.Category;
import game_logic.GameData;
import game_logic.User;
import game_logic.UserTable;
import jeopardy_client.JeopardyClient;
import jeopardy_data.ExitSignal;
import jeopardy_data.LogoutSignal;
import jeopardy_data.NewFileSignal;
import jeopardy_data.TeamNameRemover;
import jeopardy_server.JeopardyServer;
import listeners.ExitWindowListener;
import other_gui.*;

public class NetworkMG extends JFrame {

    private JPanel mainPanel;
    private JPanel currentPanel;

    private JPanel questionsPanel;
    public GameData gameData;
    public JButton[][] questionButtons;

    private static final int QUESTIONS_LENGTH_AND_WIDTH = 5;

    private JTextArea updatesTextArea;
    private JMenuBar menuBar;
    private JMenu menu;

    private JMenuItem logoutButton;
    private JMenuItem exitButton;
    private JMenuItem restartThisGameButton;
    private JMenuItem chooseNewGameFileButton;

    public JeopardyServer jeopardyServer;
    public JeopardyClient jeopardyClient;

    public boolean readyFJ = false;

    //in case we need to know which user is logged in
    private User loggedInUser;
    private UserTable userTable;

    public NetworkMG(GameData gameData, User user, UserTable userTable, JeopardyServer jeopardyServer, JeopardyClient jeopardyClient) {
        super("Jeopardy!");
        this.jeopardyServer = jeopardyServer;
        this.jeopardyClient = jeopardyClient;
        this.gameData = gameData;
        this.userTable = userTable;
        loggedInUser = user;
        initializeComponents();
        createGUI();
        addListeners();
    }

    // public methods
    public void addUpdate(String update) {
        updatesTextArea.append("\n" + update);
    }

    // this method changes the current panel to the provided panel
    public void changePanel(JPanel panel) {
        remove(currentPanel);
        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        // must repaint or the change won't show
        repaint();
        revalidate();
    }

    // To Test
    public void testNFJ() {
        new NetworkFJG(gameData, this).setVisible(true);
        repaint();
        revalidate();
    }

    public void showMainPanel() {
        changePanel(mainPanel);
    }

    // private methods
    private void initializeComponents() {
        mainPanel = new JPanel();
        currentPanel = mainPanel;
        exitButton = new JMenuItem("Exit Game");
        restartThisGameButton = new JMenuItem("Restart This Game");
        chooseNewGameFileButton = new JMenuItem("Choose New Game File");
        logoutButton = new JMenuItem("Logout");
        updatesTextArea = new JTextArea("Welcome to Jeopardy!");
        menu = new JMenu("Menu");
        questionButtons = new JButton[QUESTIONS_LENGTH_AND_WIDTH][QUESTIONS_LENGTH_AND_WIDTH];
        menuBar = new JMenuBar();
        questionsPanel = new JPanel(new GridLayout(QUESTIONS_LENGTH_AND_WIDTH, QUESTIONS_LENGTH_AND_WIDTH));
    }
    private void createGUI() {

        createMenu();
        createMainPanel();

        add(mainPanel, BorderLayout.CENTER);
        add(createProgressPanel(), BorderLayout.EAST);
        setSize(1500, 825);
    }

    // creating the JMenuBar
    private void createMenu() {

        if (this.jeopardyClient.SWG.SWGHost.isSelected()) {
            menu.add(restartThisGameButton);
        }

        menu.add(chooseNewGameFileButton);
        menu.add(logoutButton);
        menu.add(exitButton);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    // creating the main panel (the game board)
    private void createMainPanel() {
        mainPanel.setLayout(new BorderLayout());

        // getting the panel that holds the 'jeopardy' label
        JPanel jeopardyPanel = createJeopardyPanel();
        // getting the cateogries panel
        JPanel categoriesPanel = createCategoriesAndQuestionsPanels();
        JPanel northPanel = new JPanel();

        northPanel.setLayout(new BorderLayout());
        northPanel.add(jeopardyPanel, BorderLayout.NORTH);
        northPanel.add(categoriesPanel, BorderLayout.SOUTH);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(questionsPanel, BorderLayout.CENTER);
    }

    // creates the panel with the jeopardy label
    private JPanel createJeopardyPanel() {
        JPanel jeopardyPanel = new JPanel();
        JLabel jeopardyLabel = new JLabel("Jeopardy");
        AppearanceSettings.setBackground(AppearanceConstants.lightBlue, jeopardyPanel, jeopardyLabel);
        jeopardyLabel.setFont(AppearanceConstants.fontLarge);
        jeopardyPanel.add(jeopardyLabel);

        return jeopardyPanel;
    }

    // creates both the categories panel and the questions panel
    private JPanel createCategoriesAndQuestionsPanels() {
        JPanel categoriesPanel = new JPanel(new GridLayout(1, QUESTIONS_LENGTH_AND_WIDTH));
        AppearanceSettings.setBackground(Color.darkGray, categoriesPanel, questionsPanel);

        Map<String, Category> categories = gameData.getCategories();
        JLabel[] categoryLabels = new JLabel[QUESTIONS_LENGTH_AND_WIDTH];

        // iterate through the map of categories, and place them in the correct index
        for (Map.Entry<String, Category> category : categories.entrySet()) {
            categoryLabels[category.getValue().getIndex()] = category.getValue().getCategoryLabel();
        }

        // place the question buttons in the proper indices in 'questionButtons'
        for (QuestionGUIElement question : gameData.getQuestions()) {

            // adding action listeners to the question - CHANGE THIS
            question.addActionListeners(this, gameData);
            questionButtons[question.getX()][question.getY()] = question.getGameBoardButton();
        }

        // actually adding the categories and questions into their respective panels
        for (int i = 0; i < QUESTIONS_LENGTH_AND_WIDTH; i++) {

            categoryLabels[i].setPreferredSize(new Dimension(100, 70));
            categoryLabels[i].setIcon(Category.getIcon());
            categoriesPanel.add(categoryLabels[i]);

            for (int j = 0; j < QUESTIONS_LENGTH_AND_WIDTH; j++) {
                // have to use opposite indices because of how GridLayout adds components
                questionsPanel.add(questionButtons[j][i]);
            }
        }

        return categoriesPanel;
    }

    // creates the panel with the team points, and the Game Progress area
    private JPanel createProgressPanel() {
        // create panels
        JPanel pointsPanel = new JPanel(new GridLayout(gameData.getNumberOfTeams(), 2));
        JPanel southEastPanel = new JPanel(new BorderLayout());
        JPanel eastPanel = new JPanel();
        // other local variables
        JLabel updatesLabel = new JLabel("Game Progress");
        JScrollPane updatesScrollPane = new JScrollPane(updatesTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // setting appearances
        AppearanceSettings.setBackground(AppearanceConstants.lightBlue, southEastPanel, updatesLabel, updatesScrollPane,
                updatesTextArea);
        AppearanceSettings.setSize(400, 400, pointsPanel, updatesScrollPane);
        AppearanceSettings.setTextComponents(updatesTextArea);

        updatesLabel.setFont(AppearanceConstants.fontLarge);
        pointsPanel.setBackground(Color.darkGray);
        updatesLabel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        updatesScrollPane.setBorder(null);

        updatesTextArea.setText("Welcome to Jeopardy!");
        updatesTextArea.setFont(AppearanceConstants.fontSmall);
        updatesTextArea.append("The team to go first will be " + gameData.getCurrentTeam().getTeamName());

        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));
        // adding components/containers
        southEastPanel.add(updatesLabel, BorderLayout.NORTH);
        southEastPanel.add(updatesScrollPane, BorderLayout.CENTER);

        // adding team labels, which are stored in the TeamGUIComponents class,
        // to the appropriate panel
        for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
            TeamGUIComponents team = gameData.getTeamDataList().get(i);
            pointsPanel.add(team.getMainTeamNameLabel());
            pointsPanel.add(team.getTotalPointsLabel());
        }

        eastPanel.add(pointsPanel);
        eastPanel.add(southEastPanel);

        return eastPanel;
    }

    // adding even listeners
    private void addListeners() {

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //add window listener
//        addWindowListener(new ExitWindowListener(this));
        //add action listeners


        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jeopardyClient.sendMessage(new ExitSignal());
            }
        });

        // Edit here
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) { // May not work
                jeopardyClient.sendMessage(new ExitSignal());
            }

        });

        // Edit here
        restartThisGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updatesTextArea.setText("Game has been restarted.");
                // reset all data
                gameData.restartGame();
                // repaint the board to show updated data
                showMainPanel();
                addUpdate(" The team to go first will be " + gameData.getCurrentTeam().getTeamName());
            }

        });

        // Edit here
        chooseNewGameFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) { // May not work
                jeopardyClient.sendMessage(new NewFileSignal());
            }

        });

        // Edit here
        logoutButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                jeopardyClient.sendMessage(new LogoutSignal());
            }

        });
    }

}
