package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parcelle implements Serializable {

    private int id;
    private String codeParcelle;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal superficie;
    private String typeCulture;
    private LocalDate dateCreation;
    private String notes;
    private Boolean status;
    private List<Statistique> statistiques = new ArrayList<>();

    // --- Constructors ---
    public Parcelle() {
    }

    public Parcelle(String codeParcelle, BigDecimal longitude, BigDecimal latitude, BigDecimal superficie,
                    String typeCulture, LocalDate dateCreation, String notes, Boolean status) {
        this.codeParcelle = codeParcelle;
        this.longitude = longitude;
        this.latitude = latitude;
        this.superficie = superficie;
        this.typeCulture = typeCulture;
        this.dateCreation = dateCreation;
        this.notes = notes;
        this.status = status;
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeParcelle() {
        return codeParcelle;
    }

    public void setCodeParcelle(String codeParcelle) {
        this.codeParcelle = codeParcelle;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public String getTypeCulture() {
        return typeCulture;
    }

    public void setTypeCulture(String typeCulture) {
        this.typeCulture = typeCulture;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Statistique> getStatistiques() {
        return statistiques;
    }

    public void setStatistiques(List<Statistique> statistiques) {
        this.statistiques = statistiques;
    }

    @Override
    public String toString() {
        return codeParcelle; // Afficher uniquement le codeParcelle dans le ComboBox
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parcelle parcelle = (Parcelle) o;
        return id == parcelle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}