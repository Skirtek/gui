package pl.czyz.jakub.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Brygadzista extends Uzytkownik {
    private List<Brygada> brygady;

    public Brygadzista(String imie, String nazwisko, LocalDate dataUrodzenia, DzialPracownikow dzialPracownikow, String login, String haslo) {
        super(imie, nazwisko, dataUrodzenia, dzialPracownikow, login, haslo);

        brygady = new ArrayList<>();
    }

    public List<Brygada> getBrygady() {
        return brygady;
    }

    public void addBrygada(Brygada brygada) {
        this.brygady.add(brygada);
    }
}
