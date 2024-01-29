package com.example.myvoice;

import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

/**
 * in this activity the user can change his password
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private Button btnChangePassword;
    private EditText etUsername,etOldPassword,etNewPassword,etNewPasswordAgain;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    /**
     * connect the layout with the code and change the user's password
     * activated when the activity opens
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etUsername = findViewById(R.id.etUsername);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPasswordAgain = findViewById(R.id.etNewPasswordAgain);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query q = myRef.child("AllUsers");
                if (etUsername.getText().toString().equals("")||etOldPassword.getText().toString().equals("")
                        ||etNewPassword.getText().toString().equals("")||etNewPasswordAgain.getText().toString().equals("")){
                    Toast.makeText(ChangePasswordActivity.this,"אחד או יותר מהשדות ריקים",Toast.LENGTH_LONG).show();
                    return;
                }

                if (etOldPassword.getText().toString().equals(etNewPassword.getText().toString())
                        &&etOldPassword.getText().toString().equals(etNewPasswordAgain.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"לא ניתן להחליף סיסמא חדשה בישנה",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (LoginActivity.checkWifi(ChangePasswordActivity.this)) {
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dst : dataSnapshot.getChildren()) {
                                User u1 = dst.getValue(User.class);
                                if (u1.getUsername().equals(etUsername.getText().toString()) && u1.getPassword().equals(etOldPassword.getText().toString())
                                        && etNewPassword.getText().toString().equals(etNewPasswordAgain.getText().toString())) {
                                    u1.setPassword(etNewPassword.getText().toString());
                                    myRef.child("AllUsers").child(u1.getUsername()).setValue(u1);
                                    Toast.makeText(ChangePasswordActivity.this, "הסיסמא השתנתה בהצלחה", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                            }
                            Toast.makeText(ChangePasswordActivity.this, "אחד או יותר מהשדות לא נכונים", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    Toast.makeText(ChangePasswordActivity.this,"יש צורך בחיבור רשת",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
