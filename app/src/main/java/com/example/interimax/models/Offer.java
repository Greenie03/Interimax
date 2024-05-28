package com.example.interimax.models;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.interimax.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Offer {
    private String name;
    private String employerName; // Nom de l'employeur
    private String jobTitle; // Métier cible
    private String description; // Description du poste
    private Integer period; // Période de l'emploi
    private double salary; // Rémunération
    private GeoPoint coordinate;

    // Liste statique qui contient toutes les offres
    private static List<Offer> allOffers = new ArrayList<>();
    private static FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Constructeur complet
    public Offer(String name, String employerName, String jobTitle, String description, Integer period, double salary, GeoPoint coordinate) {
        this.name = name;
        this.employerName = employerName;
        this.jobTitle = jobTitle;
        this.description = description;
        this.period = period;
        this.salary = salary;
        this.coordinate = coordinate;
        addOffer(this); // Ajouter automatiquement l'offre à la liste
    }

    public Offer(){

    }

    // Ajoute une offre à la liste
    private static void addOffer(Offer offer) {
        database.collection("Job").add(offer);
    }

    // Retourne la liste des offres
    public static CompletableFuture<List<Offer>> getAllOffers() {
        List<Offer> offers = new ArrayList<>();
        CompletableFuture<List<Offer>> future = new CompletableFuture<>();
        database.collection("Job").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("document "+doc.getId(), doc.getData().toString());
                        offers.add(doc.toObject(Offer.class));
                    }
                }
                future.complete(offers);
            }
        });
        return future;
    }

    public static CompletableFuture<List<Offer>> findOffer(Optional<String> name, Optional<String> employerName, Optional<String> jobTitle, Optional<String> description, Optional<Integer> period, Optional<Double> salary, Optional<GeoPoint> coordinate){
        CompletableFuture<List<Offer>> future = new CompletableFuture<>();
        List<Offer> offers = new ArrayList<>();
        Query query = database.collection("Job");
        if(name.isPresent()){
            query.whereEqualTo("name", name.get());
        }
        if(employerName.isPresent()){
            query.whereEqualTo("employerName", employerName.get());
        }
        if(jobTitle.isPresent()){
            query.whereEqualTo("jobTitle", jobTitle.get());
        }
        if(description.isPresent()){
            query.whereEqualTo("description", description.get());
        }
        if(period.isPresent()){
            query.whereEqualTo("period", period.get());
        }
        if(salary.isPresent()){
            query.whereEqualTo("salary", salary.get());
        }
        if(coordinate.isPresent()){
            query.whereEqualTo("coordinate", coordinate.get());
        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("document "+doc.getId(), doc.getData().toString());
                        offers.add(doc.toObject(Offer.class));
                    }
                }
                future.complete(offers);
            }
        });
        return future;
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

    public GeoPoint getCoordinate() {
        return coordinate;
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
