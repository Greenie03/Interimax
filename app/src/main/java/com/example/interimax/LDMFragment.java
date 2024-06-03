package com.example.interimax;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interimax.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LDMFragment extends Fragment {

    private static final String TAG = "LDMFragment";
    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView ldmRecyclerView;
    private LDMAdapter ldmAdapter;
    private List<Map<String, Object>> ldmList;

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
        View view = inflater.inflate(R.layout.fragment_ldm, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarLDM);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final LinearLayout margin = view.findViewById(R.id.margin);

        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int topInset = insets.getSystemWindowInsetTop();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) margin.getLayoutParams();
                lp.height = topInset;
                margin.setLayoutParams(lp);
                return insets;
            }
        });

        // Setting up the back button functionality
        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        ldmRecyclerView = view.findViewById(R.id.ldm_recycler_view);
        ldmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ldmList = new ArrayList<>();
        ldmAdapter = new LDMAdapter(ldmList);
        ldmRecyclerView.setAdapter(ldmAdapter);

        Button buttonChooseLDM = view.findViewById(R.id.button_choose_ldm);
        Button buttonValidate = view.findViewById(R.id.ldm_button_validate);

        buttonChooseLDM.setOnClickListener(v -> {
            // Ouvrir le sélecteur de fichiers pour choisir une LDM
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activityResultLauncher.launch(Intent.createChooser(intent, "Choisir un fichier"));
        });

        buttonValidate.setOnClickListener(v -> {
            // Valider le fichier choisi
            if (fileUri != null) {
                // Vérifier le type de fichier
                String mimeType = getContext().getContentResolver().getType(fileUri);
                if ("application/pdf".equals(mimeType) || "application/msword".equals(mimeType) || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType) || mimeType.startsWith("image/")) {
                    // Vérifier si le fichier existe déjà
                    String fileName = getFileName(fileUri);
                    checkIfFileExists(fileName, new FileExistsCallback() {
                        @Override
                        public void onResult(boolean exists) {
                            if (exists) {
                                Toast.makeText(getContext(), "Ce fichier existe déjà.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Télécharger le fichier vers Firebase Storage
                                uploadFileToFirebase(fileUri);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Veuillez choisir un fichier PDF, DOC, DOCX ou une image", Toast.LENGTH_SHORT).show();
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

        loadUserLDMs();

        return view;
    }

    private void displayFileInfo(Uri fileUri) {
        View view = getView();
        if (view == null) return;

        RelativeLayout fileInfoLayout = view.findViewById(R.id.ldm_file_info_layout);
        ImageView fileIcon = view.findViewById(R.id.doc_icon);
        TextView fileName = view.findViewById(R.id.doc_name);
        TextView fileSize = view.findViewById(R.id.doc_size);

        if (fileInfoLayout == null || fileIcon == null || fileName == null || fileSize == null) {
            Log.e(TAG, "Some views are not initialized");
            return;
        }

        // Récupérer les informations du fichier
        Cursor cursor = getContext().getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            @SuppressLint("Range") int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            String size = cursor.getString(sizeIndex);
            cursor.close();

            // Afficher les informations du fichier
            fileName.setText(displayName);
            fileSize.setText(String.format("%s KB", size));

            // Définir l'icône en fonction du type MIME
            String mimeType = getContext().getContentResolver().getType(fileUri);
            if (mimeType != null) {
                switch (mimeType) {
                    case "application/pdf":
                        fileIcon.setImageResource(R.drawable.ic_pdf); // Icône pour les fichiers PDF
                        break;
                    case "application/msword":
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                        fileIcon.setImageResource(R.drawable.ic_doc); // Icône pour les fichiers Word
                        break;
                    default:
                        if (mimeType.startsWith("image/")) {
                            fileIcon.setImageResource(R.drawable.ic_image); // Icône pour les fichiers image
                        } else {
                            fileIcon.setImageResource(R.drawable.ic_file); // Icône générique pour les autres types de fichiers
                        }
                        break;
                }
            }
            fileInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        // Référence à Firebase Storage
        StorageReference storageRef = storage.getReference();

        // Créer une référence de fichier pour l'upload
        StorageReference ldmRef = storageRef.child("ldms/" + System.currentTimeMillis() + ".pdf");

        ldmRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Récupérer l'URL de téléchargement
                    ldmRef.getDownloadUrl().addOnSuccessListener(uri -> {
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
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String fileName = getFileName(fileUri);
        String mimeType = getContext().getContentResolver().getType(fileUri);

        // Créer un document avec les informations du fichier
        Map<String, Object> file = new HashMap<>();
        file.put("url", downloadUrl);
        file.put("name", fileName); // Ajouter le nom du fichier
        file.put("mimeType", mimeType); // Ajouter le type MIME
        file.put("timestamp", System.currentTimeMillis());
        file.put("userId", userId); // Ajouter l'ID de l'utilisateur

        // Ajouter le document à la collection 'ldms'
        db.collection("ldms")
                .add(file)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Afficher un message de succès
                            Toast.makeText(getContext(), "LDM téléchargé avec succès", Toast.LENGTH_SHORT).show();
                            loadUserLDMs(); // Recharger les LDMs après l'ajout
                        } else {
                            // Afficher une erreur si l'ajout échoue
                            Toast.makeText(getContext(), "Erreur lors de l'ajout du LDM à Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void checkIfFileExists(String fileName, FileExistsCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onResult(false);
            return;
        }

        String userId = currentUser.getUid();

        db.collection("ldms")
                .whereEqualTo("userId", userId)
                .whereEqualTo("name", fileName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        callback.onResult(true);
                    } else {
                        callback.onResult(false);
                    }
                });
    }

    private interface FileExistsCallback {
        void onResult(boolean exists);
    }

    private void loadUserLDMs() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "User not authenticated");
            return;
        }

        String userId = currentUser.getUid();

        db.collection("ldms")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ldmList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> ldm = document.getData();
                                ldm.put("id", document.getId()); // Ajouter l'ID du document pour la suppression
                                ldmList.add(ldm);
                            }
                            ldmAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Erreur lors du chargement des LDMs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class LDMAdapter extends RecyclerView.Adapter<LDMAdapter.LDMViewHolder> {
        private List<Map<String, Object>> ldmList;

        public LDMAdapter(List<Map<String, Object>> ldmList) {
            this.ldmList = ldmList;
        }

        @NonNull
        @Override
        public LDMAdapter.LDMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ldm, parent, false);
            return new LDMAdapter.LDMViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LDMAdapter.LDMViewHolder holder, int position) {
            Map<String, Object> ldm = ldmList.get(position);
            holder.bind(ldm);
        }

        @Override
        public int getItemCount() {
            return ldmList.size();
        }

        class LDMViewHolder extends RecyclerView.ViewHolder {
            TextView fileName;
            TextView fileSize;
            ImageView deleteButton;
            ImageView fileIcon;
            public LDMViewHolder(@NonNull View itemView) {
                super(itemView);
                fileName = itemView.findViewById(R.id.ldm_file_name);
                fileSize = itemView.findViewById(R.id.ldm_file_size);
                deleteButton = itemView.findViewById(R.id.ldm_delete_button);
                fileIcon = itemView.findViewById(R.id.ldm_file_icon);
            }

            public void bind(Map<String, Object> ldm) {
                fileName.setText((String) ldm.get("name")); // Afficher le nom du fichier
                fileSize.setText(String.valueOf(ldm.get("timestamp"))); // Afficher le timestamp en guise de taille

                // Définir l'icône en fonction du type MIME
                String mimeType = (String) ldm.get("mimeType");
                if (mimeType != null) {
                    switch (mimeType) {
                        case "application/pdf":
                            fileIcon.setImageResource(R.drawable.ic_pdf); // Icône pour les fichiers PDF
                            break;
                        case "application/msword":
                        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                            fileIcon.setImageResource(R.drawable.ic_doc); // Icône pour les fichiers Word
                            break;
                        default:
                            if (mimeType.startsWith("image/")) {
                                fileIcon.setImageResource(R.drawable.ic_image); // Icône pour les fichiers image
                            } else {
                                fileIcon.setImageResource(R.drawable.ic_file); // Icône générique pour les autres types de fichiers
                            }
                            break;
                    }
                }
                deleteButton.setOnClickListener(v -> {
                    String ldmId = (String) ldm.get("id");
                    db.collection("ldms").document(ldmId)
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "LDM supprimé", Toast.LENGTH_SHORT).show();
                                    loadUserLDMs(); // Recharger les LDMs après la suppression
                                } else {
                                    Toast.makeText(getContext(), "Erreur lors de la suppression du LDM", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        }
    }
}
