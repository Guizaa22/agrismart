package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.math.BigDecimal;

public class FullScreenMapController {

    @FXML private WebView mapWebView;
    @FXML private Label coordinatesLabel;
    @FXML private Button confirmButton;

    private WebEngine webEngine;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isMapLoaded = false;
    private Runnable onLocationSelected;

    @FXML
    public void initialize() {
        initializeMap();
        latitude = null;
        longitude = null;
        coordinatesLabel.setText("Coordonnées: Non sélectionnées");
        coordinatesLabel.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
        confirmButton.setDisable(true);
    }

    public void setOnLocationSelected(Runnable onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setInitialCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        System.out.println("Setting initial coordinates to lat: " + latitude + ", lng: " + longitude);
        if (isMapLoaded && webEngine != null) {
            try {
                if (latitude != null && longitude != null) {
                    webEngine.executeScript("setMarker(" + latitude.doubleValue() + ", " + longitude.doubleValue() + ");");
                    coordinatesLabel.setText("Coordonnées: (" + latitude + ", " + longitude + ")");
                    coordinatesLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
                    confirmButton.setDisable(false);
                } else {
                    webEngine.executeScript("clearMarker();");
                    coordinatesLabel.setText("Coordonnées: Non sélectionnées");
                    coordinatesLabel.setStyle("-fx-text-fill: #34495E; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
                    confirmButton.setDisable(true);
                }
            } catch (Exception e) {
                System.err.println("Error setting initial marker: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initializeMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Full Screen Map</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <style>
                    html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <script>
                    var map;
                    var marker;
                    function initializeMap() {
                        console.log("Initializing full-screen map...");
                        if (!document.getElementById('map')) {
                            console.error("Map container not found!");
                            return;
                        }
                        map = L.map('map').setView([0, 0], 2);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© OpenStreetMap contributors'
                        }).addTo(map);
                        map.on('click', function(e) {
                            console.log("Map clicked at lat: " + e.latlng.lat + ", lng: " + e.latlng.lng);
                            if (marker) map.removeLayer(marker);
                            marker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);
                            if (window.javaBridge) {
                                console.log("Calling javaBridge.setCoordinates with lat: " + e.latlng.lat + ", lng: " + e.latlng.lng);
                                window.javaBridge.setCoordinates(e.latlng.lat, e.latlng.lng);
                            } else {
                                console.error("JavaBridge not found!");
                            }
                        });
                        console.log("Full-screen map initialized successfully.");
                    }
                    function setMarker(lat, lng) {
                        console.log("Setting marker at lat: " + lat + ", lng: " + lng);
                        if (marker) map.removeLayer(marker);
                        marker = L.marker([lat, lng]).addTo(map);
                        map.setView([lat, lng], 10);
                    }
                    function clearMarker() {
                        console.log("Clearing marker");
                        if (marker) {
                            map.removeLayer(marker);
                            marker = null;
                        }
                    }
                    document.addEventListener('DOMContentLoaded', initializeMap);
                </script>
            </body>
            </html>
            """;
        webEngine.loadContent(htmlContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                try {
                    System.out.println("Map page loaded successfully");
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    JavaBridge javaBridge = new JavaBridge();
                    window.setMember("javaBridge", javaBridge);
                    Object bridgeCheck = webEngine.executeScript("window.javaBridge != null ? 'JavaBridge set' : 'JavaBridge not set'");
                    System.out.println("JavaBridge status: " + bridgeCheck);
                    isMapLoaded = true;
                    System.out.println("Full-screen map loaded successfully.");
                    if (latitude != null && longitude != null) {
                        webEngine.executeScript("setMarker(" + latitude.doubleValue() + ", " + longitude.doubleValue() + ");");
                    } else {
                        webEngine.executeScript("clearMarker();");
                    }
                } catch (Exception e) {
                    System.err.println("Error setting JavaBridge: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.err.println("Full-screen map loading failed. Check internet connection or console for details.");
                webEngine.executeScript("console.log('Map loading failed: ' + document.body.innerHTML);");
            }
        });
    }

    public class JavaBridge {
        @SuppressWarnings("unused")
        public void setCoordinates(double lat, double lng) {
            System.out.println("JavaBridge.setCoordinates called with lat: " + lat + ", lng: " + lng);
            latitude = BigDecimal.valueOf(lat);
            longitude = BigDecimal.valueOf(lng);
            Platform.runLater(() -> {
                coordinatesLabel.setText("Coordonnées: (" + latitude + ", " + longitude + ")");
                coordinatesLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
                confirmButton.setDisable(false);
            });
        }
    }

    @FXML
    private void handleConfirmLocation() {
        System.out.println("Confirming location with lat: " + latitude + ", lng: " + longitude);
        if (latitude != null && longitude != null && onLocationSelected != null) {
            onLocationSelected.run();
        } else {
            coordinatesLabel.setText("Veuillez sélectionner un emplacement sur la carte.");
            coordinatesLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: #FFFFFF; -fx-padding: 8; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
        }
        Stage stage = (Stage) mapWebView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancelling map selection");
        Stage stage = (Stage) mapWebView.getScene().getWindow();
        stage.close();
    }
}