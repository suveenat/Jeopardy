package game_logic;

import java.sql.*;
import java.util.*;

/**
 * Created by Suveena on 10/31/16.
 */
public class UserTable implements UserTableInterface {

    @Override
    public void createUserTable() {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Users?user=root&password=root&useSSL=false");
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS user_table (username varchar(55), password varchar(55), PRIMARY KEY (username))");
            // Prepared statement to add values
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
//                    System.out.println("connection not null");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
//                System.out.println("connection null");
            }
        }

    }

    // Change
    @Override
     public String insert(String username, String password) {
        Connection connection = null;
        Statement statement = null;
        String message = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Users?user=root&password=root&useSSL=false");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user_table (username, password) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.execute();
        } catch (Exception e) {
            if (e instanceof com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException) {
//                System.out.println("Duplicate");
                message = "A user with this name already exists";
                return message;
            }
            e.printStackTrace();
//            System.out.println("check1");
    } finally {
            if (statement != null) {
//                System.out.println("check2");
                try {
                    statement.close();
//                    System.out.println("check3");
                } catch (SQLException e) {
                    e.printStackTrace();
//                    System.out.println("check4");
                }
            }

            if (connection != null) {
                try {
//                    System.out.println("connection not null");
                    message = "Account created";
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
//                System.out.println("connection null");
            }
        }
        return message;
    }

    @Override
    public boolean check (String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Users?user=root&password=root&useSSL=false");
            String query = "SELECT * FROM user_table WHERE username = '" + username + "' AND password = '" + password + "'";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public void update(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Users?user=root&password=root&useSSL=false");
            preparedStatement = connection.prepareStatement("UPDATE user_info SET " +
                    "password = ? WHERE username = ?");

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();

            System.out.println("UPDATE Users SET " +
                    "password = ? WHERE username = ?");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
