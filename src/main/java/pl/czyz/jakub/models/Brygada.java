package pl.czyz.jakub.models;

import java.util.List;

public class Brygada {
    private String nazwa;
    private Brygadzista brygadzista;
    private List<Pracownik> listaPracownikow;


    public Brygada(String nazwa, Brygadzista brygadzista) {
        this.nazwa = nazwa;
        this.brygadzista = brygadzista;
    }


    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Brygadzista getBrygadzista() {
        return brygadzista;
    }

    public void setBrygadzista(Brygadzista brygadzista) {
        this.brygadzista = brygadzista;
    }

    public List<Pracownik> getListaPracownikow() {
        return listaPracownikow;
    }

    public void addPracownik(Pracownik pracownik) {
        if (pracownik instanceof Uzytkownik) {
            return;
        }

        this.listaPracownikow.add(pracownik);
    }
}
