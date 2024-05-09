import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Nhập username từ client
            System.out.print("Enter your username: ");
            String username = consoleReader.readLine();
            System.out.println("Connected to the server.");

            // Gửi username cho server
            serverWriter.println(username);

            // Tạo luồng để nhận tin nhắn từ server và in ra màn hình
            Thread serverListener = new Thread(new ServerListener(serverReader));
            serverListener.start();

            // Gửi tin nhắn từ client đến server
            String clientMessage;
            while ((clientMessage = consoleReader.readLine()) != null) {
                serverWriter.println(clientMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServerListener implements Runnable {
        private BufferedReader reader;

        public ServerListener(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
