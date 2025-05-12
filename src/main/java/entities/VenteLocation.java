package entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VenteLocation {
    private int id;
    private int parcelleId;
    private String typeOperation;
    private BigDecimal prix;
    private LocalDate dateOperation;
    private Boolean statut;

    // Constructors
    public VenteLocation() {}

    public VenteLocation(int parcelleId, String typeOperation, BigDecimal prix, LocalDate dateOperation, Boolean statut) {
        this.parcelleId = parcelleId;
        this.typeOperation = typeOperation;
        this.prix = prix;
        this.dateOperation = dateOperation;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getParcelleId() { return parcelleId; }
    public void setParcelleId(int parcelleId) { this.parcelleId = parcelleId; }
    public String getTypeOperation() { return typeOperation; }
    public void setTypeOperation(String typeOperation) { this.typeOperation = typeOperation; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public LocalDate getDateOperation() { return dateOperation; }
    public void setDateOperation(LocalDate dateOperation) { this.dateOperation = dateOperation; }
    public Boolean getStatut() { return statut; }
    public void setStatut(Boolean statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "VenteLocation{id=" + id + ", parcelleId=" + parcelleId + ", typeOperation='" + typeOperation + "', prix=" + prix + ", dateOperation=" + dateOperation + ", statut=" + statut + "}";
    }
}