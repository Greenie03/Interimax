package com.example.interimax;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FilterFragment extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_COUNT = "item_count";
    private View view;

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

        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        LinearLayout employerLayout = view.findViewById(R.id.selected_buttons);
        for (int i = 0; i < 20; i++) {
            LinearLayout b = new LinearLayout(getContext());
            ImageButton cross = new ImageButton(getContext());
            cross.setImageResource(R.drawable.small_cross_icon);
            cross.setBackground(getResources().getDrawable(R.color.transparent));
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "text", Toast.LENGTH_LONG).show();
                }
            });
            TextView employerText = new TextView(getContext());
            String button_text = "employeur n°"+String.valueOf(i);
            employerText.setText(button_text);
            employerText.setTypeface(Typeface.DEFAULT_BOLD);

            b.setGravity(Gravity.CENTER_VERTICAL);
            b.addView(employerText);
            b.addView(cross);
            b.setBackground(getDrawable(getContext(), R.drawable.employer_button_background));
            b.setPadding(16,0,16,0);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            lp.setMargins(24,0,24,0);
            b.setLayoutParams(lp);
            employerLayout.addView(b);
        }

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}