package services;

import utils.DBConnection;
import entities.Parcelle;
import entities.Statistique;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatistiqueService {

    // Create (Insert a new Statistique)
    public void create(Statistique statistique) throws SQLException {
        String sql = "INSERT INTO statistique (parcelle_id, date_enregistrement, temperature, humidite_sol, observations) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, statistique.getParcelle().getId());
            stmt.setObject(2, statistique.getDateEnregistrement());
            stmt.setBigDecimal(3, statistique.getTemperature());
            stmt.setBigDecimal(4, statistique.getHumiditeSol());
            stmt.setString(5, statistique.getObservations());
            stmt.executeUpdate();

            // Retrieve the generated statistique_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                statistique.setId(rs.getInt(1));
            }
        }
    }

    // Read (Find a Statistique by ID)
    public Statistique findById(int id) throws SQLException {
        String sql = "SELECT s.*, p.parcelle_id, p.code_parcelle, p.longitude, p.latitude, p.superficie, " +
                "p.type_culture, p.date_creation, p.notes, p.status " +
                "FROM statistique s " +
                "LEFT JOIN parcelle p ON s.parcelle_id = p.parcelle_id " +
                "WHERE s.statistique_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Parcelle parcelle = new Parcelle(
                        rs.getString("code_parcelle"),
                        rs.getBigDecimal("longitude"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("superficie"),
                        rs.getString("type_culture"),
                        rs.getObject("date_creation", LocalDate.class),
                        rs.getString("notes"),
                        rs.getBoolean("status") // Use single status field
                );
                parcelle.setId(rs.getInt("parcelle_id"));

                Statistique statistique = new Statistique(
                        parcelle,
                        rs.getObject("date_enregistrement", LocalDateTime.class),
                        rs.getBigDecimal("temperature"),
                        rs.getBigDecimal("humidite_sol"),
                        rs.getString("observations")
                );
                statistique.setId(rs.getInt("statistique_id"));
                return statistique;
            }
            return null;
        }
    }

    // Read (Find all Statistiques)
    public List<Statistique> findAll() throws SQLException {
        String sql = "SELECT s.*, p.parcelle_id, p.code_parcelle, p.longitude, p.latitude, p.superficie, " +
                "p.type_culture, p.date_creation, p.notes, p.status " +
                "FROM statistique s " +
                "LEFT JOIN parcelle p ON s.parcelle_id = p.parcelle_id";
        List<Statistique> statistiques = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Parcelle parcelle = new Parcelle(
                        rs.getString("code_parcelle"),
                        rs.getBigDecimal("longitude"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("superficie"),
                        rs.getString("type_culture"),
                        rs.getObject("date_creation", LocalDate.class),
                        rs.getString("notes"),
                        rs.getBoolean("status") // Use single status field
                );
                parcelle.setId(rs.getInt("parcelle_id"));

                Statistique statistique = new Statistique(
                        parcelle,
                        rs.getObject("date_enregistrement", LocalDateTime.class),
                        rs.getBigDecimal("temperature"),
                        rs.getBigDecimal("humidite_sol"),
                        rs.getString("observations")
                );
                statistique.setId(rs.getInt("statistique_id"));
                statistiques.add(statistique);
            }
            return statistiques;
        }
    }

    // Update (Update an existing Statistique)
    public void update(Statistique statistique) throws SQLException {
        String sql = "UPDATE statistique SET parcelle_id = ?, date_enregistrement = ?, temperature = ?, " +
                "humidite_sol = ?, observations = ? WHERE statistique_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, statistique.getParcelle().getId());
            stmt.setObject(2, statistique.getDateEnregistrement());
            stmt.setBigDecimal(3, statistique.getTemperature());
            stmt.setBigDecimal(4, statistique.getHumiditeSol());
            stmt.setString(5, statistique.getObservations());
            stmt.setInt(6, statistique.getId());
            stmt.executeUpdate();
        }
    }

    // Delete (Delete a Statistique by ID)
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM statistique WHERE statistique_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}