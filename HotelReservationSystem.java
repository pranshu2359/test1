import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "235900";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\nHOTEL RESERVATION SYSTEM");
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> reserveRoom(connection, sc);
                    case 2 -> viewReservations(connection);
                    case 3 -> getRoomNumber(connection, sc);
                    case 4 -> updateReservation(connection, sc);
                    case 5 -> deleteReservation(connection, sc);
                    case 0 -> {
                        exit();
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Wrong choice, try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  Reserve Room
    private static void reserveRoom(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter Guest Name: ");
        String guestName = sc.nextLine();
        System.out.print("Enter Room Number: ");
        int roomNumber = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Contact Number: ");
        String contactNumber = sc.nextLine();

        String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, guestName);
            ps.setInt(2, roomNumber);
            ps.setString(3, contactNumber);

            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation successful!" : "Reservation failed.");
        }
    }

    // View Reservations
    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\nCurrent Reservations:");
            System.out.println("+------------------------------------------------------------------------------------+");
            System.out.printf("| %-14s | %-15s | %-13s | %-20s | %-19s |\n",
                    "Reservation ID", "Guest Name", "Room Number", "Contact Number", "Reservation Date");
            System.out.println("+------------------------------------------------------------------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+------------------------------------------------------------------------------------+");
        }
    }

    // Get Room Number
    private static void getRoomNumber(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter Reservation ID: ");
        int reservationId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Guest Name: ");
        String guestName = sc.nextLine();

        String sql = "SELECT room_number FROM reservations WHERE reservation_id=? AND guest_name=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setString(2, guestName);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and guest " + guestName + " is " + roomNumber);
                } else {
                    System.out.println("Reservation not found.");
                }
            }
        }
    }

    // Update Reservation
    private static void updateReservation(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter Reservation ID to update: ");
        int reservationId = sc.nextInt();
        sc.nextLine();

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation does not exist.");
            return;
        }

        System.out.print("Enter new Guest Name: ");
        String newGuestName = sc.nextLine();
        System.out.print("Enter new Room Number: ");
        int newRoomNumber = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter new Contact Number: ");
        String newContactNumber = sc.nextLine();

        String sql = "UPDATE reservations SET guest_name=?, room_number=?, contact_number=? WHERE reservation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newGuestName);
            ps.setInt(2, newRoomNumber);
            ps.setString(3, newContactNumber);
            ps.setInt(4, reservationId);

            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation updated successfully!" : "Update failed.");
        }
    }

    // Delete Reservation
    private static void deleteReservation(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter Reservation ID to delete: ");
        int reservationId = sc.nextInt();

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        String sql = "DELETE FROM reservations WHERE reservation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);

            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation deleted successfully!" : "Delete failed.");
        }
    }

    // Check if Reservation Exists
    private static boolean reservationExists(Connection connection, int reservationId) throws SQLException {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Exit Method
    public static void exit() throws InterruptedException {
        System.out.println("Exiting system...");
        for (int i = 5; i > 0; i--) {
            System.out.println(".");
            Thread.sleep(500);
        }
        System.out.println("Thank you for using Hotel Reservation System!");
    }
}