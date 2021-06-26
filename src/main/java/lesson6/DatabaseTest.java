package lesson6;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {
    static Connection connection;
    static Statement statement;

    public static void main(String[] args) throws SQLException {
        DatabaseTest databaseTest = new DatabaseTest();
        // databaseTest.dropTable();
        databaseTest.start();
     //   System.out.println(databaseTest.getNick("login1", "pass1"));
        System.out.println("ник изменился на " + databaseTest.changeNick("nick1", "vasya"));
        System.out.println(databaseTest.changeNick("nick2", "vasya"));
        System.out.println(databaseTest.changeNick("nick2", "nick1"));
    }

    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(ChatConstants.DATABASE_URL);
            statement = connection.createStatement();
            System.out.println("БД подключена");
            createTable();
            insert("nick1", "login1", "pass1");
            insert("nick2", "login2", "pass2");
            insert("nick3", "login3", "pass3");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            dropTable();
            System.out.println("удалили таблицу");
        }
    }


    public void stop() {
        try {
            if (statement != null) {
                statement.close();
                dropTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getNickByLoginAndPass(String login, String pass) {
        return getNick(login, pass);
    }


    private void createTable() throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS Users (\n" +
                "        id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "        nickname  TEXT,\n" +
                "        login  TEXT,\n" +
                "        password  TEXT\n" +
                "    );");

        System.out.println("создали таблицу");
    }


    private void insert(String nick, String login, String pass) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            String sql = "INSERT INTO Users (nickname, login, password) VALUES(?,?,?) ";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nick);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, pass);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        /*} finally {
            try {
                if(preparedStatement != null){
                preparedStatement.close();
                connection.close();}
            } catch (SQLException e){
                e.printStackTrace();
            }
        }*/
            System.out.println("заполнили");
        }
    }

        /*statement.execute("INSERT INTO Users (nickname, login, password) VALUES ('"+nick+"', '"+login+"', '"+pass+"')");
        System.out.println("заполнили таблицу " + nick + login + pass);
    }*/

    private void read() throws SQLException {
        System.out.println("запускаю метод read");
        try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Users")) {
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) +
                        " " + resultSet.getString("nickname") +
                        " " + resultSet.getString("login") +
                        " " + resultSet.getString("password")
                );
            }
        }
    }

    private void dropTable() {
        String dropSql = "drop table Users";
        try {
            statement.execute(dropSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getNick(String login, String pass) {
        System.out.println("начинаем искать");
        System.out.println(login);
        System.out.println(pass);
        String nickname = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT nickname FROM Users WHERE login = ? AND password = ? ";
         //   connection = DriverManager.getConnection(ChatConstants.DATABASE_URL);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();
            nickname = resultSet.getString("nickname");
            System.out.println(nickname);
         /*  while (resultSet.next()) {
               nickname = resultSet.getString(2);
               //System.out.println(nickname);
           }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null & preparedStatement != null) {
                    resultSet.close();
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }


    public String changeNick(String nick, String newNick) {
        String nickname = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql3 = "SELECT nickname FROM Users ";
            resultSet = statement.executeQuery(sql3);
            while (resultSet.next()) {
                if (!(resultSet.getString("nickname").equals(newNick))) {
                    String sql = "UPDATE Users SET nickname = ?  WHERE nickname = ? ";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, newNick);
                    preparedStatement.setString(2, nick);
                    preparedStatement.execute();
                    System.out.println(getTableInformation());
                    String sql2 = "SELECT nickname FROM Users WHERE nickname = ? ";
                    preparedStatement = connection.prepareStatement(sql2);
                    preparedStatement.setString(1, newNick);
                    resultSet = preparedStatement.executeQuery();
                    nickname = resultSet.getString("nickname");
                } else {
                    System.out.println("Ник занят");
                }
            }
        } catch (SQLException e) {
             e.printStackTrace();
            System.out.println("Что-то пошло не так... Попробуйте еще раз.");
        }
        return nickname;
    }



    private static List<User> getTableInformation() {
        List<User> list = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM Users";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                list.add(new User(
                        resultSet.getString("nickname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return list;
    }
}

