package com.example.interimax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.adapters.HistoryAdapter;
import com.example.interimax.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class ResearchActivity extends AppCompatActivity implements FilterFragment.OnFragmentInteractionListener {
    EditText search_input;
    Map<String,Object> filterData;
    LinkedHashSet<String> historyData;
    private static String HISTORY_FILE = "history.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_research);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        search_input = findViewById(R.id.search_input);
        LinearLayout button_layout = findViewById(R.id.popular_buttons);
        FirebaseFirestore.getInstance().collection("job_popularity").orderBy("popularity", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    runOnUiThread(() -> {
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            Button b = createNewPopularButton(doc.getId());
                            button_layout.addView(b);
                        }
                    });
                }
            }
        });
        historyData = loadHistory(HISTORY_FILE);

        ImageButton back_button =  findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton search_button = findViewById(R.id.search_button_id);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text = search_input.getText().toString();
                if(!search_text.isEmpty() || filterData!=null){
                    Intent intent = new Intent(ResearchActivity.this, SearchResultActivity.class);
                    if(!search_text.isEmpty()) {
                        intent.putExtra("job_name", search_text);
                    }
                    if(filterData!=null){
                        intent.putExtra("salaryFrom", (int) filterData.get("salaryFrom"));
                        intent.putExtra("salaryTo", (int) filterData.get("salaryTo"));
                        if(!((Set<String>) filterData.get("employers")).isEmpty()){
                            Set<String> employers = (Set<String>) filterData.get("employers");
                            String[] employersArray = employers.toArray(new String[employers.size()]);
                            intent.putExtra("employers", employersArray);
                        }
                        if(!((Set<String>) filterData.get("locations")).isEmpty()){
                            Set<String> locations = (Set<String>) filterData.get("locations");
                            String[] locationsArray = locations.toArray(new String[locations.size()]);
                            intent.putExtra("locations", locationsArray);
                        }
                    }
                    if(historyData.contains(search_text)){
                        historyData.remove(search_text);
                    }
                    if(!search_text.isEmpty()) {
                        historyData.add(search_text);
                    }
                    saveHistory(historyData, HISTORY_FILE);
                    updateHistory();
                    startActivity(intent);
                }else{
                    Toast.makeText(ResearchActivity.this, "Entrez une valeur !", Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageButton filter_button = findViewById(R.id.filter_button);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterFragment filterFragment = FilterFragment.newInstance();
                if(filterData != null){
                    filterFragment.setData(filterData);
                }
                filterFragment.show(getSupportFragmentManager(), "filter_fragment");
            }
        });
        updateHistory();
    }

    private Button createNewPopularButton(String name){
        Button b = new Button(this);
        b.setText(name);
        b.setBackground(getDrawable(R.drawable.popular_button_background));
        b.setPadding(4,0,4,0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.setMargins(18,0,18,0);
        b.setLayoutParams(lp);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_input.setText(name);
            }
        });

        return b;
    }

    @Override
    public void onFragmentInteraction(Map<String, Object> data) {
        // Handle the data received from the fragment
        this.filterData = data;
    }

    public LinkedHashSet<String> loadHistory(String fileName) {
        LinkedHashSet<String> data = new LinkedHashSet<>();
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileName);
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
            Log.d("FileRead", "Fichier lu avec succès");
        } catch (IOException e) {
            Log.e("FileRead", "Erreur lors de la lecture du fichier", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e("FileRead", "Erreur lors de la fermeture du fichier", e);
                }
            }
        }
        return data;
    }

    public void saveHistory(LinkedHashSet<String> set, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            for (String item : set) {
                fos.write((item + System.lineSeparator()).getBytes());
            }
            Log.d("FileWrite", "Fichier écrit avec succès");
        } catch (IOException e) {
            Log.e("FileWrite", "Erreur lors de l'écriture du fichier", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FileWrite", "Erreur lors de la fermeture du fichier", e);
                }
            }
        }
    }

    public void updateHistory(){
        RecyclerView history = findViewById(R.id.history);
        history.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter(this, historyData);
        history.setAdapter(adapter);
        adapter.setOnClickListener(new HistoryAdapter.OnClickListener() {
            @Override
            public void onClick(int position, String model) {
                search_input.setText(model);
            }
        });
        adapter.setCrossOnClickListener(new HistoryAdapter.OnClickListener() {
            @Override
            public void onClick(int position, String model) {
                historyData.remove(model);
                updateHistory();
            }
        });
    }
}