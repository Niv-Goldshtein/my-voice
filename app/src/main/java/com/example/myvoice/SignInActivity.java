package com.example.myvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
/**
 * in this activity you log in to your account
 * the app will check that there is a user like that
 */
public class SignInActivity extends AppCompatActivity {
    private Button btnSignIn;
    private EditText etUsername, etPassword;
    private TextView tvChangePassword;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    private CheckBox cbStayLogged;
    /**
     * activated when this activity opens
     * the user can sign in to his acc
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.btnSignIn);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbStayLogged = findViewById(R.id.cbStayLogged);
        tvChangePassword = findViewById(R.id.tvChangePassword);
        tvChangePassword.setText("שנה את הסיסמא שלך");

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this,ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.getText().toString().equals("")||etPassword.getText().toString().equals("")){
                    Toast.makeText(SignInActivity.this,"אחד או יותר מהשדות ריקים",Toast.LENGTH_SHORT).show();
                    return;
                }

                Query q = myRef.child("AllUsers");
                if (LoginActivity.checkWifi(SignInActivity.this)) {
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean found = false;
                            User u2 = null;
                            SharedPreferences.Editor editor = getSharedPreferences("myTable", MODE_PRIVATE).edit();

                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User u1 = dst.getValue(User.class);
                                if (u1.getUsername().equals(etUsername.getText().toString()) && u1.getPassword().equals(etPassword.getText().toString())) {
                                    u2 = u1;
                                    if (u1.HasArray())
                                        u2.setMyHistory(u1.getMyHistory());
                                    found = true;
                                }
                            }
                            if (found) {
                                if (cbStayLogged.isChecked()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(u2);
                                    editor.putString("User", json);
                                    editor.putBoolean("save", true);
                                } else
                                    editor.putBoolean("save", false);
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(SignInActivity.this, MainPageActivity.class);
                                intent.putExtra("User", u2);
                                Toast.makeText(SignInActivity.this,"המשתמש התחבר בהצלחה",Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                            if (!found)
                                Toast.makeText(SignInActivity.this, "שם משתמש או סיסמא לא נכונים", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else
                    Toast.makeText(SignInActivity.this,"יש צורך בחיבור רשת",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
