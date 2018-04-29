# Assignment 4: Suveena Thanawala

Email: thanawal@usc.edu

ID: 4894177800


**Notes:**
- When on the start screen, because of the way that the document listener is written in the solution code, if you click away from a screen, you might have to add to the TextField (a space, or random character) for the start button to become enabled

- There were issues with sending/updating GameData I ran into, so the Final Jeopardy window will not show up BUT there is a print statement that will get written to the console when it is time for FJ

- Wrote in IntelliJ, exported to Eclipse & confirmed it works. Please let me know if there are issues (though there should not be)


**Tests Run:**


**User Accounts in Database (0.4%)** - All tested and work

- 0.2% - User accounts are stored in a database in MySql

- 0.2% - Sql script


**Start Window Changes (1.3%)** - All tested and work

- 0.05% - 3 RadioButtons

- 0.3% - Required components show, and none others

- 0.05% - Default input X

- 0.4% - Enabling buttons & # Users

- 0.4% - Handle exit/logout


**Main Screen Networking Changes (0.1%)** - All tested and work

- 0.1% - Visibly/Invisibly disabling


**Question Panel Networking Changes (1.35%)** - All tested and work

- 0.3% - The current team answering the question should have the submit answer button
and text field enabled.

- 0.05% - The current team answering should have the message label display a notification
that it is their turn to answer.

- 0.4% - All teams that are not currently answering should have their submit answer
button and text fields disabled. Their message label should update to display the
current game status. These updates include: when it is a ‘buzz in’ period and
which team buzzed in first.

- 0.4% - If a team gets the question wrong, there is a ‘buzz in’ period. Only the players that
have not answered the question yet should be able to click the ‘Buzz In’ button.
This button should only be visible during these ‘buzz in’ periods. During this
period, all players’ answer text fields and buttons should be disabled.

- 0.1% - The first player to click ‘Buzz In’ gets to try to answer the question next. The team
name label on the top of the panel should update to reflect the current team
answering (for all the players). 

- 0.1% - If all teams answer incorrectly, they should all be navigated back to the main
game board screen, and the next player to choose should be the team next in
clockwise order from the team that originally chose the question.


**JMenuItems Functionality (0.4%)** - All tested and work

- 0.1% - Only the host can reset

- 0.1% - All other options & popups

- 0.1% - 'Okay’ button pressed, player is navigated back to
the start screen.

- 0.1% - For the player that chose the menu option, they should not see a pop-up


