package org.example.ostheo_projet.model;

public class ConsultationFilters {
    public String searchText;
    public String date;


    public ConsultationFilters(String searchText, String date) {
        this.searchText = searchText;
        this.date = date;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ConsultationFilters{" +
                "searchText='" + searchText + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}