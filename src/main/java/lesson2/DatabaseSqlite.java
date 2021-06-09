package lesson2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseSqlite implements AuthService{

    static Connection connection;
    static Statement statement;

    @Override
    public void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(ChatConstants.DATABASE_URL);
            statement = connection.createStatement();
            System.out.println("БД подключена");
            System.out.println(getTableInformation());
            createTable();
            insert("nick", "login", "pass");
            System.out.println(getTableInformation());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            dropTable();
            System.out.println("удалили таблицу");
        }
    }

    @Override
    public void stop() {
        try {
            dropTable();
            if (statement != null) {
                statement.close();
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

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        return getNick(login, pass);
    }


    private void createTable() throws SQLException {
        connection.setAutoCommit(false);
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
            for(int i = 1; i < 4; i++) {
                preparedStatement.setString(1, nick + i);
                preparedStatement.setString(2, login + i);
                preparedStatement.setString(3, pass + i);
                preparedStatement.addBatch();
            }
            int [] ints = preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            try {
                if(preparedStatement != null){
                preparedStatement.close();
                }
            } catch (SQLException e){
                e.printStackTrace();

            }
        }
        System.out.println("заполнили таблицу");
    }

    private void dropTable() {
        String dropSql = "DROP TABLE Users";
        try {
            statement.execute(dropSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getNick(String login, String pass) {
        String nickname = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection.setAutoCommit(true);
            String sql = "SELECT nickname FROM Users WHERE login = ? AND password = ? ";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();
            nickname = resultSet.getString("nickname");
            System.out.println(nickname);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           try {
               if (resultSet != null & preparedStatement != null) {
                   resultSet.close();
                   preparedStatement.close();
               }
           } catch (SQLException e){
               e.printStackTrace();
           }
       }
        return nickname;
    }

    @Override
    /**
     * Метод изменения никнейма.
     * Если в БД нет ни одного совпадения resultSet != newNick (счетчик равен нулю).
     * return newNickname
     */
    public String changeNick(String nick, String newNick) {
        String newNickname = null;
        int count = 0;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection.setAutoCommit(false);
            String sqlSelect = "SELECT nickname FROM Users ";
            resultSet = statement.executeQuery(sqlSelect);
            while (resultSet.next()) {
                if (resultSet.getString("nickname").equals(newNick)) {
                    count ++;
                }
            }   if (count > 0) {
                    System.out.println("Ник занят");
                    System.out.println(getTableInformation());
                    return newNickname;
                } else {
                    String sqlSet = "UPDATE Users SET nickname = ?  WHERE nickname = ? ";
                    preparedStatement = connection.prepareStatement(sqlSet);
                    preparedStatement.setString(1, newNick);
                    preparedStatement.setString(2, nick);
                    preparedStatement.execute();
                    System.out.println(getTableInformation());
                    String sqlSelectWhere = "SELECT nickname FROM Users WHERE nickname = ? ";
                    preparedStatement = connection.prepareStatement(sqlSelectWhere);
                    preparedStatement.setString(1, newNick);
                    resultSet = preparedStatement.executeQuery();
                    newNickname = resultSet.getString("nickname");
                    connection.commit();
                }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Что-то пошло не так... Попробуйте еще раз.");
            try {
                connection.rollback();
            } catch (SQLException t) {
                t.printStackTrace();
            }
        } finally {
               try {
                   if (resultSet != null & preparedStatement != null) {
                       resultSet.close();
                       preparedStatement.close();
                   }
               } catch (SQLException e){
                   e.printStackTrace();
               }
           }
        return newNickname;

    }

    /**
     * Информация о содержимом таблицы Users.
     * @return List<User>
     */
    private List<User> getTableInformation() {
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

