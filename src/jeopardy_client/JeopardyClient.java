package jeopardy_client;

import frames.LoginGUI;
import frames.MainGUI;
import frames.NetworkMG;
import frames.StartWindowGUI;
import game_logic.*;
import jeopardy_data.*;
import jeopardy_data.NumberTeams;
import other_gui.QuestionGUIElement;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JeopardyClient extends Thread {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private User user;
    private UserTable userTable;
    public GameData gameData;
    public StartWindowGUI SWG;
    public NetworkMG NMG;
    public boolean isTurn;
    public int numTeams = 0;

    public JeopardyClient(String hostname, int port, StartWindowGUI SWG) {
        Socket s = null;
        try {
            s = new Socket(hostname, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            this.user = SWG.loggedInUser;
            this.userTable = SWG.userTable;
            this.gameData = new GameData();
            this.SWG = SWG;
            this.start();
            this.isTurn = false;

        } catch (IOException ioe) {
            System.out.println("JC: " + "ioe: " + ioe.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
//                JeopardyData data = (JeopardyData)ois.readObject();
                Object data = ois.readObject();
//                String d = (String)data;

                if (data == null) {
//                    System.out.println("data is null");
                    int n = 2;

                } else if (data instanceof FJSignal) {
                    System.out.println("Time for Final Jeopardy");
//                    NMG.gameData.disableRemainingButtons();
                    NMG.validate();
                    NMG.repaint();
                } else if (data instanceof String) {
//                    System.out.println("Is this happening");
                    if (data.equals("true")) {
//                        System.out.println("JC: " + "Getting bool");
                    } else if (data.equals("QGUIE: Testing Buzz")) {
//                        System.out.println("JC: Testing Buzz");
                    } else if (data.equals("Sent JeopardyGameData")) {
//                      System.out.println("JC: " + "JD has sent");
//                      this.mainGUI = new MainGUI(this.gameData, this.user, this.userTable);
//                      this.SWG.setVisible(false);
//                      this.mainGUI.setVisible(true);
                    } else if (((String) data).startsWith("Waiting for ")) {
                        SWG.statusMessage.setText((String) data);
                        SWG.validate();
                        SWG.repaint();
                    } else if (data.equals("HOST LOGGED OUT")) {
                        SWG.statusMessage.setText("So sorry, the host canceled the game! Please choose another game to join.");
                        resetClientSWG();
                    } else if (data.equals("HOST EXITED")) {
                        SWG.statusMessage.setText("So sorry, the host canceled the game! Please choose another game to join.");
                        resetClientSWG();
                    } else if (data.equals("Return to mainPanel")) {
                        NMG.showMainPanel();
                    } else if (data.equals("All answered")) {
                        // ??
                    } else if (data.equals("Log out normally")) {
                        NMG.dispose();
                        new LoginGUI(userTable).setVisible(true);
                    } else if (data.equals("Log out with message")) {
                        JOptionPane.showMessageDialog(this.NMG, "A player has logged out.");
                        NMG.dispose();
                        new LoginGUI(userTable).setVisible(true);
                    } else if (data.equals("CF normally")) {
                        NMG.dispose();
                        new StartWindowGUI(user, userTable).setVisible(true);
                    } else if (data.equals("CF with message")) {
                        JOptionPane.showMessageDialog(this.NMG, "Chosing new file.");
                        NMG.dispose();
                        new StartWindowGUI(user, userTable).setVisible(true);
                    } else if (data.equals("Exit normally")) {
                        System.exit(0);
                    } else if (data.equals("Exit with message")) {
                        JOptionPane.showMessageDialog(this.NMG, "A player exited.");
                        System.exit(0);
                    }

                } else if (data instanceof TeamName) {
//                    System.out.println("TeamName: " + ((TeamName) data).getData());
                } else if (data instanceof Message) {
//                    System.out.println("Is a message: ");
//                    System.out.println(((Message) (data)).getData());
                } else if (data instanceof JeopardyGameData) {
                    // HERE
                    this.gameData = ((JeopardyGameData) data).getData();
                    this.NMG = new NetworkMG(this.gameData, this.user, this.userTable, this.SWG.getJS(), this);

                    // INITIAL DISABLEMENT
                    String firstTeamName = this.gameData.getTeamDataList().get(this.NMG.gameData.firstTeam).getTeamName();
                    if (firstTeamName.equals(this.SWG.teamNameObject.getData())) {
//                        System.out.println("JC: First turn is me, " + this.SWG.teamNameObject.getData());
                        this.isTurn = true;
                    } else {
//                        System.out.println("JC: First turn is you, " + this.gameData.getTeamDataList().get(this.NMG.gameData.whoseTurn).getTeamName());
                        this.isTurn = false;
                    }

                    if (this.SWG.SWGHost.isSelected()) {
                        SWG.jeopardyServer.NMG = this.NMG;
//                        System.out.println("JC: Set NMG");
                    }

                    this.SWG.setVisible(false);
                    this.NMG.setVisible(true);

                } else if (data instanceof TeamNameRemover) {
//                    System.out.println("JC : Getting a TNR");
                } else if (data instanceof TurnChange) {
//                    System.out.println("JC: It's " + ((TurnChange) data).getData() + "'s turn now, and I am " + SWG.teamNameObject.getData());
                    if (!(SWG.teamNameObject.getData().equals(((TurnChange) data).getData()))) {
//                        NMG.disableComponents();
                        isTurn = false;
//                        this.gameData.getQuestions().disableGUIButtons();
//                        System.out.println("JC: isTurn false");
                    } else {
//                        NMG.enableComponents();
                        isTurn = true;
//                        System.out.println("JC: isTurn true");
                    }
                } else if (data instanceof QuestionGUIXY) {
                    // Change panel
                    QuestionGUIElement QGUIE = findGameBoard(((QuestionGUIXY) data).X, ((QuestionGUIXY) data).Y);
                    NMG.changePanel(QGUIE.questionPanel);
                    if (!isTurn) {
                        QGUIE.submitAnswerButton.setEnabled(false);
                        QGUIE.answerField.setEnabled(false);
                    } else {
                        QGUIE.submitAnswerButton.setEnabled(true);
                        QGUIE.answerField.setEnabled(true);
                    }
                } else if (data instanceof AnnouncementLabel) {
                    QuestionGUIElement QGUIE = findGameBoard(((AnnouncementLabel) data).X, ((AnnouncementLabel) data).Y);
                    QGUIE.announcementsLabel.setText(((AnnouncementLabel) data).getData()); // Make a QGUIE
                } else if (data instanceof Buzz) {
                    QuestionGUIElement QGUIE = findGameBoard(((Buzz) data).X, ((Buzz) data).Y);
                    QGUIE.announcementsLabel.setText(((Buzz) data).getData()); // Make a QGUIE
                    QGUIE.passButton.setVisible(true);
                    QGUIE.passButton.setText("Buzz In!");
                    if (isTurn) {
                        QGUIE.passButton.setVisible(false);
                        QGUIE.hadTurn = true;
                    } else if (!QGUIE.hadTurn) {
                        QGUIE.passButton.setVisible(true);
                        QGUIE.passButton.setEnabled(true);
                    } else {
                        QGUIE.passButton.setVisible(false);
                    }
                    QGUIE.submitAnswerButton.setEnabled(false);
                    QGUIE.answerField.setEnabled(false);
                    NMG.validate();
                    NMG.repaint();

                } else if (data instanceof BuzzedIn) {
                    QuestionGUIElement QGUIE = findGameBoard(((BuzzedIn) data).X, ((BuzzedIn) data).Y);
                    QGUIE.announcementsLabel.setText(((BuzzedIn) data).getData() + " buzzed in first!");
                    if (((BuzzedIn) data).getData().equals(this.SWG.teamNameObject.getData())) {
                        QGUIE.passButton.setEnabled(false);
                        QGUIE.answerField.setEnabled(true);
                        QGUIE.submitAnswerButton.setEnabled(true);
                        this.isTurn = true;
                    } else {
                        QGUIE.passButton.setEnabled(false);
                        this.isTurn = false;

                    }
                    NMG.validate();
                    NMG.repaint();

                } else if (data instanceof NumberTeams) {
                    this.numTeams = ((NumberTeams) data).getData();
                } else if (data instanceof AnsweredSignal) {
//                    QuestionGUIElement QGUIE = findGameBoard(((AnsweredSignal) data).X, ((AnsweredSignal) data).Y);
//                    QuestionGUIElement QGUIE = findGameBoard(((AnsweredSignal) data).X, ((AnsweredSignal) data).Y);
//                    QGUIE.changeTurns();
                    NMG.showMainPanel();
                } else if (data instanceof ResetTurn) {
                    QuestionGUIElement QGUIE = findGameBoard(((ResetTurn) data).X, ((ResetTurn) data).Y);
                    QGUIE.hadTurn = false;
                } else if (data instanceof AnsweredCorrectlySignal) {
                    NMG.showMainPanel();
                } else if (data instanceof UpdateLabel) {
//                    System.out.println("JC: " + ((UpdateLabel) data).getData());
                    NMG.addUpdate(((UpdateLabel) data).getData());
                } else if (data instanceof DisableQuestion) {
//                    System.out.println("JC: Disabling question");
                    QuestionGUIElement QGUIE  = findGameBoard(((DisableQuestion) data).X, ((DisableQuestion) data).Y);
                    QGUIE.getGameBoardButton().setEnabled(false);
//                    ((DisableQuestion) data).button.setDisabledIcon(QGUIE.getDisabledIcon());
                } else if (data instanceof DeductMessage) {
//                    System.out.println("JC: Deducting/updating");
//                    QuestionGUIElement QGUIE = findGameBoard(((DeductMessage) data).X, ((DeductMessage) data).Y);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gameData.getCurrentTeam().addPoints(((DeductMessage) data).points);
                            gameData.getCurrentTeam().updatePointsLabel();
//                            ((DeductMessage) data).currentTeam.updatePointsLabel(); // Won't update
                        }
                    });
                    NMG.validate();
                    NMG.repaint();
                } else {
                    System.out.println("JC : Other issue");
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("JC: " + "cnfe: " + cnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("JC: " + "ioe: " + ioe.getMessage());
        }
    }

    public QuestionGUIElement findGameBoard(int X, int Y) {
        QuestionGUIElement QGUIE = null;
        for (QuestionGUIElement question : this.gameData.getQuestions()) {
            if (question.getX() == X && question.getY() == Y) {
                QGUIE = question;
            }
        }
        return QGUIE;
    }


    public void resetClientSWG() {
        // Do when you're finally done
    }

    public void sendMessage(Object object) {
        try {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ioe) {
            System.out.println("JC: " + "ioe: " + ioe.getMessage());
        }
    }
}