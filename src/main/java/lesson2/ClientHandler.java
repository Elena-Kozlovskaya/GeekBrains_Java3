package lesson2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Обслуживает клиента(отвечает за связь между клиентом и сервером)
 */
public class ClientHandler {

    private MyServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private volatile boolean authorization;

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
           new Thread(() -> {
               try {
                   System.out.println("сейчас начнется авторизация");
                   authentification();
                   System.out.println("авторизация прошла успешно");
                   readMessages();
                   System.out.println("завершаю чтение ");
           } catch (IOException e){
                   e.printStackTrace();
           } finally {
                   System.out.println("закрываю соединение ");
                   closeConnection();
               }
           }).start();

           // Поток, ожидающий (120 сек.) результат авторизации.
           new Thread(() -> {
               try {
                   System.out.println("поток закрытия сейчас уснет");
                   Thread.sleep(120000);
                   System.out.println("поток закрытия проснулся");
                   if (!authorization) {
                       System.out.println("Вы не успели авторизоваться!");
                       closeConnection();
                       System.out.println("Нафиг с пляжа!");
                   } else {
                       System.out.println("Поздравляем! Вы успели авторизоваться!");
                       return;
                   }
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }).start();

       } catch (IOException ex){
           System.out.println("Проблема при создании клиента");
       }

    }

    private void readMessages() throws IOException {
        while (true){
            System.out.println("Запущено чтение метод readMessages() ClientHandler");
            String messageFromClient = inputStream.readUTF();
            System.out.println("от " + name + ": " + messageFromClient);
            if (messageFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            } else if(messageFromClient.startsWith(ChatConstants.SEND_TO_NICK)){
                String[] splitedStr = messageFromClient.split("\\s+");
                List<String> nicknames = new ArrayList<>();
                for (int i = 1; i < splitedStr.length - 1; i++){
                    nicknames.add(splitedStr[i]);
                    server.broadcastMessageToClient(this, "[" + name + "]: " + messageFromClient, nicknames);
                }
            } else if(messageFromClient.startsWith(ChatConstants.CLIENTS_LIST)){
                server.broadcastClients();
            } else {
                System.out.println("запускаю метод server.broadcastMessage");
                server.broadcastMessage("[" + name + "]: " + messageFromClient); // клиент пишет сообщение, отправляя его на сервер. Сервер отправляет сообщение другим клиентам через socket.
            }
        }
    }

    // /auth login pass
    private void authentification() throws IOException {
        while (true) {
            authorization = false;
            long start = System.currentTimeMillis();
            System.out.println("Запущена авторизация метод authentification() ClientHandler");
            String message = inputStream.readUTF();
            System.out.println("Открыт поток чтения authentification() ClientHandler");
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
                            System.out.println(finish / 1000 + " секунд ушло на авторизацию");
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
