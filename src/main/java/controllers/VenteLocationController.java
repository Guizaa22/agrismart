package controllers;

import entities.Parcelle;
import entities.VenteLocation;
import javafx.stage.WindowEvent;
import services.ParcelleService;
import services.VenteLocationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class VenteLocationController {

    @FXML private TableView<VenteLocation> venteLocationTable;
    @FXML private ComboBox<Parcelle> parcelleComboBox;
    @FXML private ComboBox<String> typeOperationComboBox;
    @FXML private TextField prixTextField;
    @FXML private Button addButton;
    @FXML private Button modifyButton;
    @FXML private Button deleteButton;

    private VenteLocationService venteLocationService;
    private ParcelleService parcelleService;
    private ObservableList<VenteLocation> venteLocationList = FXCollections.observableArrayList();
    private ObservableList<Parcelle> availableParcelles = FXCollections.observableArrayList();
    private ObservableList<String> typeOperations = FXCollections.observableArrayList("Vente", "Location");

    public VenteLocationController() {
        try {
            venteLocationService = new VenteLocationService();
            parcelleService = new ParcelleService();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de l'initialisation des services : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAvailableParcelles();
        loadVenteLocations();

        // Initialiser les ComboBox
        parcelleComboBox.setItems(availableParcelles);
        typeOperationComboBox.setItems(typeOperations);

        if (!availableParcelles.isEmpty()) {
            parcelleComboBox.getSelectionModel().selectFirst();
        }
        typeOperationComboBox.getSelectionModel().selectFirst();

        // Pré-remplir les champs lorsqu'une ligne est sélectionnée
        venteLocationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                typeOperationComboBox.getSelectionModel().select(newSelection.getTypeOperation().equalsIgnoreCase("vente") ? "Vente" : "Location");
                prixTextField.setText(newSelection.getPrix().toString());
                parcelleComboBox.getSelectionModel().select(availableParcelles.stream()
                        .filter(p -> p.getId() == newSelection.getParcelleId())
                        .findFirst().orElse(null));
            }
        });
    }

    private void setupTableColumns() {
        TableColumn<VenteLocation, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VenteLocation, Integer> parcelleIdCol = new TableColumn<>("Parcelle ID");
        parcelleIdCol.setPrefWidth(100);
        parcelleIdCol.setCellValueFactory(new PropertyValueFactory<>("parcelleId"));

        TableColumn<VenteLocation, String> typeCol = new TableColumn<>("Type Opération");
        typeCol.setPrefWidth(150);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeOperation"));

        TableColumn<VenteLocation, BigDecimal> prixCol = new TableColumn<>("Prix");
        prixCol.setPrefWidth(100);
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));

        TableColumn<VenteLocation, LocalDate> dateCol = new TableColumn<>("Date Opération");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateOperation"));

        TableColumn<VenteLocation, Boolean> statutCol = new TableColumn<>("Statut");
        statutCol.setPrefWidth(100);
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        venteLocationTable.getColumns().setAll(idCol, parcelleIdCol, typeCol, prixCol, dateCol, statutCol);
    }

    private void loadAvailableParcelles() {
        try {
            availableParcelles.setAll(parcelleService.findAvailableParcelles());
            System.out.println("Parcelles disponibles chargées : " + availableParcelles);
            if (availableParcelles.isEmpty()) {
                showAlert("Information", "Aucune parcelle disponible à vendre ou à louer.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des parcelles disponibles : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadVenteLocations() {
        try {
            venteLocationList.setAll(venteLocationService.findAll());
            System.out.println("Chargement des données dans la table : " + venteLocationList);
            venteLocationTable.setItems(venteLocationList);
            if (venteLocationList.isEmpty()) {
                System.out.println("Aucune donnée disponible pour affichage.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd() {
        Parcelle selectedParcelle = parcelleComboBox.getSelectionModel().getSelectedItem();
        String selectedType = typeOperationComboBox.getSelectionModel().getSelectedItem();
        String prixText = prixTextField.getText();

        if (selectedParcelle == null) {
            showAlert("Avertissement", "Veuillez sélectionner une parcelle.", Alert.AlertType.WARNING);
            return;
        }
        if (selectedType == null) {
            showAlert("Avertissement", "Veuillez sélectionner un type d'opération.", Alert.AlertType.WARNING);
            return;
        }
        if (prixText.isEmpty()) {
            showAlert("Avertissement", "Veuillez entrer un prix.", Alert.AlertType.WARNING);
            return;
        }

        try {
            BigDecimal prix = new BigDecimal(prixText);
            if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Avertissement", "Le prix doit être supérieur à zéro.", Alert.AlertType.WARNING);
                return;
            }

            VenteLocation vl = new VenteLocation();
            vl.setParcelleId(selectedParcelle.getId());
            vl.setTypeOperation(selectedType.toLowerCase());
            vl.setPrix(prix);
            vl.setDateOperation(LocalDate.now());
            vl.setStatut(true);
            venteLocationService.save(vl);
            loadVenteLocations();
            showAlert("Succès", "Vente/Location ajoutée avec succès.", Alert.AlertType.INFORMATION);

            // Réinitialiser les champs après l'ajout
            prixTextField.clear();
            typeOperationComboBox.getSelectionModel().selectFirst();
            parcelleComboBox.getSelectionModel().selectFirst();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModify() {
        VenteLocation selected = venteLocationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Avertissement", "Veuillez sélectionner une ligne à modifier.", Alert.AlertType.WARNING);
            return;
        }

        String selectedType = typeOperationComboBox.getSelectionModel().getSelectedItem();
        String prixText = prixTextField.getText();

        if (selectedType == null) {
            showAlert("Avertissement", "Veuillez sélectionner un type d'opération.", Alert.AlertType.WARNING);
            return;
        }
        if (prixText.isEmpty()) {
            showAlert("Avertissement", "Veuillez entrer un prix.", Alert.AlertType.WARNING);
            return;
        }

        try {
            BigDecimal prix = new BigDecimal(prixText);
            if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Avertissement", "Le prix doit être supérieur à zéro.", Alert.AlertType.WARNING);
                return;
            }

            selected.setTypeOperation(selectedType.toLowerCase());
            selected.setPrix(prix);
            // Conserver le statut actuel ou le modifier si nécessaire
            // selected.setStatut(!selected.getStatut()); // Commenté pour ne pas modifier le statut
            venteLocationService.update(selected);
            loadVenteLocations();
            showAlert("Succès", "Vente/Location modifiée avec succès.", Alert.AlertType.INFORMATION);

            // Réinitialiser les champs après la modification
            prixTextField.clear();
            typeOperationComboBox.getSelectionModel().selectFirst();
            parcelleComboBox.getSelectionModel().selectFirst();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        VenteLocation selected = venteLocationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                venteLocationService.delete(selected.getId());
                venteLocationList.remove(selected);
                loadVenteLocations();
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner une ligne à supprimer.", Alert.AlertType.WARNING);
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
        try {
            if (venteLocationService != null) venteLocationService.close();
            if (parcelleService != null) parcelleService.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest((WindowEvent event) -> shutdown());
    }
}