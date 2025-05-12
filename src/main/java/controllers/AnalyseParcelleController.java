package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class AnalyseParcelleController {

    @FXML private TableView<Parcelle> parcelleTable;
    @FXML private TableColumn<Parcelle, String> codeParcelleColumn;
    @FXML private TableColumn<Parcelle, String> typeCultureColumn;
    @FXML private TableColumn<Parcelle, String> superficieColumn;
    @FXML private TableColumn<Parcelle, String> longitudeColumn;
    @FXML private TableColumn<Parcelle, String> latitudeColumn;
    @FXML private TableColumn<Parcelle, Void> actionColumn;
    @FXML private Button backButton;

    private ParcelleService parcelleService;

    @FXML
    public void initialize() {
        parcelleService = new ParcelleService();

        codeParcelleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodeParcelle()));
        typeCultureColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeCulture()));
        superficieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSuperficie().toString()));
        longitudeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLongitude().toString()));
        latitudeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLatitude().toString()));

        actionColumn.setCellFactory(param -> new TableCell<Parcelle, Void>() {
            private final Button analyseButton = new Button("Analyse");

            {
                analyseButton.setOnAction(event -> {
                    Parcelle parcelle = getTableView().getItems().get(getIndex());
                    handleAnalyse(parcelle);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(analyseButton);
                }
            }
        });

        loadParcelles();
    }

    private void loadParcelles() {
        try {
            parcelleTable.getItems().setAll(parcelleService.findAll());
        } catch (Exception e) {
            System.err.println("Error loading parcelles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAnalyse(Parcelle parcelle) {
        System.out.println("Analyse clicked for parcelle: " + parcelle.getCodeParcelle());
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeParcelle.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/listeParcelle.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Liste des Parcelles");
        } catch (IOException e) {
            System.err.println("Error loading ListeParcelle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}