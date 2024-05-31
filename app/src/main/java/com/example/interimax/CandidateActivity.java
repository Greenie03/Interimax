package com.example.interimax;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interimax.models.Offer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CandidateActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPLOAD_CV = 1;
    private final int REQUEST_CODE_UPLOAD_MOTIVATION_LETTER = 2;
    private Uri cvUri;
    private Uri mlUri;
    private DocumentReference candidature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(getIntent().getExtras() != null){
                Offer offer = getIntent().getParcelableExtra("offer");

                ImageView icon = findViewById(R.id.icon);
                TextView name = findViewById(R.id.name);
                TextView salary = findViewById(R.id.salary);
                TextView employerName = findViewById(R.id.employer_name);
                TextView city = findViewById(R.id.city);
                name.setText(offer.getName());
                employerName.setText(offer.getEmployerName());
                String salaryText = String.valueOf(offer.getSalary()) + "/h";
                salary.setText(salaryText);
                city.setText(offer.getCity());

                Button candidateButton = findViewById(R.id.candidate);
                ImageButton cvUploadButton = findViewById(R.id.cv_upload_button);
                ImageButton mLUploadButton = findViewById(R.id.motivation_letter_upload);

                cvUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent uploadCVIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        uploadCVIntent.setType("*/*");
                        uploadCVIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});
                        uploadCVIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(uploadCVIntent, REQUEST_CODE_UPLOAD_CV);
                    }
                });

                mLUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent uploadCVIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        uploadCVIntent.setType("*/*");
                        uploadCVIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "application/pdf"});
                        uploadCVIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(uploadCVIntent, REQUEST_CODE_UPLOAD_MOTIVATION_LETTER);
                    }
                });

                candidateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(cvUri != null) {
                            String mimeType = CandidateActivity.this.getContentResolver().getType(cvUri);
                            if ("application/pdf".equals(mimeType) || "image/*".equals(mimeType)) {
                                // Code pour télécharger le fichier vers Firebase Storage
                                candidature = FirebaseFirestore.getInstance().collection("candidature").document();
                                String[] ext = mimeType.split("/");
                                uploadFileToFirebase(cvUri, "cvs/", "cv", ext[ext.length-1]);
                                if(mlUri != null){
                                    String mlMimeType = CandidateActivity.this.getContentResolver().getType(mlUri);
                                    if ("application/pdf".equals(mlMimeType) || "image/*".equals(mlMimeType)) {
                                        // Code pour télécharger le fichier vers Firebase Storage
                                        String[] mlExt = mimeType.split("/");
                                        uploadFileToFirebase(mlUri, "motivation_letters/", "motivation_letter", mlExt[mlExt.length-1]);
                                        candidature.update("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        candidature.update("offer", offer.getId());
                                    } else {
                                        Toast.makeText(CandidateActivity.this, "Veuillez choisir un fichier PDF", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(CandidateActivity.this, "Veuillez choisir un fichier PDF", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(CandidateActivity.this, DoneActivity.class);
                            intent.putExtra("offer", offer);
                            startActivity(intent);
                        }else{
                            Toast.makeText(CandidateActivity.this, "Veuillez entrer un CV !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }else{
            Intent intent = new Intent(CandidateActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPLOAD_CV) {
                cvUri = data.getData();
                String fileName = getFileName(cvUri);
                TextView cvName = findViewById(R.id.cv_name);
                ImageView validCv = findViewById(R.id.valid_cv);

                cvName.setText(fileName);
                validCv.setVisibility(View.VISIBLE);

            }
            if(requestCode == REQUEST_CODE_UPLOAD_MOTIVATION_LETTER){

                mlUri = data.getData();
                String fileName = getFileName(mlUri);
                TextView mlName = findViewById(R.id.ml_name);
                ImageView validMl = findViewById(R.id.valid_ml);

                mlName.setText(fileName);
                validMl.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }

    private void uploadFileToFirebase(Uri fileUri, String path, String field, String ext) {
        // Référence à Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Créer une référence de fichier pour l'upload
        StorageReference cvRef = storageRef.child(path + System.currentTimeMillis() + "." + ext);

        cvRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Récupérer l'URL de téléchargement
                    cvRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Ajouter les informations du fichier dans Firestore
                        candidature.update(field,uri.toString());
                    }).addOnFailureListener(e -> {
                        // Afficher une erreur si l'URL de téléchargement ne peut pas être récupérée
                        Toast.makeText(CandidateActivity.this, "Erreur lors de la récupération de l'URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Afficher une erreur si l'upload échoue
                    Toast.makeText(CandidateActivity.this, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show();
                });
    }
}