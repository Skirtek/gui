package pl.czyz.jakub.models;

import java.time.LocalDateTime;
import java.util.List;

public class Zlecenie {
    private List<Praca> listaPrac;
    private Brygada brygada;
    private StanZlecenia stanZlecenia;
    private LocalDateTime dataUtworzenia;
    private LocalDateTime dataRealizacji;
    private LocalDateTime dataZakoczenia;

    public Zlecenie(Brygada brygada, StanZlecenia stanZlecenia) {
        this.brygada = brygada;
        this.stanZlecenia = stanZlecenia;
        this.dataUtworzenia = LocalDateTime.now();
    }

    public List<Praca> getListaPrac() {
        return listaPrac;
    }

    public void setListaPrac(List<Praca> listaPrac) {
        this.listaPrac = listaPrac;
    }

    public Brygada getBrygada() {
        return brygada;
    }

    public void setBrygada(Brygada brygada) {
        this.brygada = brygada;
    }

    public StanZlecenia getStanZlecenia() {
        return stanZlecenia;
    }

    public void setStanZlecenia(StanZlecenia stanZlecenia) {
        this.stanZlecenia = stanZlecenia;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public LocalDateTime getDataRealizacji() {
        return dataRealizacji;
    }

    public void setDataRealizacji(LocalDateTime dataRealizacji) {
        this.dataRealizacji = dataRealizacji;
    }

    public LocalDateTime getDataZakoczenia() {
        return dataZakoczenia;
    }

    public void setDataZakoczenia(LocalDateTime dataZakoczenia) {
        this.dataZakoczenia = dataZakoczenia;
    }
}
