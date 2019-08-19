package vanunu.deeznuts;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage,count,productcountview;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private CustomProgressDialog dialog;
    private UserClass user1;
    private final int counter=0;
    private Button AllProductsListB,createagroup;
    DataSnapshot userSnapshot;
    private Integer id = 0, MaxID;
    private ProductClass productClass;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_catalog:
                    Intent intent2 = new Intent(MainActivity.this, CatalogActivity.class);
                    intent2.putExtra("count", count.getText().toString());
                    startActivity(intent2);
                    finish();
                    return true;
                case R.id.navigation_myaccount:
                    Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                    intent1.putExtra("count", count.getText().toString());
                    startActivity(intent1);
                    finish();
                    return true;
                case R.id.navigation_myproducts:
                    if(Integer.valueOf(count.getText().toString())>0) {
                        Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
                        intent.putExtra("count", count.getText().toString());
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
                    Intent intent3 = new Intent(MainActivity.this, AddProductActivity.class);
                    intent3.putExtra("count", count.getText().toString());
                    startActivity(intent3);
                    finish();
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog=new CustomProgressDialog(MainActivity.this,2);
        dialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            dialog.dismiss();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            mTextMessage = (TextView) findViewById(R.id.message);


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
            } else {
                Toast.makeText(this, "All gucci", Toast.LENGTH_SHORT).show();
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NFC}, 99);
            }
            count = (TextView) findViewById(R.id.textViewcount);
            productcountview = (TextView) findViewById(R.id.textViewProductCountView);

            SetFirebaseCloudMessagesCode();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            user = firebaseAuth.getCurrentUser();
            AllProductsListB = (Button) findViewById(R.id.button4AllProducts);
            createagroup=(Button) findViewById(R.id.button10);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    user1 = dataSnapshot.child(user.getUid()).getValue(UserClass.class);
                    mTextMessage.setText("Welcome, " + user1.GetName());
                    if (user1.getRank().equals("Admin")) {
                        AllProductsListB.setVisibility(View.VISIBLE);
                        createagroup.setVisibility(View.VISIBLE);
                    }
                    if (user1.getProductsoldstatus()) {
                        ProductSoldNotification();
                        user1.setProductsoldstatus(false);
                        databaseReference.child(user.getUid()).setValue(user1);
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mTextMessage.setText("fail");
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(2).setChecked(true);
        UsersProductCounter();
    }



    public void OnClickAllProductsListB(View v) {
        Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
        intent.putExtra("rank","Admin");
        intent.putExtra("count", count.getText().toString());
        startActivity(intent);
    }
    public void onClickPurchasedProducts(View v) {
        Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
        intent.putExtra("rank","UsersBoughtProducts");
        intent.putExtra("count", count.getText().toString());
        startActivity(intent);

    }
    public void CreateAGroup(View v) {
        Intent intent = new Intent(MainActivity.this, CreateAGroupActivity.class);
        startActivity(intent);
    }

    public void UsersProductCounter() {
        id=0;
        count.setText("0");
        productcountview.setText("You have "+count.getText().toString()+" products on sale.");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MaxID = dataSnapshot.child("ProductsID").getValue(Integer.class);
                while (id < MaxID) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);

                    if (productClass.getStatus() && (productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        count.setText(String.valueOf(Integer.valueOf(count.getText().toString())+1));
                        productcountview.setText("You have "+count.getText().toString()+" products on sale.");
                    }
                    id++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void SetFirebaseCloudMessagesCode()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserClass userClass = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class);
                userClass.SetFCMCode(FirebaseInstanceId.getInstance().getToken());
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userClass);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }





    public void ProductSoldNotification()
    {

                                NotificationCompat.Builder notification=new NotificationCompat.Builder(MainActivity.this);
                notification.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher).setTicker("Deez Nuts Bay: Product sold/deleted!").setWhen(System.currentTimeMillis()).setContentTitle("Product has been sold").setContentText(String.valueOf("Product/s has been sold!"));
                Intent intent = new Intent(MainActivity.this,ProductsListActivity.class);
                PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);
                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.notify(45612,notification.build());



    }



}
