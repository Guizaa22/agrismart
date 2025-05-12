package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class ParcelleDetailsController {

    @FXML private TextField codeParcelleField;
    @FXML private TextField typeCultureField;
    @FXML private TextField superficieField;
    @FXML private TextField longitudeField;
    @FXML private TextField latitudeField;
    @FXML private TextField dateCreationField;
    @FXML private TextField notesField;
    @FXML private CheckBox statusCheckBox;

    private Parcelle parcelle;
    private ListeParcelleController parentController;

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
        codeParcelleField.setText(parcelle.getCodeParcelle());
        typeCultureField.setText(parcelle.getTypeCulture());
        superficieField.setText(parcelle.getSuperficie().toString());
        longitudeField.setText(parcelle.getLongitude().toString());
        latitudeField.setText(parcelle.getLatitude().toString());
        dateCreationField.setText(parcelle.getDateCreation().toString());
        notesField.setText(parcelle.getNotes());
        statusCheckBox.setSelected(parcelle.getStatus());
    }

    public void setParentController(ListeParcelleController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleModify() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierParcelle.fxml"));
            Scene scene = new Scene(loader.load());
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(parcelle);
            Stage stage = new Stage();
            stage.setTitle("Modifier Parcelle: " + parcelle.getCodeParcelle());
            stage.setScene(scene);
            stage.showAndWait();

            // Rafraîchir les détails après modification
            setParcelle(parcelle);
            if (parentController != null) {
                parentController.loadParcelles();
            }
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la Suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la parcelle " + parcelle.getCodeParcelle() + " ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            ParcelleService parcelleService = new ParcelleService();
            try {
                parcelleService.delete(parcelle.getCodeParcelle());
                showAlert("Succès", "Parcelle supprimée avec succès", Alert.AlertType.INFORMATION);
                if (parentController != null) {
                    parentController.loadParcelles();
                }
                Stage stage = (Stage) codeParcelleField.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                try {
                    parcelleService.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}