package vanunu.deeznuts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.id.message;

public class ProductsListActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ProductClass productClass;
    private TextView title;
    private ProductClass[] Products;
    private SharedPreferences sharedPreferences;
    ListView lst;
    SharedPreferences.Editor x;
    String rank;
    private Integer id = 0, MaxID;
    private String[] TitleArr, PriceArr, ImageUrlArr, IdArr;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(ProductsListActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_catalog:
                    Intent intent2 = new Intent(ProductsListActivity.this, CatalogActivity.class);
                    intent2.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent2);
                    finish();
                    return true;
                case R.id.navigation_myaccount:
                    Intent intent1 = new Intent(ProductsListActivity.this, ProfileActivity.class);
                    intent1.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent1);
                    finish();
                    return true;
                case R.id.navigation_myproducts:
                    if (rank.equals("UserSelling")) Toast.makeText(getApplicationContext(),"Already on this page!",Toast.LENGTH_SHORT).show();
                    if (rank.equals("Admin")||rank.equals("UsersBoughtProducts"))
                    {
                        if(Integer.valueOf((String) getIntent().getExtras().get("count"))>0) {
                            Intent intent = new Intent(getApplicationContext(), ProductsListActivity.class);
                            intent.putExtra("count", (String) getIntent().getExtras().get("count"));
                            intent.putExtra("rank","UserSelling");
                            finish();
                            startActivity(intent);
                            return true;
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Please sell a product in order to access this page!",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    return false;

                case R.id.navigation_addproduct:
                    Intent intent3 = new Intent(ProductsListActivity.this, AddProductActivity.class);
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
        navigation.getMenu().getItem(3).setChecked(true);
        if (rank.equals("Admin")||rank.equals("UsersBoughtProducts")) navigation.getMenu().getItem(2).setChecked(true);//no menu item will be checked
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        title = (TextView) findViewById(R.id.textView27);
        rank = (String) getIntent().getExtras().get("rank");
        if (rank.equals("Admin")) {
            AdminsProductArrayMaker();
            title.setText("All Products");
        }
        if (rank.equals("UserSelling")) {
            Integer NumberOfProducts = Integer.valueOf((String) getIntent().getExtras().get("count"));
            UsersProductArrayMaker(NumberOfProducts);
            title.setText("My Listings");
        }
        if (rank.equals("UsersBoughtProducts")) {
            UsersBoughtProducts();
            title.setText("Products Purchased");
        }
        lst = (ListView) findViewById(R.id.ItemsListView);
        ExplanationPopup();

    }


    private void UsersBoughtProducts() {
        sharedPreferences = getSharedPreferences("MyProducts", Context.MODE_PRIVATE);
        Products = new ProductClass[sharedPreferences.getInt("ProductNum", 0)];
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MaxID = dataSnapshot.child("ProductsID").getValue(Integer.class);
                String[] status = new String[sharedPreferences.getInt("ProductNum", 0)];
                for (int i = 0; i < sharedPreferences.getInt("ProductNum", 0); i++) {
                    productClass = dataSnapshot.child("Product" + sharedPreferences.getInt("Product" + String.valueOf(i), 0)).getValue(ProductClass.class);

                    Products[i] = productClass;
                    status[i] = "neutral";

                }
                TitleArr = ProductsArrayToTitleArray(Products);
                PriceArr = ProductsArrayToPriceArray(Products);
                ImageUrlArr = ProductsArrayToImageLinkArray(Products);
                IdArr = ProductsArrayToProductIdArray(Products);
                ProductListViewClass productListViewClass = new ProductListViewClass(ProductsListActivity.this, TitleArr, PriceArr, ImageUrlArr, IdArr, status);
                lst.setAdapter(productListViewClass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void AdminsProductArrayMaker() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MaxID = dataSnapshot.child("ProductsID").getValue(Integer.class);
                Products = new ProductClass[MaxID];
                String[] status = new String[MaxID];
                Integer count = 0;
                while (id < MaxID) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if (productClass.getStatus()) {
                        status[count] = "active";
                    } else {
                        status[count] = "inactive";
                    }
                    Products[count] = productClass;
                    count++;


                    id++;
                }
                TitleArr = ProductsArrayToTitleArray(Products);
                PriceArr = ProductsArrayToPriceArray(Products);
                ImageUrlArr = ProductsArrayToImageLinkArray(Products);
                IdArr = ProductsArrayToProductIdArray(Products);
                ProductListViewClass productListViewClass = new ProductListViewClass(ProductsListActivity.this, TitleArr, PriceArr, ImageUrlArr, IdArr, status);
                lst.setAdapter(productListViewClass);


                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        if (Products[position].getStatus()) { //if product is active
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductsListActivity.this);
                            builder1.setMessage("What do you want to do with " + TitleArr[position] + "?")
                                    .setPositiveButton("Delete this product", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                                            builder.setMessage("Are you sure you want to remove " + TitleArr[position] + " from your listed products?")
                                                    .setPositiveButton("Remove this gucci homeboy!", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Products[position].setStatus(false);
                                                            databaseReference.child("Product" + IdArr[position]).setValue(Products[position]);
                                                            Intent intent = new Intent(ProductsListActivity.this, MainActivity.class);
                                                            Toast.makeText(getApplicationContext(),"Product Removed!",Toast.LENGTH_SHORT).show();
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .setNegativeButton("Nah I'm good", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    });

                                            builder.create().show();

                                        }
                                    })
                                    .setNegativeButton("Edit this product", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(ProductsListActivity.this, ProductEditActivity.class);
                                            intent.putExtra("productID", IdArr[position]);
                                            startActivity(intent);
                                        }
                                    });

                            builder1.create().show();
                        }


                        if (!Products[position].getStatus()) { //if product is INACTIVE
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductsListActivity.this);
                            builder1.setMessage("What do you want to do with " + TitleArr[position] + "?")
                                    .setPositiveButton("Revive this product", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                                            builder.setMessage("Are you sure you want to revive " + TitleArr[position] + " ?")
                                                    .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Products[position].setStatus(true);
                                                            databaseReference.child("Product" + IdArr[position]).setValue(Products[position]);
                                                            Intent intent = new Intent(ProductsListActivity.this, MainActivity.class);
                                                            Toast.makeText(getApplicationContext(),"Product Revived!",Toast.LENGTH_SHORT).show();
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    })
                                                    .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                        }
                                                    });
                                            builder.create().show();

                                        }
                                    })
                                    .setNegativeButton("Edit this product", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(ProductsListActivity.this, ProductEditActivity.class);
                                            intent.putExtra("productID", IdArr[position]);
                                            startActivity(intent);
                                        }
                                    });
                            builder1.create().show();
                        }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void UsersProductArrayMaker(Integer arrLength) {
        Products = new ProductClass[arrLength];
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MaxID = dataSnapshot.child("ProductsID").getValue(Integer.class);
                Integer count = 0;
                String[] status = new String[MaxID];
                while (id < MaxID) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if (productClass.getStatus() && (productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Products[count] = productClass;
                        status[count] = "neutral";
                        count++;

                    }
                    id++;
                }
                TitleArr = ProductsArrayToTitleArray(Products);
                PriceArr = ProductsArrayToPriceArray(Products);
                ImageUrlArr = ProductsArrayToImageLinkArray(Products);
                IdArr = ProductsArrayToProductIdArray(Products);

                ProductListViewClass productListViewClass = new ProductListViewClass(ProductsListActivity.this, TitleArr, PriceArr, ImageUrlArr, IdArr, status);
                lst.setAdapter(productListViewClass);


                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductsListActivity.this);
                        builder1.setMessage("What do you want to do with " + TitleArr[position] + "?")
                                .setPositiveButton("Delete this product", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                                        builder.setMessage("Are you sure you want to remove " + TitleArr[position] + " from your listed products?")
                                                .setPositiveButton("Remove!", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        Products[position].setStatus(false);
                                                        databaseReference.child("Product" + IdArr[position]).setValue(Products[position]);
                                                        startActivity(new Intent(ProductsListActivity.this, MainActivity.class));
                                                        Toast.makeText(getApplicationContext(),"Product Removed!",Toast.LENGTH_SHORT).show();
                                                        finish();


                                                    }
                                                })
                                                .setNegativeButton("Changed my mind!", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                    }
                                                });

                                        builder.create().show();

                                    }
                                })
                                .setNegativeButton("Edit this product", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(ProductsListActivity.this, ProductEditActivity.class);
                                        intent.putExtra("productID", IdArr[position]);
                                        startActivity(intent);
                                    }
                                });

                        builder1.create().show();

                    }
                });


                lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   final int pos, long id) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductsListActivity.this);
                        builder1.setMessage("Share Your Product")
                                .setPositiveButton("Whatsapp", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id){
                                        PackageManager pm=getPackageManager();
                                        try {

                                            Intent waIntent = new Intent(Intent.ACTION_SEND);
                                            waIntent.setType("text/plain");
                                            String text = "Hi there! I'd like to show you my product! It's on sale on Deez Nuts Bay, it's name is " + Products[pos].getName() + ", it is in "+ Products[pos].getCatagory() + " category and it costs " + Products[pos].getPrice()+"$!";

                                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA); //this is to check if whatsapp is installed, if this gets an error then the catch will start.
                                            waIntent.setPackage("com.whatsapp");

                                            waIntent.putExtra(Intent.EXTRA_TEXT, text);
                                            startActivity(Intent.createChooser(waIntent, "Share with"));

                                        } catch (PackageManager.NameNotFoundException e) {
                                            Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setNegativeButton("NFC", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                       ActivateNFC(IdArr[pos]);
                                    }
                                });
                        builder1.create().show();


                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
