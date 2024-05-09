package com.example.interimax.models;

import java.util.ArrayList;
import java.util.List;

public class Offer {
    private String name;
    private String employerName; // Nom de l'employeur
    private String jobTitle; // Métier cible
    private String description; // Description du poste
    private Integer period; // Période de l'emploi
    private double salary; // Rémunération
    private double latitude;
    private double longitude;

    // Liste statique qui contient toutes les offres
    private static List<Offer> allOffers = new ArrayList<>();

    // Constructeur complet
    public Offer(String name, String employerName, String jobTitle, String description, Integer period, double salary, double latitude, double longitude) {
        this.name = name;
        this.employerName = employerName;
        this.jobTitle = jobTitle;
        this.description = description;
        this.period = period;
        this.salary = salary;
        this.latitude = latitude;
        this.longitude = longitude;
        addOffer(this); // Ajouter automatiquement l'offre à la liste
    }

    // Ajoute une offre à la liste
    private static void addOffer(Offer offer) {
        allOffers.add(offer);
    }

    // Retourne la liste des offres
    public static List<Offer> getAllOffers() {
        return allOffers;
    }
    public static List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer(
                "Assistant Administratif - Paris",
                "Agence Intérim Paris Centre",
                "Assistant Administratif",
                "Gestion des appels, traitement de texte, et gestion de dossiers clients.",
                8, // Durée en heures pour une journée complète
                12.50, // Salaire horaire
                48.8566, 2.3522));

        offers.add(new Offer(
                "Ouvrier de Construction - Marseille",
                "BTP Marseille Sud",
                "Ouvrier",
                "Travaux de construction, aide à la maçonnerie et préparation des sites.",
                24, // Durée en heures pour trois jours
                13.00, // Salaire horaire
                43.2965, 5.3698));

        offers.add(new Offer(
                "Serveur en Restauration - Lyon",
                "RestoLyon",
                "Serveur",
                "Service en salle, accueil des clients et préparation des commandes.",
                40, // Durée en heures pour cinq jours
                11.00, // Salaire horaire
                45.7640, 4.8357));

        offers.add(new Offer(
                "Aide-Soignant - Toulouse",
                "Santé Intérim Toulouse",
                "Aide-Soignant",
                "Assistance aux patients, soins de base et maintien de l'hygiène.",
                56, // Durée en heures pour sept jours
                14.00, // Salaire horaire
                43.6045, 1.4442));

        offers.add(new Offer(
                "Préparateur de Commandes - Lille",
                "Logistique Nord",
                "Préparateur de Commandes",
                "Préparation des commandes, gestion des stocks et emballage.",
                16, // Durée en heures pour deux jours
                10.50, // Salaire horaire
                50.6293, 3.0573));

        return offers;
    }



    // Supprimer une offre
    public static boolean removeOffer(Offer offer) {
        return allOffers.remove(offer);
    }

    // Trouver et modifier une offre existante
    public static boolean updateOffer(String oldName, String newName, String newEmployerName, String newJobTitle, String newDescription, Integer newPeriod, double newSalary) {
        for (Offer offer : allOffers) {
            if (offer.getName().equals(oldName)) {
                offer.name = newName;
                offer.employerName = newEmployerName;
                offer.jobTitle = newJobTitle;
                offer.description = newDescription;
                offer.period = newPeriod;
                offer.salary = newSalary;
                return true;
            }
        }
        return false;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmployerName() {
        return employerName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPeriod() {
        return period;
    }

    public double getSalary() {
        return salary;
    }
}
