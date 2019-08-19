package vanunu.deeznuts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CreateAGroupActivity extends Activity {
    private EditText nameET;
    private String name;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_agroup);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        nameET = findViewById(R.id.editText5);
    }


    public String getToken() {
        Random random = new Random();
        String CHARS = "ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return token.toString();
    }


    public void onClickCreateG(View v) {
        name = nameET.getText().toString().trim();
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer ID;
                String token="";
                Boolean check=false;

                ID = Integer.valueOf(dataSnapshot.child("GroupID").getValue(String.class));


                while(!check) {
                   token = getToken();
                    check = true;
                    for (int i = 0; i < ID; i++) {
                        if (dataSnapshot.child("Group" + i).getValue(Group.class).getCode().equals(token)) {
                            check = false;
                        }
                    }
                }
                Group g = new Group(name, token, String.valueOf(ID));
                 databaseReference.child("Group" + ID).setValue(g);
                 databaseReference.child("GroupID").setValue(String.valueOf(ID + 1));
                 AlertDialog.Builder builder1 = new AlertDialog.Builder(CreateAGroupActivity.this);
                 builder1.setMessage("Group Code:" + token)
                            .setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });

                    builder1.create().show();
                    Toast.makeText(CreateAGroupActivity.this,"Alright m8",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }
}
