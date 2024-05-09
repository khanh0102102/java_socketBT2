import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class NumberServer {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Khởi tạo một luồng mới để xử lý client này
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                for (int i = 1; i <= 1000; i++) {
                    // Gửi số từ 1 đến 1000 cho client
                    writer.println(i);
                    Thread.sleep(1000); // Đợi 1 giây
                }
                clientSocket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
