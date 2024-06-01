package com.example.interimax.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.interimax.R;

public class EditTextDialogFragment extends DialogFragment {

    private static final String ARG_HINT = "hint";
    private EditText editText;
    private Button saveButton;
    private EditTextDialogListener listener;

    public interface EditTextDialogListener {
        void onSaveClick(String inputText, int viewId);
    }

    public static EditTextDialogFragment newInstance(String hint, int viewId) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HINT, hint);
        args.putInt("viewId", viewId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setEditTextDialogListener(EditTextDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_text_dialog, container, false);

        editText = view.findViewById(R.id.edit_text);
        saveButton = view.findViewById(R.id.save_button);

        if (getArguments() != null) {
            String hint = getArguments().getString(ARG_HINT);
            editText.setHint(hint);
        }

        saveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveClick(editText.getText().toString(), getArguments().getInt("viewId"));
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
