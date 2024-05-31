package com.example.interimax.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.example.interimax.models.Offer;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class SavedOffersAdapter extends RecyclerView.Adapter<SavedOffersAdapter.ViewHolder> {

    private List<Offer> savedOffersList;

    public SavedOffersAdapter(List<Offer> savedOffersList) {
        this.savedOffersList = savedOffersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Offer offer = savedOffersList.get(position);
        holder.offerTitle.setText(offer.getJobTitle());
        holder.offerSalary.setText(String.format("%.2f€/h", offer.getSalary())); // Assurez-vous que cette méthode existe dans votre modèle
        holder.offerCompany.setText(offer.getEmployerName()); // Assurez-vous que cette méthode existe dans votre modèle
        GeoPoint coordinate = offer.getCoordinate();
        holder.offerLocation.setText(String.format("%s, %s", coordinate.getLatitude(), coordinate.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return savedOffersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView offerTitle;
        TextView offerSalary;
        TextView offerCompany;
        TextView offerLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            offerTitle = itemView.findViewById(R.id.offer_title);
            offerSalary = itemView.findViewById(R.id.offer_salary);
            offerCompany = itemView.findViewById(R.id.offer_company);
            offerLocation = itemView.findViewById(R.id.offer_location);
        }
    }
}
