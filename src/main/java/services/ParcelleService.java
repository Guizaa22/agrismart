package services;

import entities.Parcelle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelleService {

    private Connection connection;

    public ParcelleService() {
        try {
            String url = "jdbc:mysql://localhost:3306/agrismart?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false); // Disable auto-commit
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public void create(Parcelle parcelle) throws SQLException {
        String sql = "INSERT INTO parcelle (code_parcelle, type_culture, superficie, longitude, latitude, date_creation, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, parcelle.getCodeParcelle());
            pstmt.setString(2, parcelle.getTypeCulture());
            pstmt.setBigDecimal(3, parcelle.getSuperficie());
            pstmt.setBigDecimal(4, parcelle.getLongitude());
            pstmt.setBigDecimal(5, parcelle.getLatitude());
            pstmt.setDate(6, java.sql.Date.valueOf(parcelle.getDateCreation()));
            pstmt.setString(7, parcelle.getNotes());
            pstmt.setBoolean(8, parcelle.getStatus());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Inserted parcelle, rows affected: " + rowsAffected);
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    parcelle.setId(rs.getInt(1));
                    System.out.println("Generated ID for new parcelle: " + parcelle.getId());
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during create: " + e.getMessage());
            throw e;
        }
    }

    public List<Parcelle> findAll() throws SQLException {
        List<Parcelle> parcelles = new ArrayList<>();
        String sql = "SELECT * FROM parcelle";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Parcelle parcelle = new Parcelle();
                parcelle.setId(rs.getInt("id"));
                parcelle.setCodeParcelle(rs.getString("code_parcelle"));
                parcelle.setTypeCulture(rs.getString("type_culture"));
                parcelle.setSuperficie(rs.getBigDecimal("superficie"));
                parcelle.setLongitude(rs.getBigDecimal("longitude"));
                parcelle.setLatitude(rs.getBigDecimal("latitude"));
                parcelle.setDateCreation(rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null);
                parcelle.setNotes(rs.getString("notes"));
                parcelle.setStatus(rs.getBoolean("status"));
                parcelles.add(parcelle);
            }
            System.out.println("Parcelles récupérées de la base : " + parcelles);
        } catch (SQLException e) {
            System.err.println("Error during findAll: " + e.getMessage());
            throw e;
        }
        return parcelles;
    }

    public Parcelle findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM parcelle WHERE code_parcelle = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Parcelle parcelle = new Parcelle();
                    parcelle.setId(rs.getInt("id"));
                    parcelle.setCodeParcelle(rs.getString("code_parcelle"));
                    parcelle.setLongitude(rs.getBigDecimal("longitude"));
                    parcelle.setLatitude(rs.getBigDecimal("latitude"));
                    parcelle.setSuperficie(rs.getBigDecimal("superficie"));
                    parcelle.setTypeCulture(rs.getString("type_culture"));
                    parcelle.setDateCreation(rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null);
                    parcelle.setNotes(rs.getString("notes"));
                    parcelle.setStatus(rs.getBoolean("status"));
                    return parcelle;
                }
            }
            return null;
        }
    }

    public void update(Parcelle parcelle) throws SQLException {
        String sql = "UPDATE parcelle SET type_culture = ?, superficie = ?, longitude = ?, latitude = ?, date_creation = ?, notes = ?, status = ? WHERE code_parcelle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parcelle.getTypeCulture());
            pstmt.setBigDecimal(2, parcelle.getSuperficie());
            pstmt.setBigDecimal(3, parcelle.getLongitude());
            pstmt.setBigDecimal(4, parcelle.getLatitude());
            pstmt.setDate(5, java.sql.Date.valueOf(parcelle.getDateCreation()));
            pstmt.setString(6, parcelle.getNotes());
            pstmt.setBoolean(7, parcelle.getStatus());
            pstmt.setString(8, parcelle.getCodeParcelle());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated parcelle, rows affected: " + rowsAffected);
            if (rowsAffected == 0) {
                throw new SQLException("Aucune parcelle trouvée avec le code " + parcelle.getCodeParcelle());
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during update: " + e.getMessage());
            throw e;
        }
    }

    public void delete(String codeParcelle) throws SQLException {
        String sql = "DELETE FROM parcelle WHERE code_parcelle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codeParcelle);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted parcelle, rows affected: " + rowsAffected);
            if (rowsAffected > 0) {
                connection.commit();
            } else {
                throw new SQLException("Aucune parcelle trouvée avec code_parcelle: " + codeParcelle);
            }
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Error during delete: " + e.getMessage());
            throw e;
        }
    }

    public List<Parcelle> findAvailableParcelles() throws SQLException {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT * FROM parcelle WHERE status = TRUE";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Parcelle p = new Parcelle();
                p.setId(rs.getInt("id"));
                p.setCodeParcelle(rs.getString("code_parcelle"));
                p.setTypeCulture(rs.getString("type_culture"));
                p.setSuperficie(rs.getBigDecimal("superficie"));
                p.setLongitude(rs.getBigDecimal("longitude"));
                p.setLatitude(rs.getBigDecimal("latitude"));
                p.setDateCreation(rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null);
                p.setNotes(rs.getString("notes"));
                p.setStatus(rs.getBoolean("status"));
                parcelles.add(p);
            }
            System.out.println("Parcelles disponibles chargées : " + parcelles);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des parcelles disponibles : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return parcelles;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }
}