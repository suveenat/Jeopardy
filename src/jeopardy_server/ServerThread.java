package jeopardy_server;

import frames.MainGUI;
import frames.NetworkMG;
import game_logic.GameData;
import jeopardy_data.*;
import jeopardy_data.NumberTeams;
import other_gui.QuestionGUIElement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Exchanger;

public class ServerThread extends Thread {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JeopardyServer cs;
    public boolean loggedOut = false;
    public boolean choseNewFile = false;
    public boolean exited = false;

    public ServerThread(Socket s, JeopardyServer cs) {
        try {
            this.cs = cs;
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            this.start();
        } catch (IOException ioe) {
            System.out.println("ST: " + "ioe: " + ioe.getMessage());
        }
    }

    public void sendMessage(Object object) {
        try {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ioe) {
            System.out.println("ST: " + "ioe: " + ioe.getMessage());
        }
    }



    public void run() {
        try {
            while(true) {
                Object data = ois.readObject();
//                cs.sendMessageToAllClients(data);
                // Server thread
                if (data instanceof Message) {
                } else if (data instanceof TeamName) {
                    cs.teamNameVector.add((TeamName) data);
                    if (cs.SWG.numberOfTeams == cs.teamNameVector.size()) {
                        cs.setAllTeamsAdded();
                        cs.gameData.setTeams(cs.getTeamListVector(), cs.teamNameVector.size());
                        cs.sendMessageToAllClients(cs.getJeopardyGameData()); // Sending here
                        cs.SWG.setVisible(false);
                    } else {
                    }
                } else if (data instanceof DeductMessage) {
                    cs.sendMessageToAllClients(data);
                    if (cs.SWG.quickPlay.isSelected()) {
                        cs.questionsSelected = 5;
                    } else {
                        cs.questionsSelected = 25;
                    }
                    cs.questionsAnswered++;
                    if (cs.questionsAnswered == cs.questionsSelected) {
                        cs.sendMessageToAllClients(new FJSignal(((DeductMessage) data).X, ((DeductMessage) data).Y));
//                        cs.disableRemainingButtons(((DeductMessage) data).nmg, ((DeductMessage) data).gd);
                    }
                } else if (data instanceof DisableQuestion) {
                    cs.sendMessageToAllClients(data);
                } else if (data instanceof TeamNameRemover) {
                    cs.teamNameVector.remove(((TeamNameRemover) data).getData());
                    cs.connectionCount--;
                    cs.updateStatusMessages();
                } else if (data instanceof TurnChange) {
                    cs.incrementTurns();

                    cs.sendMessageToAllClients(data);
                } else if (data instanceof QuestionGUIXY) {
                    cs.sendMessageToAllClients(new NumberTeams(cs.numberOfTeams));
                    cs.sendMessageToAllClients(data);
                } else if (data instanceof UpdateMessage) {
                    cs.sendMessageToAllClients(data);
                } else if (data instanceof Buzz) {
                    if (cs.numberOfTeams <= cs.numTurns) {
                        cs.resetTurns(((Buzz) data).X, ((Buzz) data).Y);
                        cs.sendMessageToAllClients(new AnsweredSignal(((Buzz) data).X, ((Buzz) data).Y));

                    } else {

                    }
                    cs.incrementTurns();

                    cs.sendMessageToAllClients(data);
                } else if (data instanceof BuzzedIn) {
                    if (data.equals("QGUIE: Testing Buzz")) {

                        cs.sendMessageToAllClients(data);
                    }
                    cs.sendMessageToAllClients(data);
                } else if (data instanceof String) {
                    if (data.equals("QGUIE: Testing Buzz")) {

                        cs.sendMessageToAllClients("QGUIE: Testing Buzz");
                    } else if (data.equals("Return to mainPanel")) {
                        cs.sendMessageToAllClients(data);
                    }
                } else if (data instanceof ExitSignal) {
                    ((ExitSignal) data).exiting(this);
                    cs.exitGame();
                    cs.closeSocket();
                } else if (data instanceof NewFileSignal) {
                    // Chosing New File
                    ((NewFileSignal) data).chosingNewFile(this);
                    cs.newFileFunction();
                    cs.closeSocket();
                } else if (data instanceof LogoutSignal) {
                    // Logging this person out
                    ((LogoutSignal) data).loggingOut(this);
                    cs.logoutFunction();
                    cs.closeSocket();
                } else if (data instanceof AnsweredCorrectlySignal) {

                    cs.sendMessageToAllClients(data);
                } else if (data instanceof UpdateLabel) {

                    cs.sendMessageToAllClients(data);
                }

                else {

                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("ST: " + "cnfe in run: " + cnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("ST: " +  "ioe in run: " + ioe.getMessage());
        }
    }

}