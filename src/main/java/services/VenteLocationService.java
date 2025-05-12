package services;

import entities.VenteLocation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteLocationService {

    private Connection connection;

    public VenteLocationService() {
        try {
            String url = "jdbc:mysql://localhost:3306/agrismart?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            System.out.println("Database connection established successfully for VenteLocationService.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public void save(VenteLocation vl) throws SQLException {
        String sql = "INSERT INTO vente_location (parcelle_id, type_operation, prix, date_operation, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, vl.getParcelleId());
            pstmt.setString(2, vl.getTypeOperation());
            pstmt.setBigDecimal(3, vl.getPrix());
            pstmt.setDate(4, java.sql.Date.valueOf(vl.getDateOperation()));
            pstmt.setBoolean(5, vl.getStatut());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Inserted VenteLocation, rows affected: " + rowsAffected);
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    vl.setId(rs.getInt(1));
                    System.out.println("Generated ID for new VenteLocation: " + vl.getId());
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during save: " + e.getMessage());
            throw e;
        }
    }

    public List<VenteLocation> findAll() throws SQLException {
        List<VenteLocation> venteLocations = new ArrayList<>();
        String sql = "SELECT vl.* FROM vente_location vl " +
                "JOIN parcelle p ON vl.parcelle_id = p.id " +
                "WHERE p.status = TRUE";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VenteLocation vl = new VenteLocation();
                vl.setId(rs.getInt("id"));
                vl.setParcelleId(rs.getInt("parcelle_id"));
                vl.setTypeOperation(rs.getString("type_operation"));
                vl.setPrix(rs.getBigDecimal("prix"));
                vl.setDateOperation(rs.getDate("date_operation").toLocalDate());
                // Gestion robuste pour statut
                String statutStr = rs.getString("statut");
                vl.setStatut(statutStr != null && (statutStr.equalsIgnoreCase("true") || statutStr.equalsIgnoreCase("1")));
                venteLocations.add(vl);
            }
            System.out.println("VenteLocations récupérées de la base : " + venteLocations);
        } catch (SQLException e) {
            System.err.println("Error during findAll: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return venteLocations;
    }

    public void update(VenteLocation vl) throws SQLException {
        String sql = "UPDATE vente_location SET parcelle_id = ?, type_operation = ?, prix = ?, date_operation = ?, statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, vl.getParcelleId());
            pstmt.setString(2, vl.getTypeOperation());
            pstmt.setBigDecimal(3, vl.getPrix());
            pstmt.setDate(4, java.sql.Date.valueOf(vl.getDateOperation()));
            pstmt.setBoolean(5, vl.getStatut());
            pstmt.setInt(6, vl.getId());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated VenteLocation, rows affected: " + rowsAffected);
            if (rowsAffected == 0) {
                throw new SQLException("Aucune VenteLocation trouvée avec l'ID " + vl.getId());
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during update: " + e.getMessage());
            throw e;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vente_location WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted VenteLocation, rows affected: " + rowsAffected);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during delete: " + e.getMessage());
            throw e;
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed for VenteLocationService.");
        }
    }
}