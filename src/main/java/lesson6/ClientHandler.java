package lesson6;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Обслуживает клиента(отвечает за связь между клиентом и сервером)
 */
public class ClientHandler {

    private MyServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private volatile boolean authorization;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private String name;

    public String getName() {
        return name;
    }



    public ClientHandler(MyServer server, Socket socket) {
        try{
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.execute(() -> {
                try {
                    LOGGER.debug(ChatConstants.CH + "сейчас начнется авторизация");
                    authentification();
                    LOGGER.debug(ChatConstants.CH + "авторизация прошла успешно");
                    readMessages();
                    LOGGER.debug(ChatConstants.CH + "завершаю чтение ");
                } catch (IOException | SQLException e){
                    e.printStackTrace();
                } finally {
                    LOGGER.info(ChatConstants.CH + "закрываю соединение ");
                    closeConnection();
                    executorService.shutdown();
                }
            });

            // Переделала ожидание авторизации (8 ДЗ 2 уровень). Проверка авторизации каждые 5 сек., закрытие через 120 сек. если не авторизовался.
            executorService.execute(() -> {
                try {
                    long timerStart = System.currentTimeMillis();
                    long timerFinish = 120000;

                    while (!authorization | timerFinish <= 0){
                        LOGGER.debug(ChatConstants.CH + "поток закрытия сейчас уснет");
                        Thread.sleep(5000);
                        LOGGER.debug(ChatConstants.CH + "поток закрытия проснулся");
                        timerFinish = System.currentTimeMillis() - timerStart;
                        System.out.println(timerFinish);
                    }
                    if(authorization){
                        LOGGER.debug(ChatConstants.CH + "Поздравляем! Вы успели авторизоваться!");
                    } else {
                        LOGGER.debug(ChatConstants.CH + "Вы не успели авторизоваться!");
                        LOGGER.debug(ChatConstants.CH + "Нафиг с пляжа!");
                        closeConnection();
                    }
                    /*System.out.println("поток закрытия сейчас уснет");
                    Thread.sleep(120000);
                    System.out.println("поток закрытия проснулся");
                    if (!authorization) {
                    } else {
                     }*/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


        } catch (IOException ex){
            LOGGER.error(ChatConstants.CH + "Проблема при создании клиента");
        }

    }

    private void readMessages() throws IOException {
        while (true) {
            LOGGER.debug(ChatConstants.CH + "Запущено чтение метод readMessages()");
            String messageFromClient = inputStream.readUTF();
            LOGGER.info(ChatConstants.CH + "от " + name + ": " + messageFromClient);
            if (messageFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            } else if (messageFromClient.startsWith(ChatConstants.SEND_TO_NICK)) {
                String[] splitedStr = messageFromClient.split("\\s+");
                List<String> nicknames = new ArrayList<>();
                for (int i = 1; i < splitedStr.length - 1; i++) {
                    nicknames.add(splitedStr[i]);
                    server.broadcastMessageToClient(this, "[" + name + "]: " + messageFromClient, nicknames);
                }
            } else if (messageFromClient.startsWith(ChatConstants.CLIENTS_LIST)) {
                server.broadcastClients();
            } else if (messageFromClient.startsWith(ChatConstants.CHANGE_NICK)) {
                String[] parts = messageFromClient.split("\\s+");
                String nick = server.getAuthService().changeNick(parts[1], parts[2]);
                if (nick != null) {
                    // проверка уникальности ника
                    if (!server.isNickBusy(nick)) {
                        sendMsg(ChatConstants.CHANGE_NICK_OK + " " + nick);
                        name = nick;
                        //server.subscribe(this); // подписка клиента на сервер, чтобы сервер мог отправлять сообщения клиенту
                        server.broadcastMessage(parts[1] + " изменил никнейм на " + name);
                    } else {
                        sendMsg("Ник уже используется");
                    }
                } else {
                    sendMsg("Ник уже используется");
                }
            } else {
                LOGGER.debug(ChatConstants.CH + "запускаю метод server.broadcastMessage");
                server.broadcastMessage("[" + name + "]: " + messageFromClient); // клиент пишет сообщение, отправляя его на сервер. Сервер отправляет сообщение другим клиентам через socket.
            }
        }
    }

    // /auth login pass
    private void authentification() throws IOException, SQLException {
        while (true) {
            authorization = false;
            long start = System.currentTimeMillis();
            LOGGER.debug(ChatConstants.CH + "Запущена авторизация метод authentification()");
            String message = inputStream.readUTF();
            LOGGER.debug(ChatConstants.CH + "Открыт поток чтения authentification()");
            if (message.startsWith(ChatConstants.AUTH_COMMAND)) {
                String[] parts = message.split("\\s+"); // разбиваем строчку по пробелам длинной 3 (регулярное выражение)
                String nick = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                if (nick != null) {
                    // проверка уникальности ника
                    if (!server.isNickBusy(nick)) {
                        sendMsg(ChatConstants.AUTH_OK + " " + nick);
                        name = nick;
                        server.subscribe(this); // подписка клиента на сервер, чтобы сервер мог отправлять сообщения клиенту
                        server.broadcastMessage(name + " вошел в чат");
                        long finish = System.currentTimeMillis() - start;
                        authorization = true;
                        LOGGER.debug(ChatConstants.CH + finish / 1000 + " секунд ушло на авторизацию");
                        return;
                    } else {
                        sendMsg("Ник уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }


    public void sendMsg(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        LOGGER.info(ChatConstants.CH + "Закрываю соединение");
        server.unsubscribe(this);
        server.broadcastMessage(name + " вышел из чата");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}