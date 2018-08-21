package com.ssii.demogeofences2.Account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ssii.demogeofences2.R;

public class SignUpActivity extends AppCompatActivity {

    EditText email, password, age;
    Button signUpButton;
    RadioGroup genreRadioButtonGroup;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        initializeComponents();
    }

    private void initializeComponents() {
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        age = findViewById(R.id.ageText);
        signUpButton = findViewById(R.id.signUpbtn);
        genreRadioButtonGroup = findViewById(R.id.genreRadioButtonGroup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();

                    auth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Te has registrado con éxito", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private Boolean validate() {
        Boolean result = false;

        String _email = email.getText().toString();
        String _password = password.getText().toString();
        String _age = age.getText().toString();

        if (_email.isEmpty() || _password.isEmpty() || _age.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();

        }
        else
            result = true;
        return result;
    }
}
