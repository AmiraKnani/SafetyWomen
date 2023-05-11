package com.example.safewomen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    TextInputEditText fullName, email, password, dob, city;
    Button register, cancel;
    TextView signin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.editTextTextPersonName2);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword2);
        dob = findViewById(R.id.editTextDateOfBirth);
        city = findViewById(R.id.editTextCity);
        register = findViewById(R.id.buttonRegister);
        cancel = findViewById(R.id.buttonCancel);
        signin = findViewById(R.id.textViewSignInHere);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("fullName", fullName.getText().toString());
                                    user.put("email", email.getText().toString());
                                    user.put("dob", dob.getText().toString());
                                    user.put("city", city.getText().toString());

                                    db.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(Signup.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                        intent = new Intent(Signup.this, MainActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(Signup.this, "Failed to Register", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(Signup.this, "Failed to Authenticate", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
