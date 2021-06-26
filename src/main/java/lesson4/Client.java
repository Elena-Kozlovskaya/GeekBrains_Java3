package lesson4;

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
    private DataOutputStream bos;
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
                    String strFromServer = inputStream.readUTF();
                    chatArea.append("\n");
                    if (strFromServer.equals(ChatConstants.AUTH_OK)) {
                        break;
                    }
                    String[] parts = strFromServer.split("\\s+");
                    String fileName = parts[1];
                    file = new File("history_"+fileName+".txt");
                    bos = new DataOutputStream(new FileOutputStream(file, true)); // По замечаниям к 3 ДЗ открытие потока для записи один раз при авторизации.
                    chatArea.append("Сообщение с сервера: " + strFromServer);
                    chatArea.append("\n");

                    chatArea.append("Последние 100 сообщений чата: " + "\n" + readFile());
                    chatArea.append("\n");
                    break;
                }


                //чтение
                while (true) {
                    String strFromServer = inputStream.readUTF();
                    if (strFromServer.equals(ChatConstants.STOP_WORD)) {
                        break;
                    } if(strFromServer.startsWith(ChatConstants.CLIENTS_LIST)) {
                        chatArea.append("Сейчас онлайн " + strFromServer);
                    } else {
                        chatArea.append(strFromServer);
                        bos.writeUTF(strFromServer + "\n"); // запись в файл
                    }
                    chatArea.append("\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if(bos != null){
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public void closeConnection() {
        try {
            if(bos != null) {
                bos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    /* По замечаниям 3 ДЗ, переделала чтение файла. Теперь только один LinkedList.
    С счетчиком терялась одна запись, поэтому перезаписывала во второй LinkedList.
    С переменной maxSize этой проблемы нет.
    */

    private List<String> readFile() {
        List<String> messagesList = new LinkedList<>();
        int maxSize = 101;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) {
            String message;
            while ((message = reader.readLine()) != null) {
                messagesList.add(message);
                if (messagesList.size() == maxSize){
                    messagesList.remove(0);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return messagesList;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

}



