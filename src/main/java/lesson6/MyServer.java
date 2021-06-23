package lesson6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Непосредственно сервер
 */
public class MyServer {

    private List<ClientHandler> clients;
    private AuthService authService;




    public MyServer() {

        try (ServerSocket server = new ServerSocket(ChatConstants.PORT)){
            authService = new DatabaseSqlite();
            // authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while(true){
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); // Создали нового клиета, добавили в список клиентов
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(authService != null){
                authService.stop();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nick) {
      return clients.stream().anyMatch(client -> client.getName().equals(nick)); // верни true если хоть один элемент соответсвует условию
        /*for(ClientHandler client : clients){
            if(client.getName().equals(nick)){
                return true;
            }
        }
        return false;*/
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClients();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClients();
    }

    /**
     * Отправка сообщений всем пользователям
     * @param message
     */
    public synchronized void broadcastMessage(String message) {
        clients.forEach(c -> c.sendMsg(message));
    }

    public synchronized void broadcastMessageToClient(ClientHandler from, String message, List<String> nicknames) { //распилить меседж
        clients.stream()
                .filter(c -> nicknames.contains(c.getName()))
                .forEach(c -> c.sendMsg(message));
        from.sendMsg(message);


        /*for(ClientHandler client : clients){
            if(nicknames.contains(client.getName())){
                continue;
            }
            client.sendMsg(message);
        }*/
    }

    public synchronized void broadcastClients(){
        // текст сообщения собираем в нужный формат
        String clientsMessage = ChatConstants.CLIENTS_LIST +
                " " +
                clients.stream()
                        .map(c -> c.getName())
                        .collect(Collectors.joining(" "));//клиент проходя через map превращается в client.getName(). На выходе stream[String]. Клиента превращаем в имя и дальше обрабатываем. Через collect соединяем строки через пробел.
        // /client nick1 nick2 nick3
        clients.stream().forEach(c -> c.sendMsg(clientsMessage)); // отправка сообщения
    }
}
