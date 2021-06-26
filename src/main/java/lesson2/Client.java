package lesson2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;



public class Client extends JFrame {

    private Socket socket;

    private JTextArea chatArea;

    private JTextField inputField;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String strFromServer;
    private File file;



    public Client() {

        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initGUI();
    }

    private void openConnection() throws IOException {
        socket = new Socket(ChatConstants.HOST, ChatConstants.PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                //авторизация
                while (true) {
                    strFromServer = inputStream.readUTF();
                    chatArea.append("\n");
                    if (strFromServer.equals(ChatConstants.AUTH_OK)) {
                        break;
                    }
                    String[] parts = strFromServer.split("\\s+");
                    String fileName = parts[1];
                    file = new File("history_"+fileName+".txt");
                    chatArea.append("Сообщение с сервера: " + strFromServer);
                    chatArea.append("\n");

                    chatArea.append("Последние 100 сообщений чата: " + "\n" + readFile());
                    chatArea.append("\n");
                    break;
                }


                //чтение
                while (true) {
                    strFromServer = inputStream.readUTF();
                    if (strFromServer.equals(ChatConstants.STOP_WORD)) {
                        break;
                    } if(strFromServer.startsWith(ChatConstants.CLIENTS_LIST)) {
                        chatArea.append("Сейчас онлайн " + strFromServer);
                    } else {
                        chatArea.append(strFromServer);
                        writeFile(strFromServer + "\n");
                    }
                    chatArea.append("\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }


    public void closeConnection() {
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

    public void initGUI() {
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Message area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        //down pannel
        JPanel panel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        panel.add(inputField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        panel.add(sendButton, BorderLayout.EAST);

        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        inputField.addActionListener(e -> sendMessage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    outputStream.writeUTF(ChatConstants.STOP_WORD);
                    closeConnection();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);

    }

    private void sendMessage() {
        if (!inputField.getText().trim().isEmpty()) {
            try {
                outputStream.writeUTF(inputField.getText());
                inputField.setText("");
                inputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Send error occurred");
            }
        }
    }

    private void writeFile(String strFromServer){
        try(DataOutputStream bos = new DataOutputStream(new FileOutputStream(file, true))) {
            bos.writeUTF(strFromServer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<String> readFile() {
        LinkedList<String> messagesList = new LinkedList<>();
        LinkedList<String> lastHundredMessages = new LinkedList<>();
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            String message;
            while ((message = reader.readLine()) != null) {
                    messagesList.addLast(message);
                }
                while(count < 100) {
                lastHundredMessages.addFirst(messagesList.getLast() + "\n");
                messagesList.removeLast();
                count++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return lastHundredMessages;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

}



