import java.nio.file.*;
import java.sql.*;
import java.util.concurrent.*;

public class FileCleanupTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Checking for files to delete...");
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "system")) {
            // Query to find files older than 60 seconds
            String query = "SELECT filename FROM downloads WHERE download_time < NOW() - INTERVAL 60 SECOND";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String fileName = rs.getString("filename");
                System.out.println("File to be checked for deletion: " + fileName);
                
                deleteFile(fileName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String fileName) {
        // Check if file exists in edgeserver1
        Path fileInServer1 = Paths.get("D:\\edgeserver1", fileName);
        if (Files.exists(fileInServer1)) {
            try {
                Files.delete(fileInServer1);
                System.out.println("Deleted file from edgeserver1: " + fileInServer1);
            } catch (Exception e) {
                System.out.println("Error deleting file from edgeserver1: " + e.getMessage());
            }
        }

        // Check if file exists in edgeserver2
        Path fileInServer2 = Paths.get("D:\\edgeserver2", fileName);
        if (Files.exists(fileInServer2)) {
            try {
                Files.delete(fileInServer2);
                System.out.println("Deleted file from edgeserver2: " + fileInServer2);
            } catch (Exception e) {
                System.out.println("Error deleting file from edgeserver2: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new FileCleanupTask(), 0, 10, TimeUnit.SECONDS);
    }
}
