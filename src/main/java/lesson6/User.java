package lesson6;

public class User {
    private String nick;
    private String login;
    private String pass;

    public User(String nick, String login, String pass) {
        this.nick = nick;
        this.login = login;
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", login='" + login + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
