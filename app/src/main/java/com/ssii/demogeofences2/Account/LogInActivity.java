package com.ssii.demogeofences2.Account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssii.demogeofences2.R;

public class LogInActivity extends AppCompatActivity {

    EditText email, password;
    Button logIn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        checkUserActive();
    }

    private void checkUserActive() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Cambiar a home cogiendo su email
        }
        else {
            initializeComponents();
        }
    }

    private void initializeComponents() {
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        logIn = findViewById(R.id.logInButton);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                auth.signInWithEmailAndPassword(user_email, user_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                } else {
                                    Toast.makeText(LogInActivity.this, "No se pudo iniciar sesión",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
