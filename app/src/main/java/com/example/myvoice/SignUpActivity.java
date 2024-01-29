package com.example.myvoice;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * in this activity you create a new user
 * the app will check that there is no user with the same username
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText etUsername,etPassword,etPasswordAgain;
    private Button btnSignUp;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    /**
     * activated when this activity opens
     * create a new user after checking there is no other user with the same username
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.getText().toString().equals("")||etPassword.getText().toString().equals("")
                        ||etPasswordAgain.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this,"אחד או יותר מהשדות ריקים",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "הסיסמא לא זהה ב2 השדות", Toast.LENGTH_SHORT).show();
                    return;
                }

                Query q = myRef.child("AllUsers");
                if (LoginActivity.checkWifi(getApplicationContext())) {
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User u = dst.getValue(User.class);
                                if (u.getUsername().equals(etUsername.getText().toString())) {
                                    Toast.makeText(SignUpActivity.this, "שם משתמש כבר קיים", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            User u = new User(etUsername.getText().toString(), etPassword.getText().toString());
                            myRef.child("AllUsers").child(u.getUsername()).setValue(u);
                            Toast.makeText(SignUpActivity.this, "המשתמש נרשם בהצלחה", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else
                    Toast.makeText(SignUpActivity.this,"יש צורך בחיבור רשת",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
