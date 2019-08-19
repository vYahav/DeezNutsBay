package vanunu.deeznuts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText nameET, priceET, descriptionET;
    private TextView LinkTV;
    private Spinner spinner;
    private CustomProgressDialog dialog;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private String name, price, description;
    private static final int REQUEST_CODE = 2;
    private int id = 888;
    private String url;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_catalog:
                    Intent intent1 = new Intent(AddProductActivity.this, CatalogActivity.class);
                    intent1.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent1);
                    finish();
                    return true;
                case R.id.navigation_myaccount:
                    Intent intent3 = new Intent(AddProductActivity.this, ProfileActivity.class);
                    intent3.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent3);
                    finish();
                    return true;
                case R.id.navigation_myproducts:
                    if(Integer.valueOf( (String) getIntent().getExtras().get("count"))>0) {
                        Intent intent = new Intent(AddProductActivity.this, ProductsListActivity.class);
                        intent.putExtra("count", (String) getIntent().getExtras().get("count"));
                        intent.putExtra("rank", "UserSelling");
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
                    Toast.makeText(getApplicationContext(),"Already on this page!",Toast.LENGTH_SHORT).show();
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
        navigation.getMenu().getItem(4).setChecked(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        dialog=new CustomProgressDialog(AddProductActivity.this,2);
        firebaseAuth = FirebaseAuth.getInstance();//check if user is logged in, if not send him to login page.
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.CatalogListArray, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        nameET = (EditText) findViewById(R.id.editTextProductName);
        priceET = (EditText) findViewById(R.id.editTextProductPrice);
        descriptionET = (EditText) findViewById(R.id.editTextProductDescription);
        LinkTV = (TextView) findViewById(R.id.textViewLink);
        LinkTV.setText("");
        mStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                id = dataSnapshot.child("ProductsID").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {/*return;*/}
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 111); //this is good for stacking up the permission request
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 98);
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            }
        }
    }




    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK)
        {
            dialog.show();
            Uri ImageData=data.getData();
            StorageReference filepath=mStorage.child("Product"+String.valueOf(id));

            filepath.putFile(ImageData).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddProductActivity.this,"Uploaded image successfully!",Toast.LENGTH_SHORT).show();
                    url=taskSnapshot.getDownloadUrl().toString();//PICTURE
                    LinkTV.setText(url);//PICTURE
                    dialog.dismiss();
                }
            });
        }
    }

    public void onClickAddImageGallery(View v)
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (id != 888) {


                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_CODE);//starting onActivityResult (above onClickAddImage)

            } else {
                Toast.makeText(this, "Please wait a few moments...", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(AddProductActivity.this,"You didn't allow us to access your storage :(",Toast.LENGTH_SHORT).show();
        }

    }
    public void onClickAddImageCamera(View v)
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (id != 888) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                Toast.makeText(this, "Please wait a few moments...", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(AddProductActivity.this,"You didn't allow us to access your camera :(",Toast.LENGTH_SHORT).show();
        }

    }


    public void onClickAddProduct(View v) {
        if (!nameET.getText().toString().isEmpty() && !priceET.getText().toString().isEmpty() && !descriptionET.getText().toString().isEmpty()) {
            if(Integer.valueOf(priceET.getText().toString().trim()) > 0 && Integer.valueOf(priceET.getText().toString().trim()) <=1000) {
                if (LinkTV.getText().toString() != "") {
                    if (id != 888) {
                        name = nameET.getText().toString().trim();
                        price = priceET.getText().toString().trim();
                        description = descriptionET.getText().toString().trim();

                        url = LinkTV.getText().toString();//PICTURE

                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                id = dataSnapshot.child("ProductsID").getValue(Integer.class);
                                databaseReference.child("ProductsID").setValue(id + 1);
                                ProductClass productClass = new ProductClass(name, price, description, id, url, FirebaseAuth.getInstance().getCurrentUser().getUid(), spinner.getSelectedItem().toString(),CountryLookup(),dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getGroupID());
                                databaseReference.child("Product" + String.valueOf(id)).setValue(productClass);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        Toast.makeText(AddProductActivity.this, "Product Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(this, "Please wait a few moments...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please wait until the image is uploaded or upload an image if haven't done so already!", Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Toast.makeText(this, "Price must be above 0 & below 1000!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Please enter a value inside all the fields.", Toast.LENGTH_SHORT).show();
        }
    }



    public String CountryLookup()
    {
        String country="antarctica";

        Location location;
        LocationManager locationManager;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(addressList.size()>0) {
                    country =addressList.get(0).getCountryName();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Location not found! :(", Toast.LENGTH_SHORT).show();
            }
        }
        return country;
    }

}
