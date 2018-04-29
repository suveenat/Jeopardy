package jeopardy_server;

import frames.NetworkMG;
import frames.StartWindowGUI;
import game_logic.GameData;
import jeopardy_data.ResetTurn;
import jeopardy_data.JeopardyGameData;
import jeopardy_data.TeamName;
import other_gui.QuestionGUIElement;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class JeopardyServer extends Thread {
    private ServerSocket ss;
    private int port;
    private Vector<ServerThread> serverThreads;
    public boolean allTeamsAdded = false;
    public Vector<TeamName> teamNameVector;
    public int connectionCount = 0; // Not 1?
    public GameData gameData;
    int numberOfTeams = 0;
    public StartWindowGUI SWG;
    public NetworkMG NMG;
    public int numTurns = 1;
    public int questionsAnswered = 0;
    public int questionsSelected = 25;

    public JeopardyServer(int port, int numberOfTeams, GameData gameData, StartWindowGUI SWG) {
        ServerSocket ss = null;
        this.numberOfTeams = numberOfTeams;
        this.port = port;
        serverThreads = new Vector<ServerThread>();
        teamNameVector = new  Vector<TeamName>();
        this.gameData = gameData;
        this.SWG = SWG;

        this.start();
    }

    public void run() {
        try {
            ss = new ServerSocket(port);
            while (true) {
                Socket s = ss.accept();

                this.connectionCount++;

                ServerThread st = new ServerThread(s, this);
                serverThreads.add(st);
                // More polling
                if (this.allTeamsAdded) {
                    // Nothing
                } else {
                    updateStatusMessages();
                }
            }
        } catch (IOException ioe) {
            System.out.println("JS: " + "ioe: " + ioe.getMessage());
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ioe) {
                    System.out.println("JS: " + "ioe closing ss: " + ioe.getMessage());
                }
            }
        }
    }

    public Vector<String> getTeamListVector() {
        Vector <String> teamNameStringVector = new Vector<String>();
        for (int i = 0; i < this.teamNameVector.size(); i++) {
            teamNameStringVector.add(this.teamNameVector.get(i).getData());
        }
        return teamNameStringVector;
    }

    public void updateStatusMessages() {
        if ((numberOfTeams - connectionCount) == 1) {
            sendMessageToAllClients("Waiting for " + (numberOfTeams - connectionCount) + " other player to join...");
            SWG.statusMessage.setText("Waiting for " + (numberOfTeams - connectionCount) + "  other player to join...");
        } else {
            sendMessageToAllClients("Waiting for " + (numberOfTeams - connectionCount) + " other players to join...");
            SWG.statusMessage.setText("Waiting for " + (numberOfTeams - connectionCount) + "  other players to join...");
        }
    }

    public void sendMessageToAllClients(Object object) {
//        System.out.println("Sending");
        synchronized (serverThreads) {

            for (ServerThread st : serverThreads) {
                st.sendMessage(object);
            }
        }
    }

    public int incrementTurns() {
        this.numTurns++;
        return this.numTurns;
    }

    public void resetTurns(int X, int Y) {
        this.numTurns = 0;
        for (ServerThread st : serverThreads) {
            st.sendMessage(new ResetTurn(X, Y));
        }
    }

    public void newFileFunction() {
        synchronized (serverThreads) {
            for (ServerThread st : serverThreads) {
                if(st.choseNewFile) {
                    st.sendMessage("CF normally");
                } else {
                    st.sendMessage("CF with message");
                }
            }
        }
    }

    public void logoutFunction() {
        synchronized (serverThreads) {
            for (ServerThread st : serverThreads) {
                if(st.loggedOut) {
                    st.sendMessage("Log out normally");
                } else {
                    st.sendMessage("Log out with message");
                }
            }
        }
    }

    public void exitGame() {
        synchronized (serverThreads) {
            for (ServerThread st : serverThreads) {
                if(st.exited) {
                    st.sendMessage("Exit normally");
                } else {
                    st.sendMessage("Exit with message");
                }
            }
        }
    }

    public void disableRemainingButtons(NetworkMG NMG, GameData gameData){
        //if quick play
        if (questionsSelected == 5){
            //set all questions disabled
            gameData.questions.forEach(question -> {
                question.getGameBoardButton().setEnabled(false);
                question.getGameBoardButton().setDisabledIcon(QuestionGUIElement.getDisabledIcon());
            });
        }
    }

    public void closeSocket() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAllTeamsAdded() {
        this.allTeamsAdded = true;
    }

    public JeopardyGameData getJeopardyGameData() {
        return new JeopardyGameData(this.gameData);
    }

}
