package controllers;

import entities.Parcelle;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import services.ParcelleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class ListeParcelleController {

    @FXML private VBox parcelleListVBox;
    @FXML private WebView chatWebView;
    @FXML private VBox chatBox;
    @FXML private Button minimizedChatButton;
    @FXML private HBox mainHBox;
    @FXML private ScrollPane parcelleScrollPane;

    private boolean chatVisible = true;

    @FXML
    public void initialize() {
        String iframeUrl = "https://www.chatbase.co/chatbot-iframe/S_laSaCOmnkv6C9oqGcAz";
        chatWebView.getEngine().load(iframeUrl);
        loadParcelles();
    }

    @FXML
    private void handleAddParcelle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutParcelle.fxml"));
            Scene scene = new Scene(loader.load());
            AjoutParcelleController controller = loader.getController();
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Ajouter Nouvelle Parcelle");
            stage.setScene(scene);
            stage.showAndWait();
            loadParcelles();
        } catch (IOException e) {
            System.err.println("Error loading AjoutParcelle: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleShowAnalyse() {
        try {
            Stage stage = (Stage) parcelleListVBox.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AnalyseParcelle.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/listeParcelle.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Analyse des Parcelles");
        } catch (IOException e) {
            System.err.println("Error loading AnalyseParcelle: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre d'analyse: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showListeParcelles() {
        loadParcelles();
    }

    @FXML
    private void showAVendre() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VenteLocation.fxml"));
            VBox root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/venteLocation.css").toExternalForm());
            stage.setTitle("Vente et Location des Parcelles");
            stage.setScene(scene);
            VenteLocationController controller = loader.getController();
            controller.setStage(stage); // Passer le stage au contrôleur
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading VenteLocation: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre Vente/Location: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error opening VenteLocation: " + e.getMessage());
        }
    }

    @FXML
    private void handleIAChat() {
        toggleChat();
    }

    @FXML
    private void toggleChat() {
        chatVisible = !chatVisible;
        if (chatVisible) {
            chatBox.setVisible(true);
            chatBox.setManaged(true);
            minimizedChatButton.setVisible(false);
            minimizedChatButton.setManaged(false);
            if (parcelleScrollPane != null) {
                parcelleScrollPane.setPrefWidth(760.0);
            }
        } else {
            chatBox.setVisible(false);
            chatBox.setManaged(false);
            minimizedChatButton.setVisible(true);
            minimizedChatButton.setManaged(true);
            if (parcelleScrollPane != null) {
                parcelleScrollPane.setPrefWidth(1160.0);
            }
        }
    }

    public void loadParcelles() {
        ParcelleService parcelleService = new ParcelleService();
        try {
            List<Parcelle> parcelles = parcelleService.findAll();
            System.out.println("Loaded " + parcelles.size() + " parcelles: " + parcelles);
            parcelleListVBox.getChildren().clear();
            for (Parcelle parcelle : parcelles) {
                VBox card = createParcelleCard(parcelle);
                parcelleListVBox.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Error loading parcelles: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des parcelles: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                parcelleService.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private VBox createParcelleCard(Parcelle parcelle) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 12; -fx-background-radius: 10;");
        card.setPrefWidth(250);
        card.setAlignment(Pos.CENTER);

        URL imageUrl = getClass().getResource("/images/parcelle.jpg");
        ImageView imageView = new ImageView();
        if (imageUrl != null) {
            imageView.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.err.println("⚠️ Image /images/parcelle.jpg introuvable !");
        }
        imageView.setFitHeight(100);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);

        Label lblCode = new Label("Code: " + parcelle.getCodeParcelle());
        lblCode.getStyleClass().add("card-code");

        Label lblStatus = new Label("Disponible: " + (parcelle.getStatus() ? "Oui" : "Non"));
        lblStatus.getStyleClass().add("card-status");

        Button btnModif = new Button("Modifier");
        btnModif.getStyleClass().add("modify-button");
        btnModif.setOnAction(e -> {
            try {
                showParcelleDetails(parcelle);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la modification: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button btnSupp = new Button("Supprimer");
        btnSupp.getStyleClass().add("delete-button");
        btnSupp.setOnAction(e -> {
            ParcelleService parcelleService = new ParcelleService();
            try {
                parcelleService.delete(parcelle.getCodeParcelle());
                loadParcelles();
                showAlert("Succès", "Parcelle supprimée avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression: " + ex.getMessage(), Alert.AlertType.ERROR);
            } finally {
                try {
                    parcelleService.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox actionButtons = new HBox(10, btnModif, btnSupp);
        actionButtons.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, lblCode, lblStatus, actionButtons);
        card.setOnMouseClicked(event -> showParcelleDetails(parcelle));
        return card;
    }

    private void showParcelleDetails(Parcelle parcelle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParcelleDetails.fxml"));
            Scene scene = new Scene(loader.load());
            ParcelleDetailsController controller = loader.getController();
            controller.setParcelle(parcelle);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Détails Parcelle: " + parcelle.getCodeParcelle());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            loadParcelles();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de détails: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void shutdown() {
        // No longer needed since we're creating fresh instances
    }
}