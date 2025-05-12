package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Statistique implements Serializable {

    private int id;
    private Parcelle parcelle;
    private LocalDateTime dateEnregistrement;
    private BigDecimal temperature;
    private BigDecimal humiditeSol;
    private String observations;

    // --- Constructors ---
    public Statistique() {
    }

    public Statistique(Parcelle parcelle, LocalDateTime dateEnregistrement, BigDecimal temperature,
                       BigDecimal humiditeSol, String observations) {
        this.parcelle = parcelle;
        this.dateEnregistrement = dateEnregistrement;
        this.temperature = temperature;
        this.humiditeSol = humiditeSol;
        this.observations = observations;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parcelle getParcelle() {
        return parcelle;
    }

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
    }

    public LocalDateTime getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDateTime dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getHumiditeSol() {
        return humiditeSol;
    }

    public void setHumiditeSol(BigDecimal humiditeSol) {
        this.humiditeSol = humiditeSol;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    // --- Overridden Methods ---
    @Override
    public String toString() {
        return "Statistique{" +
                "id=" + id +
                ", parcelle=" + (parcelle != null ? parcelle.getCodeParcelle() : "null") +
                ", dateEnregistrement=" + dateEnregistrement +
                ", temperature=" + temperature +
                ", humiditeSol=" + humiditeSol +
                ", observations='" + observations + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistique that = (Statistique) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}