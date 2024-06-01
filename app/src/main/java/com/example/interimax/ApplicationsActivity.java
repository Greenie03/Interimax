package com.example.interimax;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.adapters.ActiveApplicationAdapter;
import com.example.interimax.adapters.OfferAdapter;
import com.example.interimax.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationsActivity extends AppCompatActivity implements ActiveApplicationAdapter.OnClickListener {

    FirebaseFirestore db;
    FirebaseAuth auth;
    List<Map<String, Object>> obj;
    RecyclerView listResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        listResult = findViewById(R.id.list);

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db.collection("candidature").whereEqualTo("userId", auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Map<String, Object>> candidatures = new HashMap<>();
                    List<String> offersIds = new ArrayList<>();
                    List<Offer> offers = new ArrayList<>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("candidatures", doc.getData().toString());
                        offersIds.add((String) doc.getData().get("offer"));
                        candidatures.put((String) doc.getData().get("offer"), doc.getData());
                    }
                    db.collection("Job").whereIn(FieldPath.documentId(),offersIds).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            for(QueryDocumentSnapshot doc : task2.getResult()){
                                offers.add(doc.toObject(Offer.class));
                                candidatures.get(doc.getId()).putAll(doc.getData());
                            }
                            //afficher les candidatures
                            runOnUiThread(() -> {
                                obj = new ArrayList<>(candidatures.values());
                                updateList(obj);
                                TextView total = findViewById(R.id.total);
                                String totalText = "Vous avez " + candidatures.size() + " candidatures actives";
                                total.setText(totalText);

                                RadioButton all = findViewById(R.id.all);
                                RadioButton selection = findViewById(R.id.selection);
                                RadioButton refuse = findViewById(R.id.refuse);

                                RadioGroup radioButton = findViewById(R.id.radio_group);
                                radioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                        RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                                        if (radioButton.equals(all)) {
                                            updateList(obj);
                                        } else if (radioButton.equals(selection)) {
                                            List<Map<String, Object>> list = obj.stream().filter(c -> (long) c.get("status") == 1).collect(Collectors.toList());
                                            updateList(list);
                                        }else if (radioButton.equals(refuse)) {
                                            List<Map<String, Object>> list = obj.stream().filter(c -> (long) c.get("status") == 2).collect(Collectors.toList());
                                            updateList(list);
                                        }
                                    }
                                });

                            });

                        }
                    });
                }
            }
        });

    }

    private void updateList(List<Map<String, Object>> list){
        listResult.setLayoutManager(new LinearLayoutManager(ApplicationsActivity.this));
        ActiveApplicationAdapter adapter = new ActiveApplicationAdapter(ApplicationsActivity.this, list);
        adapter.setOnClickListener(this);
        listResult.setAdapter(adapter);
    }

    @Override
    public void onAcceptClick(List<Map<String, Object>> list, Map<String, Object> item, int position) {
        FirebaseFirestore.getInstance()
                .collection("candidature")
                .document(item.get("userId") + "_" + item.get("offer"))
                .update("status", 3).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        list.get(position).put("status", 3L);
                        updateList(list);
                    }
                });
    }

    @Override
    public void onDeclineClick(List<Map<String, Object>> list, Map<String, Object> item, int position) {
        FirebaseFirestore.getInstance()
                .collection("candidature")
                .document(item.get("userId") + "_" + item.get("offer"))
                .update("status", 4).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        list.get(position).put("status", 4L);
                        updateList(list);
                    }
                });
    }
}