private void ActivateNFC(String id)
{

    NdefRecord ndefRecord = NdefRecord.createMime("text/plain", id.getBytes());
    NdefMessage ndefMessage = new NdefMessage(ndefRecord);
    NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
    if (mAdapter == null) {
        Toast.makeText(getApplicationContext(), "This device doesn't have NFC", Toast.LENGTH_LONG).show();
        return;
    }

    if (!mAdapter.isEnabled()) {
        Toast.makeText(getApplicationContext(), "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        return;
    }
    Toast.makeText(getApplicationContext(),"Now please put the 2 devices close together.", Toast.LENGTH_LONG).show();
    mAdapter.setNdefPushMessage(ndefMessage,this);
}

    private String[] ProductsArrayToTitleArray(ProductClass[] parr) {
        String[] TitleArray = new String[parr.length];
        for (int i = 0; i < parr.length; i++) {
            TitleArray[i] = parr[i].getName();
        }

        return TitleArray;
    }


    private String[] ProductsArrayToPriceArray(ProductClass[] parr) {
        String[] PriceArray = new String[parr.length];
        for (int i = 0; i < parr.length; i++) {
            PriceArray[i] = parr[i].getPrice();
        }

        return PriceArray;
    }


    private String[] ProductsArrayToImageLinkArray(ProductClass[] parr) {
        String[] ImageLinkArray = new String[parr.length];
        for (int i = 0; i < parr.length; i++) {
            ImageLinkArray[i] = parr[i].getImgurl();
        }

        return ImageLinkArray;
    }

    private String[] ProductsArrayToProductIdArray(ProductClass[] parr) {
        String[] ProductIDArray = new String[parr.length];
        for (int i = 0; i < parr.length; i++) {
            ProductIDArray[i] = String.valueOf(parr[i].getId());
        }

        return ProductIDArray;
    }

    private void ExplanationPopup() {
        if (rank.equals("UserSelling")) {
            x = getSharedPreferences("ProductListExplanationPopupUserSelling", Context.MODE_PRIVATE).edit();
            if (getSharedPreferences("ProductListExplanationPopupUserSelling", Context.MODE_PRIVATE).getBoolean("status", true)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                builder.setTitle("Tip of the day!");
                builder.setMessage("This is a list of the products that you are currently selling! Click on a product to delete or edit it! Also, if you want to share your product with your friends on whatsapp or NFC long press the product!")
                        .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                x.putBoolean("status", true);
                                x.apply();
                            }
                        })
                        .setNegativeButton("Do not show again!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                x.putBoolean("status", false);
                                x.apply();
                            }
                        });

                builder.create().show();

            }
        }

        if (rank.equals("UsersBoughtProducts")) {
            x= getSharedPreferences("ProductListExplanationPopupUsersBoughtProducts", Context.MODE_PRIVATE).edit();
            if (getSharedPreferences("ProductListExplanationPopupUsersBoughtProducts", Context.MODE_PRIVATE).getBoolean("status", true)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                builder.setTitle("Ahoy Deez Nutsers! Tip ahead of you!");
                builder.setMessage("This screen shows you the products that you have bought on THIS device! *(Be warned that if you uninstall the app this list will be lost!)")
                        .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                x.putBoolean("status", true);
                                x.apply();
                            }
                        })
                        .setNegativeButton("Do not show again!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                x.putBoolean("status", false);
                                x.apply();
                            }
                        });

                builder.create().show();

            }
        }
        if (rank.equals("Admin")) {
            x = getSharedPreferences("ProductListExplanationPopupAdmin", Context.MODE_PRIVATE).edit();
            if (getSharedPreferences("ProductListExplanationPopupAdmin", Context.MODE_PRIVATE).getBoolean("status", true)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                builder.setTitle("This is a tip for the leaders of Deez-Nutsland!");
                builder.setMessage("On this activity you can see all the products that were ever added to deez nuts! You can either edit them, delete them (make them inactive) or even revive them (make them active)!!")
                        .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                x.putBoolean("status", true);
                                x.apply();
                            }
                        })
                        .setNegativeButton("Do not show again!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                x.putBoolean("status", false);
                                x.apply();
                            }
                        });

                builder.create().show();

            }
        }
    }
}


