package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjoutParcelleController {

    @FXML private TextField codeParcelleField;
    @FXML private TextField typeCultureField;
    @FXML private TextField superficieField;
    @FXML private CheckBox statusCheckBox;
    @FXML private TextArea notesArea;

    private BigDecimal latitude = null;
    private BigDecimal longitude = null;

    private Parcelle parcelle;
    private ListeParcelleController parentController;

    @FXML
    public void initialize() {
        parcelle = new Parcelle();
    }

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
        if (parcelle != null) {
            codeParcelleField.setText(parcelle.getCodeParcelle());
            codeParcelleField.setDisable(true);
            typeCultureField.setText(parcelle.getTypeCulture());
            superficieField.setText(parcelle.getSuperficie().toString());
            statusCheckBox.setSelected(parcelle.getStatus());
            notesArea.setText(parcelle.getNotes());
            longitude = parcelle.getLongitude();
            latitude = parcelle.getLatitude();
        }
    }

    public void setParentController(ListeParcelleController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleAdd() {
        ParcelleService parcelleService = new ParcelleService();
        try {
            if (codeParcelleField.getText().isEmpty() || typeCultureField.getText().isEmpty()) {
                showAlert("Champs requis", "Code et type de culture sont obligatoires", Alert.AlertType.WARNING);
                return;
            }

            if (longitude == null || latitude == null) {
                showAlert("Coordonnées manquantes", "Veuillez choisir un emplacement via la carte", Alert.AlertType.WARNING);
                return;
            }

            BigDecimal superficie = superficieField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(superficieField.getText());

            Parcelle updated = (parcelle != null && parcelle.getId() > 0) ? parcelle : new Parcelle();
            updated.setCodeParcelle(codeParcelleField.getText());
            updated.setTypeCulture(typeCultureField.getText());
            updated.setSuperficie(superficie);
            updated.setLongitude(longitude);
            updated.setLatitude(latitude);
            updated.setDateCreation(LocalDate.now());
            updated.setStatus(statusCheckBox.isSelected());
            updated.setNotes(notesArea.getText());

            if (parcelle != null && parcelle.getId() > 0) {
                parcelleService.update(updated);
                System.out.println("Parcelle mise à jour : " + updated);
            } else {
                parcelleService.create(updated);
                System.out.println("Parcelle créée : " + updated);
                Parcelle addedParcelle = parcelleService.findByCode(updated.getCodeParcelle());
                if (addedParcelle != null) {
                    System.out.println("Parcelle bien ajoutée dans la base : " + addedParcelle);
                } else {
                    System.out.println("Erreur : la parcelle n'a pas été trouvée dans la base après l'ajout !");
                }
            }

            showAlert("Succès", "Parcelle enregistrée", Alert.AlertType.INFORMATION);
            if (parentController != null) {
                parentController.loadParcelles();
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Superficie invalide", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur BD", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            try {
                parcelleService.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleOpenMapWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FullScreenMap.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Choisir Emplacement");
            stage.setScene(scene);
            stage.showAndWait();

            FullScreenMapController controller = loader.getController();
            this.longitude = controller.getLongitude();
            this.latitude = controller.getLatitude();

            if (longitude != null && latitude != null) {
                // Optionally update UI to show coordinates
            } else {
                showAlert("Aucune coordonnée", "Veuillez sélectionner un point sur la carte.", Alert.AlertType.WARNING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Chargement de la carte impossible", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) codeParcelleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}