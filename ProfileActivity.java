package vanunu.deeznuts;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailtv, balancetv,grouptv;
    private EditText nameET,creditcardET,addressET;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private UserClass userClass;
    private Group group;
    private FirebaseUser user, user1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_catalog:
                    Intent intent1 = new Intent(ProfileActivity.this, CatalogActivity.class);
                    intent1.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent1);
                    finish();
                    return true;
                case R.id.navigation_myaccount:
                    Toast.makeText(getApplicationContext(),"Already on this page!",Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_myproducts:
                    if(Integer.valueOf( (String) getIntent().getExtras().get("count"))>0) {
                        Intent intent = new Intent(ProfileActivity.this, ProductsListActivity.class);
                        intent.putExtra("count",  (String) getIntent().getExtras().get("count"));
                        intent.putExtra("rank","UserSelling");
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please sell a product in order to access this page!",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                case R.id.navigation_addproduct:
                    Intent intent3 = new Intent(ProfileActivity.this, AddProductActivity.class);
                    intent3.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent3);
                    finish();
                    return true;
            }
            return false;
        }

    };
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(0).setChecked(true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        emailtv = (TextView) findViewById(R.id.textView3);
        balancetv = (TextView) findViewById(R.id.textViewProfileBalance);
        nameET = (EditText) findViewById(R.id.editTextName5);
        creditcardET = (EditText) findViewById(R.id.editTextCreditcard1);
        addressET = (EditText) findViewById(R.id.editTextAddress);
        grouptv = (TextView) findViewById(R.id.textView19);
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        FirebaseUser user = firebaseAuth.getCurrentUser();
        emailtv.setText(user.getEmail());
        user1 = firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userClass = dataSnapshot.child(user1.getUid()).getValue(UserClass.class);
                nameET.setText(userClass.GetName());
                balancetv.setText(String.valueOf(userClass.getBalance()));
                creditcardET.setText(userClass.getCreditcard());
                addressET.setText(userClass.getAddress());
                group= dataSnapshot.child("Group" + userClass.getGroupID()).getValue(Group.class);
                grouptv.setText(group.getName()+":"+group.getCode());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onClickSave(View v) {

        user = firebaseAuth.getCurrentUser();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userClass = dataSnapshot.child(user.getUid()).getValue(UserClass.class);
                String name = nameET.getText().toString().trim();
                String creditcard = creditcardET.getText().toString().trim();
                String address = addressET.getText().toString().trim();
                userClass.setName(name);
                userClass.setAddress(address);
                userClass.setCreditcard(creditcard);
                databaseReference.child(user.getUid()).setValue(userClass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();


    }

    public void onClickLogOut(View v) {

        startActivity(new Intent(this, LoginActivity.class));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserClass userClass = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class);
                userClass.SetFCMCode("null");
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userClass);
                finish();
                firebaseAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void AddFundsB(View v) {
        startActivity(new Intent(this, AddFundsToBalanceActivity.class));
    }

}
