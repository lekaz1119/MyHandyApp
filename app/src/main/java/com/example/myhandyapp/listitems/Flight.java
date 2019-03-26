package com.example.myhandyapp.listitems;


// Flight class, records a message String and messageType(send or recieve)
public class Flight extends ListItem {

    private long id;
    private String airline;
    private String airport;

    public Flight() {

    }

    public Flight(String airport, String airline) {
        this.airport=airport;
        this.airline=airline;
    }

    public String getAirline() {
        return airline;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}