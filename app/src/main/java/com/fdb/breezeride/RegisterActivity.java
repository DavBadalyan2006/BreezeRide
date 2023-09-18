package com.fdb.breezeride;

import static com.fdb.breezeride.Constants.IMAGE_HEIGHT;
import static com.fdb.breezeride.Constants.IMAGE_WIDTH;
import static com.fdb.breezeride.Constants.KEY_COLLECTION_USERS;
import static com.fdb.breezeride.Constants.KEY_EMAIL;
import static com.fdb.breezeride.Constants.KEY_IMAGE;
import static com.fdb.breezeride.Constants.KEY_USER_ID;
import static com.fdb.breezeride.Constants.KEY_USER_NAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.fdb.breezeride.R;
import com.fdb.breezeride.databinding.ActivityRegisterBinding;
import com.fdb.breezeride.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private String encodedImage;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore firebaseFirestore;
    private ActivityRegisterBinding activityRegisterBinding;
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    if (result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        try {

                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            activityRegisterBinding.imageProfile.setImageBitmap(bitmap);
                            activityRegisterBinding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityRegisterBinding.getRoot());

        setListeners();
    }

    private void setListeners() {

        activityRegisterBinding.textSignIn.setOnClickListener(v -> onBackPressed());

        activityRegisterBinding.buttonRegister.setOnClickListener(v -> {
            if (isValidRegisterDetails()) {

                Register();

            }
        });

        activityRegisterBinding.layoutImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });

        activityRegisterBinding.inputUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (encodedImage == null) {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textAddImage.setText(R.string.add_image);

                } else {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textAddImage.setText("");

                }

                if (charSequence.length() > 3) {

                    activityRegisterBinding.inputUserName.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textUserName.setText("");

                }   else {

                    activityRegisterBinding.inputUserName.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textUserName.setText(R.string.short_username);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        activityRegisterBinding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (encodedImage == null) {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textAddImage.setText(R.string.add_image);

                } else {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textAddImage.setText("");

                }

                if (Patterns.EMAIL_ADDRESS.matcher(charSequence.toString()).matches()) {

                    activityRegisterBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textEmail.setText("");

                } else {

                    activityRegisterBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textEmail.setText(R.string.unknown_email);

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        activityRegisterBinding.inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (encodedImage == null) {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textAddImage.setText(R.string.add_image);

                } else {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textAddImage.setText("");

                }

                if (charSequence.length() < 6) {

                    activityRegisterBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textPassword.setText(R.string.poor_password);

                } else if (charSequence.length() <= 8) {

                    activityRegisterBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_password_input
                    ));

                    activityRegisterBinding.textPassword.setText(R.string.fair_password);

                } else {

                    activityRegisterBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textPassword.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        activityRegisterBinding.inputConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (encodedImage == null) {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textAddImage.setText(R.string.add_image);

                } else {

                    activityRegisterBinding.imageProfile.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textAddImage.setText("");

                }

                if (activityRegisterBinding.inputPassword.getText().toString().
                        equals(activityRegisterBinding.inputConfirmPassword.getText().toString())) {

                    activityRegisterBinding.inputConfirmPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityRegisterBinding.textPassword.setText("");
                    activityRegisterBinding.textConfirmPassword.setText("");

                } else {

                    activityRegisterBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.inputConfirmPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityRegisterBinding.textPassword.setText(R.string.match_password);
                    activityRegisterBinding.textConfirmPassword.setText(R.string.match_password);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void Register() {

        loading(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(v -> {

                    boolean flag = true;

                    for (QueryDocumentSnapshot queryDocumentSnapshot: v.getResult()) {

                        if (activityRegisterBinding.inputUserName.getText().toString().trim()
                                .equals(queryDocumentSnapshot.getString(KEY_USER_NAME))) {

                            flag = false;

                        }

                    }

                    if (flag) {

                        firebaseAuth.createUserWithEmailAndPassword(activityRegisterBinding.inputEmail.getText().toString(),
                                        activityRegisterBinding.inputPassword.getText().toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        HashMap<String, Object> userData = new HashMap<>();

                                        userData.put(KEY_USER_NAME, activityRegisterBinding.inputUserName.getText().toString().trim());
                                        userData.put(KEY_EMAIL, activityRegisterBinding.inputEmail.getText().toString());
                                        userData.put(KEY_IMAGE, encodedImage);


                                        firebaseFirestore.collection(KEY_COLLECTION_USERS)
                                                .add(userData)
                                                .addOnSuccessListener(documentReference -> {
                                                    preferenceManager.putString(KEY_USER_ID, documentReference.getId());
                                                    preferenceManager.putString(KEY_USER_NAME, activityRegisterBinding.inputUserName.getText().toString().trim());
                                                    preferenceManager.putString(KEY_IMAGE, encodedImage);

                                                    showToast("Sign up is successful\n" +
                                                            "Verify your account");

                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                    loading(false);

                                                })
                                                .addOnFailureListener(e -> {

                                                    showToast(e.getMessage());
                                                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();

                                                });


                                    } else if (Objects.equals(
                                            Objects.requireNonNull(task.getException()).getClass(),
                                            FirebaseAuthUserCollisionException.class)) {
                                        loading(false);

                                        inputErrorVisualisation(2);
                                        showToast("Email already used");

                                    } else if (Objects.equals(
                                            Objects.requireNonNull(task.getException()).getClass(),
                                            FirebaseAuthWeakPasswordException.class)) {

                                        loading(false);
                                        showToast("Password is short");

                                    } else {

                                        showToast("Sign up isn't successful");
                                        loading(false);

                                    }

                                });

                    }

                    else {

                        inputErrorVisualisation(1);
                        showToast("This username is already used");
                        loading(false);

                    }
                });
    }

    private Boolean isValidRegisterDetails() {

        if (encodedImage == null) {

            showToast("Select profile image");
            return false;

        } else if (activityRegisterBinding.inputUserName.getText().toString().trim().isEmpty()) {

            showToast("Enter username");
            return false;

        } else if (activityRegisterBinding.inputEmail.getText().toString().trim().isEmpty()) {

            showToast("Enter email");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(activityRegisterBinding.inputEmail.getText().toString()).matches()) {

            showToast("Enter valid email!");
            return false;

        } else if (activityRegisterBinding.inputPassword.getText().toString().trim().isEmpty()) {

            showToast("Enter password");
            return false;

        } else if (activityRegisterBinding.inputConfirmPassword.getText().toString().trim().isEmpty()) {

            showToast("Confirm password");
            return false;

        } else if (!activityRegisterBinding.inputPassword.getText().toString().
                equals(activityRegisterBinding.inputConfirmPassword.getText().toString())) {

            showToast("Passwords doesn't matching");
            return false;

        } else {

            return true;

        }

    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activityRegisterBinding.buttonRegister.setVisibility(View.INVISIBLE);
            activityRegisterBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activityRegisterBinding.buttonRegister.setVisibility(View.VISIBLE);
            activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);

        }

    }

    private String encodedImage(Bitmap bitmap) {

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.WEBP, 95, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private void changeStatusBarColor() {

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white, getTheme()));

    }

    private void inputErrorVisualisation(int code) {

        if (code == 1) {

            activityRegisterBinding.inputUserName.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activityRegisterBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activityRegisterBinding.textEmail.setText("");
            activityRegisterBinding.textUserName.setText(R.string.used_username);

        } else if (code == 2) {

            activityRegisterBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activityRegisterBinding.inputUserName.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activityRegisterBinding.textEmail.setText(R.string.used_email);
            activityRegisterBinding.textUserName.setText("");

        }

    }

}