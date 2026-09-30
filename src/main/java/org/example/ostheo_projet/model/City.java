package org.example.ostheo_projet.model;

import jakarta.persistence.*;

/**
 * Entity class representing a city.
 * Maps to the "city" table in the database.
 */
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;


    /**
     * Default constructor for City.
     */
    public City() {}

    /**
     * Constructs a new City with the specified parameters.
     *
     * @param id the unique identifier of the city
     * @param name the name of the city
     * @param postalCode the postal code of the city
     * @param departement the department where the city is located
     */
    public City(int id, String name, String postalCode, Departement departement) {
        this.id = id;
        this.name = name;
        this.postalCode = postalCode;
        this.departement = departement;
    }

    /**
     * Gets the unique identifier of the city.
     *
     * @return the city's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the city.
     *
     * @param id the city's ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the city.
     *
     * @return the city's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the city.
     *
     * @param name the city's name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the postal code of the city.
     *
     * @return the city's postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code of the city.
     *
     * @param postalCode the city's postal code to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    /**
     * Returns a string representation of the city.
     *
     * @return a string containing all city attributes
     */
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", departement='" + departement + '\'' +
                '}';
    }
}
