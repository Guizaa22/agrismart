package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModifierParcelleController {

    @FXML private TextField codeParcelleField;
    @FXML private TextField superficieField;
    @FXML private TextField typeCultureField;
    @FXML private DatePicker dateCreationField;
    @FXML private TextArea notesArea;
    @FXML private CheckBox statusCheckBox;
    @FXML private Label messageLabel;
    @FXML private Label coordinatesLabel;
    @FXML private Button selectLocationButton;

    private ParcelleService parcelleService;
    private Parcelle parcelle;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @FXML
    public void initialize() {
        parcelleService = new ParcelleService();
        messageLabel.setText("");
        messageLabel.setVisible(false);
        System.out.println("ModifierParcelleController initialized");
    }

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
        this.latitude = parcelle.getLatitude();
        this.longitude = parcelle.getLongitude();
        codeParcelleField.setText(parcelle.getCodeParcelle());
        superficieField.setText(parcelle.getSuperficie().toString());
        typeCultureField.setText(parcelle.getTypeCulture());
        dateCreationField.setValue(parcelle.getDateCreation());
        notesArea.setText(parcelle.getNotes());
        if (statusCheckBox != null) {
            statusCheckBox.setSelected(parcelle.getStatus());
        } else {
            System.err.println("Erreur : statusCheckBox est null dans ModifierParcelleController");
        }
        updateCoordinatesLabel();
        System.out.println("Parcelle loaded with coordinates: lat=" + latitude + ", lng=" + longitude);
    }

    private void updateCoordinatesLabel() {
        if (latitude != null && longitude != null) {
            coordinatesLabel.setText("Coordonnées: (" + latitude + ", " + longitude + ")");
            coordinatesLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
        } else {
            coordinatesLabel.setText("Coordonnées: Non sélectionnées");
            coordinatesLabel.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
        }
    }

    @FXML
    private void openMapWindow() {
        try {
            System.out.println("Opening map window with initial coordinates: lat=" + latitude + ", lng=" + longitude);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FullScreenMap.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            FullScreenMapController controller = loader.getController();
            controller.setInitialCoordinates(latitude, longitude);
            controller.setOnLocationSelected(() -> {
                this.latitude = controller.getLatitude();
                this.longitude = controller.getLongitude();
                System.out.println("Location selected in Modifier: lat=" + this.latitude + ", lng=" + this.longitude);
                updateCoordinatesLabel();
            });

            Stage mapStage = new Stage();
            mapStage.initModality(Modality.APPLICATION_MODAL);
            mapStage.setTitle("Modifier l'Emplacement");
            mapStage.setScene(scene);
            mapStage.showAndWait();
            System.out.println("Map window closed with final coordinates: lat=" + latitude + ", lng=" + longitude);
        } catch (IOException e) {
            messageLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 14px;");
            messageLabel.setText("Erreur lors de l'ouverture de la carte : " + e.getMessage());
            messageLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateParcelle() {
        try {
            System.out.println("Updating parcel with coordinates: lat=" + latitude + ", lng=" + longitude);
            if (parcelle == null) throw new IllegalStateException("Aucune parcelle à modifier.");
            parcelle.setCodeParcelle(codeParcelleField.getText().trim());
            parcelle.setSuperficie(new BigDecimal(superficieField.getText().trim()));
            parcelle.setTypeCulture(typeCultureField.getText().trim());
            parcelle.setDateCreation(dateCreationField.getValue());
            parcelle.setNotes(notesArea.getText().trim());
            if (statusCheckBox != null) {
                parcelle.setStatus(statusCheckBox.isSelected());
            } else {
                throw new IllegalStateException("statusCheckBox est null, impossible de définir le statut.");
            }
            if (latitude != null && longitude != null) {
                parcelle.setLatitude(latitude);
                parcelle.setLongitude(longitude);
            } else {
                throw new IllegalArgumentException("Veuillez sélectionner un emplacement.");
            }

            parcelleService.update(parcelle);
            System.out.println("Parcelle updated: " + parcelle);

            Platform.runLater(() -> {
                messageLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setText("Parcelle modifiée avec succès !");
                messageLabel.setVisible(true);
            });

            java.util.concurrent.CompletableFuture.delayedExecutor(2, java.util.concurrent.TimeUnit.SECONDS).execute(() -> {
                Platform.runLater(this::handleBackToList);
            });

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            Platform.runLater(() -> {
                messageLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setText("Erreur lors de la modification : " + e.getMessage());
                messageLabel.setVisible(true);
            });
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Exception: " + e.getMessage());
            Platform.runLater(() -> {
                messageLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setText("Erreur : " + e.getMessage());
                messageLabel.setVisible(true);
            });
        }
    }

    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeParcelle.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) codeParcelleField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Parcelles");
            stage.show();
        } catch (IOException e) {
            Platform.runLater(() -> {
                messageLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 14px;");
                messageLabel.setText("Erreur lors du chargement de la liste : " + e.getMessage());
                messageLabel.setVisible(true);
            });
        } finally {
            try {
                parcelleService.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}