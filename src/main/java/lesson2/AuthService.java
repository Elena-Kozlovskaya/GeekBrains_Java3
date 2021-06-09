package lesson2;

/**
 * Сервис авторизации
 */
public interface AuthService {
    /**
     * Запустить сервис
     */
    void start();

    /**
     * Остановить сервис
     */
    void stop();

    /**
     * Получить никнейм
     */
    String getNickByLoginAndPass(String login, String pass);

    /**
     * Изменить никнейм
     */
    String changeNick(String nick, String newNick);
}
