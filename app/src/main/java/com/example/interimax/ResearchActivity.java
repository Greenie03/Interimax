package com.example.interimax;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.models.Offer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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
        Offer.getAllOffers().thenAccept(offers -> {
            runOnUiThread(() -> {
                Set<String> jobTitle = offers.stream().map(Offer::getJobTitle).collect(Collectors.toSet());
                jobTitle.forEach(jt -> {
                    Button b = createNewPopularButton(jt);
                    button_layout.addView(b);
                    Log.d("Job", jt);
                });
            });
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
                if(!search_text.isEmpty()){
                    Intent intent = new Intent(ResearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("job_name", search_text);
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
                    historyData.add(search_text);
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
        LinkedHashSet<String> set;
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            set = (LinkedHashSet<String>) ois.readObject();
            Log.d("history", set.toString());
        } catch (IOException | ClassNotFoundException e) {
            Log.d("history error", "y'a une erreur chef");
            Log.e(e.toString(),Log.getStackTraceString(e));
            return new LinkedHashSet<>();
        }
        return set;
    }

    public void saveHistory(LinkedHashSet<String> set, String fileName) {
        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(set);
        } catch (IOException e) {
            Log.d("loading error", "y'a une erreur chef");
            Log.e("Save History Error", "Error while saving history: " + e.getMessage(), e);
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