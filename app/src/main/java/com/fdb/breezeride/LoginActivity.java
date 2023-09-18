package com.fdb.breezeride;

import static com.fdb.breezeride.Constants.KEY_COLLECTION_USERS;
import static com.fdb.breezeride.Constants.KEY_EMAIL;
import static com.fdb.breezeride.Constants.KEY_IMAGE;
import static com.fdb.breezeride.Constants.KEY_IP_ADDRESS;
import static com.fdb.breezeride.Constants.KEY_IS_SIGNED_IN;
import static com.fdb.breezeride.Constants.KEY_USER_ID;
import static com.fdb.breezeride.Constants.KEY_USER_NAME;
import static com.fdb.breezeride.Constants.KEY_VERIFIED;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fdb.breezeride.R;
import com.fdb.breezeride.databinding.ActivityLoginBinding;
import com.fdb.breezeride.PreferenceManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;
    private ActivityLoginBinding activityLoginBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());

        changeStatusBarColor();

        setContentView(activityLoginBinding.getRoot());
        setListeners();
        autoLogIn();

    }

    private void setListeners() {

        activityLoginBinding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        activityLoginBinding.buttonLogin.setOnClickListener(v -> {

            if (isValidLoginDetails()) {

                if (firebaseUser != null) {

                    firebaseUser.reload();

                }

                if (isInsertedEmailSaved(activityLoginBinding.inputEmail.getText().toString())) {

                    isVerified();

                } else {

                    Login();

                }

            }

        });

        activityLoginBinding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Patterns.EMAIL_ADDRESS.matcher(charSequence.toString()).matches()) {

                    activityLoginBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityLoginBinding.textEmail.setText("");

                } else {

                    activityLoginBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityLoginBinding.textEmail.setText(R.string.unknown_email);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        activityLoginBinding.inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 6) {

                    activityLoginBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_incorrect_input
                    ));

                    activityLoginBinding.textPassword.setText(R.string.poor_password);

                } else {

                    activityLoginBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                            getApplicationContext(), R.drawable.background_correct_input
                    ));

                    activityLoginBinding.textPassword.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activityLoginBinding.buttonLogin.setVisibility(View.INVISIBLE);
            activityLoginBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activityLoginBinding.buttonLogin.setVisibility(View.VISIBLE);
            activityLoginBinding.progressBar.setVisibility(View.INVISIBLE);

        }

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private Boolean isValidLoginDetails() {

        if (activityLoginBinding.inputEmail.getText().toString().trim().isEmpty()) {

            showToast("Enter Email");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(activityLoginBinding.inputEmail.getText().toString()).matches()) {

            showToast("Enter Email");
            return false;

        } else if (activityLoginBinding.inputPassword.getText().toString().trim().isEmpty()) {

            showToast("Enter Password");
            return false;

        } else if (activityLoginBinding.inputPassword.getText().toString().trim().getBytes().length < 6) {

            showToast("Password is short");
            return false;

        } else {

            return true;

        }

    }

    private void Login() {

        loading(true);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(activityLoginBinding.inputEmail.getText().toString(),
                        activityLoginBinding.inputPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    loading(false);


                    if (task.isSuccessful()) {

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (firebaseUser != null) {

                            if (firebaseUser.isEmailVerified()) {

                                firestore.collection(KEY_COLLECTION_USERS)
                                        .whereEqualTo(KEY_EMAIL, activityLoginBinding.inputEmail.getText().toString())
                                        .get()
                                        .addOnCompleteListener(v -> {

                                            if (v.isSuccessful() && v.getResult() != null && v.getResult().getDocuments().size() > 0) {

                                                DocumentSnapshot documentSnapshot = v.getResult().getDocuments().get(0);
                                                preferenceManager.putString(KEY_USER_ID, documentSnapshot.getId());
                                                preferenceManager.putString(KEY_USER_NAME, documentSnapshot.getString(KEY_USER_NAME));
                                                preferenceManager.putString(KEY_IMAGE, documentSnapshot.getString(KEY_IMAGE));
                                                preferenceManager.putString(KEY_EMAIL, documentSnapshot.getString(KEY_EMAIL));
                                                preferenceManager.putString(KEY_IP_ADDRESS, getDeviceIpAddress());

                                                preferenceManager.putBoolean(KEY_VERIFIED, true);
                                                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);

                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } else {

                                                loading(false);
                                                showToast("This account isn't sign up");

                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                                if (firebaseUser != null) {
                                                    firebaseUser.delete();
                                                }

                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                finish();

                                            }
                                        });

                            }

                        }

                    } else if (Objects.equals(
                            Objects.requireNonNull(task.getException()).getClass(),
                            FirebaseAuthInvalidUserException.class)) {

                        inputErrorVisualisation(1);
                        showToast("Invalid Email");

                    } else if (Objects.equals(
                            task.getException().getClass(),
                            FirebaseAuthInvalidCredentialsException.class)) {

                        inputErrorVisualisation(2);
                        showToast("Incorrect Password");

                    } else {

                        showToast("Doesn't Sign in");

                    }
                });
    }

    private void isVerified() {

        if (firebaseUser != null) {

            firebaseUser.reload();

            try {

                TimeUnit.MILLISECONDS.sleep(50);

                firebaseUser.reload();

                if (firebaseUser.isEmailVerified()) {

                    Login();

                } else {

                    showToast("Verify Account");
                    firebaseUser.sendEmailVerification();

                }

            } catch (InterruptedException e) {

                throw new RuntimeException(e);

            }

        } else {

            Login();

        }

    }

    private boolean isInsertedEmailSaved(String email) {

        return email.equals(preferenceManager.getString(KEY_EMAIL));

    }

    private void autoLogIn() {

        if (firebaseUser != null) {
            firebaseUser.reload();

            try {

                firebaseUser.reload();

                TimeUnit.MILLISECONDS.sleep(50);

                firebaseUser.reload();

                if (firebaseUser.isEmailVerified()) {

                    if (preferenceManager.getBoolean(KEY_IS_SIGNED_IN) && preferenceManager.getBoolean(KEY_VERIFIED)) {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                } else {

                    showToast("Verify your account");
                    firebaseUser.sendEmailVerification();

                }

            } catch (InterruptedException e) {

                throw new RuntimeException(e);

            }

        }

    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.white, getTheme()));
    }

    private void inputErrorVisualisation(int code) {

        if (code == 1) {

            activityLoginBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activityLoginBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activityLoginBinding.textPassword.setText("");
            activityLoginBinding.textEmail.setText(R.string.unknown_email);

        } else if (code == 2) {

            activityLoginBinding.inputEmail.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_correct_input
            ));

            activityLoginBinding.inputPassword.setBackground(AppCompatResources.getDrawable(
                    getApplicationContext(), R.drawable.background_incorrect_input
            ));

            activityLoginBinding.textEmail.setText("");
            activityLoginBinding.textPassword.setText(R.string.wrong_password);

        }

    }

    private void biometricSuccess() {

        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate()) {

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showToast("Error");
                break;

            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                activityLoginBinding.textRestorePassword.setText(R.string.error);
            }

        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("        ChatT")
                .setDescription("Use Fingerprint for login")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }

    public static String getDeviceIpAddress() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                        return inetAddress.getHostAddress();

                    }

                }

            }

        } catch (SocketException ex) {

            ex.printStackTrace();

        }

        return null;

    }

}