package com.example.interimax.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Offer implements Parcelable {
    private String id;
    private String name;
    private String employerName; // Nom de l'employeur
    private String jobTitle; // Métier cible
    private String description; // Description du poste
    private Integer period; // Période de l'emploi
    private double salary; // Rémunération
    private GeoPoint coordinate;
    private String city;
    private int popularity;

    // Liste statique qui contient toutes les offres
    private static List<Offer> allOffers = new ArrayList<>();
    private static FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Constructeur complet
    public Offer(String id, String name, String employerName, String jobTitle, String description, Integer period, double salary, GeoPoint coordinate, String city, int popularity) {
        this.id = id;
        this.name = name;
        this.employerName = employerName;
        this.jobTitle = jobTitle;
        this.description = description;
        this.period = period;
        this.salary = salary;
        this.coordinate = coordinate;
        this.city = city;
        addOffer(this); // Ajouter automatiquement l'offre à la liste
    }

    public Offer(){

    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    // Ajoute une offre à la liste
    private static void addOffer(Offer offer) {
        database.collection("Job").add(offer).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    DocumentReference doc = task.getResult();
                    offer.setId(doc.getId());
                }else{
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("FirestoreError", "Erreur lors de l'exécution de la requête", e);
                    }
                }
            }
        });
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
                        Offer offer = doc.toObject(Offer.class);
                        offer.setId(doc.getId());
                        offers.add(offer);
                    }
                }else{
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("FirestoreError", "Erreur lors de l'exécution de la requête", e);
                    }
                }
                future.complete(offers);
            }
        });
        return future;
    }

    public static CompletableFuture<List<Offer>> findOffer(Optional<String> name, Optional<String[]> employers, Optional<Integer> salaryFrom, Optional<Integer> salaryTo, Optional<String[]> locations, Optional<Long> limit){
        CompletableFuture<List<Offer>> future = new CompletableFuture<>();
        List<Offer> offers = new ArrayList<>();
        Query query = database.collection("Job");
        if(name.isPresent()){
            query = query.whereEqualTo("jobTitle", name.get());
        }
        if(employers.isPresent()){
            query = query.whereIn("employerName", Arrays.asList(employers.get()));
        }
        if(locations.isPresent()){
            query = query.whereIn("city", Arrays.asList(locations.get()));
        }
        if(limit.isPresent()){
            query = query.limit(limit.get());
        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("document "+doc.getId(), doc.getData().toString());
                        Offer offer = doc.toObject(Offer.class);
                        if(salaryFrom.isPresent() &&
                                salaryTo.isPresent() &&
                                offer.getSalary() >= salaryFrom.get() &&
                                offer.getSalary() <= salaryTo.get()){
                            offer.setId(doc.getId());
                            offers.add(offer);
                        } else if (!salaryFrom.isPresent() && !salaryTo.isPresent()) {
                            offer.setId(doc.getId());
                            offers.add(offer);
                        }
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

    public void setId(String id) {
        this.id = id;
    }

    // Getters

    public String getId() {
        return id;
    }

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

    public String getCity() {
        return city;
    }

    public int getPopularity(){
        return popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.description);
        parcel.writeString(this.employerName);
        parcel.writeString(this.city);
        parcel.writeString(this.jobTitle);
        parcel.writeInt(this.period);
        parcel.writeDouble(this.salary);
        parcel.writeDouble(this.getCoordinate().getLatitude());
        parcel.writeDouble(this.getCoordinate().getLongitude());
        parcel.writeInt(this.popularity);
    }

    public Offer(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.employerName = in.readString();
        this.city = in.readString();
        this.jobTitle = in.readString();
        this.period = in.readInt();
        this.salary = in.readDouble();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        this.coordinate = new GeoPoint(lat,lng);
        this.popularity = in.readInt();
    }
}