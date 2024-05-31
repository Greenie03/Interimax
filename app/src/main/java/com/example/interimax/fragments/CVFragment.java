package com.example.interimax.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.interimax.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class CVFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    // Registering for Activity Result
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    fileUri = result.getData().getData();
                    displayFileInfo(fileUri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cv, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setting up the back button functionality
        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Button buttonChooseCV = view.findViewById(R.id.button_choose_cv);
        Button buttonValidate = view.findViewById(R.id.button_validate);

        buttonChooseCV.setOnClickListener(v -> {
            // Ouvrir le sélecteur de fichiers pour choisir un CV
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activityResultLauncher.launch(Intent.createChooser(intent, "Choisir un fichier"));
        });

        buttonValidate.setOnClickListener(v -> {
            // Valider le fichier choisi
            if (fileUri != null) {
                // Vérifier que c'est un fichier PDF
                String mimeType = getContext().getContentResolver().getType(fileUri);
                if ("application/pdf".equals(mimeType)) {
                    // Code pour télécharger le fichier vers Firebase Storage
                    uploadFileToFirebase(fileUri);
                } else {
                    Toast.makeText(getContext(), "Veuillez choisir un fichier PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Afficher un message pour choisir un fichier
                Toast.makeText(getContext(), "Veuillez choisir un fichier", Toast.LENGTH_SHORT).show();
            }
        });
        // Gérer le bouton de retour de l'appareil
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() != null) {
                    DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            }
        });
        return view;

    }
    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Retourner au menu déroulant
            if (getActivity() != null) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    private void displayFileInfo(Uri fileUri) {
        RelativeLayout fileInfoLayout = getView().findViewById(R.id.file_info_layout);
        ImageView fileIcon = getView().findViewById(R.id.file_icon);
        TextView fileName = getView().findViewById(R.id.file_name);
        TextView fileSize = getView().findViewById(R.id.file_size);

        // Récupérer les informations du fichier
        Cursor cursor = getContext().getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String displayName = null;
            int colInd = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (colInd >= 0){
                displayName = cursor.getString(colInd);}
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            String size = cursor.getString(sizeIndex);
            cursor.close();

            // Afficher les informations du fichier
            fileName.setText(displayName);
            fileSize.setText(size + " KB");
            fileIcon.setImageResource(R.drawable.ic_pdf); // Assurez-vous que vous avez un icône PDF dans votre dossier drawable

            fileInfoLayout.setVisibility(View.VISIBLE);
        }
    }
    private void uploadFileToFirebase(Uri fileUri) {
        // Référence à Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Créer une référence de fichier pour l'upload
        StorageReference cvRef = storageRef.child("cvs/" + System.currentTimeMillis() + ".pdf");

        cvRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Récupérer l'URL de téléchargement
                    cvRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Ajouter les informations du fichier dans Firestore
                        addFileToFirestore(uri.toString());
                    }).addOnFailureListener(e -> {
                        // Afficher une erreur si l'URL de téléchargement ne peut pas être récupérée
                        Toast.makeText(getContext(), "Erreur lors de la récupération de l'URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Afficher une erreur si l'upload échoue
                    Toast.makeText(getContext(), "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show();
                });
    }

    private void addFileToFirestore(String downloadUrl) {
        // Référence à Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Créer un document avec les informations du fichier
        Map<String, Object> file = new HashMap<>();
        file.put("url", downloadUrl);
        file.put("timestamp", System.currentTimeMillis());

        // Ajouter le document à la collection 'cvs'
        db.collection("cvs")
                .add(file)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Afficher un message de succès
                            Toast.makeText(getContext(), "CV téléchargé avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            // Afficher une erreur si l'ajout échoue
                            Toast.makeText(getContext(), "Erreur lors de l'ajout du CV à Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
