package vanunu.deeznuts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private EditText fullname, username, password, creditcard, email, nameET, creditcardET, addressET,groupcodeET;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Location location;
    private CustomProgressDialog progress;
    private LocationManager locationManager;
    private Boolean GroupCodeExists=false;
    private String groupcode,GroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progress= new CustomProgressDialog(SignupActivity.this,2);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        nameET = (EditText) findViewById(R.id.editTextFullName);
        creditcardET = (EditText) findViewById(R.id.editTextCreditCard);
        addressET = (EditText) findViewById(R.id.editTextAddress);
        groupcodeET = (EditText) findViewById(R.id.editTextGroupCode);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
        else
        {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addressList.size()>0) {
                        addressET.setText(addressList.get(0).getAddressLine(0));
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Location not found! :(", Toast.LENGTH_SHORT).show();
                }
        }

    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

                if(requestCode==123)
                {

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if(addressList.size()>0) {
                                String city=addressList.get(0).getAddressLine(0);
                                addressET.setText(city);
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Location not found! :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        }

    public void onClickSignupB(View v) {
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String name=nameET.getText().toString().trim();
        String credit=creditcardET.getText().toString().trim();
        String address=addressET.getText().toString().trim();
        groupcode= groupcodeET.getText().toString().trim();
        if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) || name.isEmpty() || credit.isEmpty() || address.isEmpty() || groupcode.isEmpty())
        {
            Toast.makeText(this,"Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pass.length()<6)
        {
            Toast.makeText(this,"Password cannot be shorter than 6 letters!", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progress.show();
                GroupCodeExists = false;
                Integer count = 0;
                Integer MaxID = Integer.valueOf(dataSnapshot.child("GroupID").getValue(String.class));
                while (!GroupCodeExists && count < MaxID) {
                    if (groupcode.equals(dataSnapshot.child("Group" + String.valueOf(count)).getValue(Group.class).getCode())) {
                        GroupCodeExists = true;
                        GroupID=String.valueOf(count);
                    }
                    count++;
                }

                if (GroupCodeExists) {
                    progress.dismiss();
                    RegisterUser();
                } else {
                    progress.dismiss();
                    Toast.makeText(SignupActivity.this, "Group does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }});


    }

    public void RegisterUser()
    {
        progress.show();
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString().trim();


        firebaseAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progress.cancel();
                            String name=nameET.getText().toString().trim();
                            String credit=creditcardET.getText().toString().trim();
                            String address=addressET.getText().toString().trim();
                            UserClass userClass= new UserClass(name,credit,address,GroupID);
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            databaseReference.child(user.getUid()).setValue(userClass);


                            Toast.makeText(SignupActivity.this,"Registered Successfully!",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            progress.cancel();
                            Toast.makeText(SignupActivity.this,"Registration Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
