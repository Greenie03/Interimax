package com.example.interimax;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilterFragment extends BottomSheetDialogFragment {

    private View view;
    private Set<String> employers;
    private Set<String> locations;
    private int salaryFromValue;
    private int salaryToValue;
    private HorizontalScrollView scrollView;
    private TextView noEmployer;
    private LinearLayout employerLayout;
    private TextView averageSalary;
    private TextView salaryFrom;
    private TextView salaryTo;
    private RangeSlider slider;
    private RelativeLayout locationLayout;
    private TextView locationDisplay;
    private OnFragmentInteractionListener mListener;
    private Map<String, Object> data;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Map<String, Object> data);
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_filter_list_dialog, container,
                false);

        scrollView = view.findViewById(R.id.scrollView);
        noEmployer = view.findViewById(R.id.no_employer);
        employerLayout = view.findViewById(R.id.selected_buttons);
        averageSalary = view.findViewById(R.id.average_salary);
        salaryFrom = view.findViewById(R.id.salary_from);
        salaryTo = view.findViewById(R.id.salary_to);
        slider = view.findViewById(R.id.slider);
        locationLayout = view.findViewById(R.id.location_layout);
        locationDisplay = view.findViewById(R.id.locations);

        if(data!=null){
            employers = (Set<String>) data.get("employers");
            salaryFromValue = (int) data.get("salaryFrom");
            salaryToValue = (int) data.get("salaryTo");
            locations = (Set<String>) data.get("locations");
        }else{
            employers = new HashSet<>();
            locations = new HashSet<>();
            salaryFromValue = 10;
            salaryToValue = 60;
        }
        slider.setValues((float) salaryFromValue, (float) salaryToValue);
        updateRangeValues();
        updateLocationsDisplay();

        ImageButton backButton = view.findViewById(R.id.back_button);
        Button doneButton = view.findViewById(R.id.finish_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToActivity();
                dismiss();
            }
        });

        for(String employer : employers){
            LinearLayout button = createCrossButton(employer, employerLayout, employers, scrollView, noEmployer);
            employerLayout.addView(button);
            updateVisibility(employers, scrollView, noEmployer);
        }

        EditText filterEmployerET = view.findViewById(R.id.filter_employer_input);
        ImageButton filterEmployerBtn = view.findViewById(R.id.filter_employer_button_id);

        filterEmployerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                String search_text = filterEmployerET.getText().toString();
                if(!search_text.isEmpty()){
                    boolean added = employers.add(search_text);
                    if(added) {
                        LinearLayout button = createCrossButton(search_text, employerLayout, employers, scrollView, noEmployer);
                        employerLayout.addView(button);
                        updateVisibility(employers, scrollView, noEmployer);
                    }
                }else{
                    Toast.makeText(getContext(), "Entrez une valeur !", Toast.LENGTH_LONG).show();
                }
            }
        });

        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                updateRangeValues();
            }
        });

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<String> tmpLocations = locations;
                View customLayout = getLayoutInflater().inflate(R.layout.location_layout, null);
                ImageButton add = customLayout.findViewById(R.id.add_location_button_id);
                EditText locationET = customLayout.findViewById(R.id.location_input);
                LinearLayout locationLayout = customLayout.findViewById(R.id.selected_locations);
                HorizontalScrollView locationScrollView = customLayout.findViewById(R.id.locationScrollView);
                TextView noLocations = customLayout.findViewById(R.id.no_location);
                //Button myPosition = customLayout.findViewById(R.id.my_position_button);

                for(String location : tmpLocations){
                    LinearLayout button = createCrossButton(location,locationLayout, tmpLocations, locationScrollView, noLocations);
                    locationLayout.addView(button);
                    updateVisibility(tmpLocations, locationScrollView, noLocations);
                }

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String location_text = locationET.getText().toString();
                        if(!location_text.isEmpty()) {
                            boolean added = tmpLocations.add(location_text);
                            if (added) {
                                LinearLayout button = createCrossButton(location_text, locationLayout, tmpLocations, locationScrollView, noLocations);
                                locationLayout.addView(button);
                                updateVisibility(tmpLocations, locationScrollView, noLocations);
                            }
                        }else{
                            Toast.makeText(getContext(), "Entrez une valeur !", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /*myPosition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ActivityCompat.checkSelfPermission(
                                getContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                getContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    },
                                    1
                            );
                        }else{
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String city = address.getLocality();

                                }else{
                                    Toast.makeText(getContext(), "Aucune ville détéctée",Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                Log.getStackTraceString(e);
                            }
                        }
                    }
                });*/

                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setTitle("Ajouter une localisation")
                        .setView(customLayout)
                        .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                locations = tmpLocations;
                                updateLocationsDisplay();
                            }
                        })
                        .setNegativeButton("Annuler",null)
                        .create();
                alert.getWindow().setBackgroundDrawable(getDrawable(getContext(), R.drawable.rounded));
                alert.show();
            }
        });



        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sendDataToActivity() {
        if (mListener != null) {
            data = new HashMap<>();
            data.put("employers", employers);
            data.put("salaryFrom", salaryFromValue);
            data.put("salaryTo", salaryToValue);
            data.put("locations", locations);
            mListener.onFragmentInteraction(data);
        }
    }

    public void setData(Map<String, Object> data){
        this.data = data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateVisibility(Set<String> set, HorizontalScrollView scrollView, TextView display) {
        if (set.isEmpty()) {
            scrollView.setVisibility(View.INVISIBLE);
            display.setVisibility(View.VISIBLE);
        } else {
            scrollView.setVisibility(View.VISIBLE);
            display.setVisibility(View.INVISIBLE);
        }
    }

    private void updateLocationsDisplay(){
        if(locations.isEmpty()){
            locationDisplay.setText(R.string.no_location);
        }else{
            String locationsDisplayText = String.join(",", new ArrayList<>(locations));;
            locationDisplay.setText(locationsDisplayText);
        }
    }

    private void updateRangeValues(){
        Integer avg = (int) ((slider.getValues().get(0) + slider.getValues().get(1)) / 2);
        averageSalary.setText(avg +"€");
        Integer min = (int) slider.getValues().get(0).floatValue();
        salaryFromValue = min;
        salaryFrom.setText(min +"€");
        Integer max = (int) slider.getValues().get(1).floatValue();
        salaryToValue = max;
        salaryTo.setText(max +"€");
    }

    private LinearLayout createCrossButton(String button_text, LinearLayout layout, Set<String> set, HorizontalScrollView hsv, TextView noValues){
        LinearLayout b = new LinearLayout(getContext());
        ImageButton cross = new ImageButton(getContext());
        cross.setImageResource(R.drawable.small_cross_icon);
        cross.setBackground(getResources().getDrawable(R.color.transparent));
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set.remove(button_text);
                layout.removeView(b);
                updateVisibility(set, hsv, noValues);
            }
        });
        LinearLayout.LayoutParams crossLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        crossLp.setMargins(32,16,16,16);
        cross.setLayoutParams(crossLp);

        TextView buttonText = new TextView(getContext());
        buttonText.setText(button_text);
        buttonText.setTypeface(Typeface.DEFAULT_BOLD);
        buttonText.setTextSize(18);

        b.setGravity(Gravity.CENTER_VERTICAL);
        b.addView(buttonText);
        b.addView(cross);
        b.setBackground(getDrawable(getContext(), R.drawable.employer_button_background));
        b.setPadding(32,8,32,8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.setMargins(24,0,24,0);
        b.setLayoutParams(lp);

        return b;
    }
}