import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Khởi tạo một luồng mới để xử lý client này
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Yêu cầu client nhập username
                writer.println("Enter your username:");
                username = reader.readLine();
                System.out.println(username + " has joined the chat.");

                // Gửi tin nhắn đến tất cả client rằng một người mới đã tham gia
                synchronized (clientWriters) {
                    for (PrintWriter pw : clientWriters) {
                        pw.println(username + " has joined the chat.");
                    }
                }
                clientWriters.add(writer);

                // Xử lý tin nhắn từ client và broadcast đến tất cả client khác
                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println(username + ": " + clientMessage);
                    synchronized (clientWriters) {
                        for (PrintWriter pw : clientWriters) {
                            pw.println(username + ": " + clientMessage);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    System.out.println(username + " has left the chat.");
                    synchronized (clientWriters) {
                        clientWriters.remove(writer);
                        for (PrintWriter pw : clientWriters) {
                            pw.println(username + " has left the chat.");
                        }
                    }
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
