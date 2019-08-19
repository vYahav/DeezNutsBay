package vanunu.deeznuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {
    private TextView titleTV,priceTV,descriptionTV,whatsappb,countryTV;
    private ImageView imageIV,countryIMG;
    private String category,usergroupid;
    private DatabaseReference databaseReference;
    private ProductClass productClass;
    private Button PurchaseB,NextB,PreviousB;
    private SharedPreferences sharedPreferences;
    private CustomProgressDialog dialog;
    private int id=0,MaxID;
    private boolean LoopFix; //this is to fix infinite looping.
    public RemoteMessage remoteMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        titleTV = (TextView) findViewById(R.id.ProductTitle);
        imageIV = (ImageView) findViewById(R.id.ProductImage);
        priceTV = (TextView) findViewById(R.id.ProductPricee);
        descriptionTV = (TextView) findViewById(R.id.ProductDescription);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        PurchaseB = (Button) findViewById(R.id.PurchaseB);
        NextB = (Button) findViewById(R.id.NextB);
        PreviousB = (Button) findViewById(R.id.PreviousB);
        countryIMG = (ImageView) findViewById(R.id.imageView6);
        countryTV = (TextView) findViewById(R.id.textView29);
        whatsappb = (Button) findViewById(R.id.whatsappbutton);

        if (getIntent().getExtras().get("intention").equals("Search")) {
            dialog = new CustomProgressDialog(ProductActivity.this, 2);
            id = Integer.valueOf((String) getIntent().getExtras().get("id"));
            buildproductsfromprovidedid();

        }
        if (getIntent().getExtras().get("intention").equals("Category"))
        {
            category = (String) getIntent().getExtras().get("Category");
            dialog = new CustomProgressDialog(ProductActivity.this, 2);
            buildproductsfromcategory();
        }
    }


    public void buildproductsfromprovidedid() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productClass=dataSnapshot.child("Product"+String.valueOf(id)).getValue(ProductClass.class);
                    titleTV.setText(productClass.getName());
                    priceTV.setText(productClass.getPrice());
                    descriptionTV.setText(productClass.getDescription());
                    Picasso.with(ProductActivity.this).load(productClass.getImgurl().toString()).into(imageIV);
                    String country=productClass.getCountry().toLowerCase();
                    Picasso.with(ProductActivity.this).load("http://img.freeflagicons.com/thumb/round_icon/"+country+"/"+country+"_640.png").into(countryIMG);
                    countryTV.setText(country.toUpperCase());
                    if (country.toLowerCase().equals("antarctica")) countryTV.setText("Country Not Found.");
                    priceTV.setVisibility(View.VISIBLE);
                    descriptionTV.setVisibility(View.VISIBLE);
                    countryTV.setVisibility(View.VISIBLE);
                    PurchaseB.setVisibility(View.VISIBLE);
                    countryTV.setVisibility(View.VISIBLE);
                    countryIMG.setVisibility(View.VISIBLE);
                    whatsappb.setVisibility(View.VISIBLE);
                    dialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }










    public void buildproductsfromcategory()
    {
        id=0;
        dialog.show();
        LoopFix=true;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usergroupid=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getGroupID();
                productClass=dataSnapshot.child("Product"+String.valueOf(id)).getValue(ProductClass.class);
                MaxID=dataSnapshot.child("ProductsID").getValue(Integer.class);
                if(productClass.getStatus()&& !(productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && productClass.getCatagory().equals(category)&&usergroupid.equals(productClass.getGroupid())) {
                    titleTV.setText(productClass.getName());
                    priceTV.setText(productClass.getPrice());
                    descriptionTV.setText(productClass.getDescription());
                    Picasso.with(ProductActivity.this).load(productClass.getImgurl().toString()).into(imageIV);
                    String country=productClass.getCountry().toLowerCase();
                    Picasso.with(ProductActivity.this).load("http://img.freeflagicons.com/thumb/round_icon/"+country+"/"+country+"_640.png").into(countryIMG);
                    countryTV.setText(country.toUpperCase());
                    if (country.toLowerCase().equals("antarctica")) countryTV.setText("Country Not Found.");
                    NextB.setVisibility(View.VISIBLE);
                    PreviousB.setVisibility(View.VISIBLE);
                    priceTV.setVisibility(View.VISIBLE);
                    descriptionTV.setVisibility(View.VISIBLE);
                    countryTV.setVisibility(View.VISIBLE);
                    PurchaseB.setVisibility(View.VISIBLE);
                    countryTV.setVisibility(View.VISIBLE);
                    countryIMG.setVisibility(View.VISIBLE);
                    whatsappb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                else
                {
                    onClickNextB(new View(ProductActivity.this));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    public void onClickNextB(final View v)
    {
       id++;
        dialog.show();
        if(id<MaxID)
        {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usergroupid=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getGroupID();
                    productClass=dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if(productClass.getStatus()&& !(productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&& productClass.getCatagory().equals(category)&&usergroupid.equals(productClass.getGroupid())) {
                        titleTV.setText(productClass.getName());
                        priceTV.setText(productClass.getPrice());
                        descriptionTV.setText(productClass.getDescription());
                        Picasso.with(ProductActivity.this).load(productClass.getImgurl().toString()).into(imageIV);
                        String country=productClass.getCountry().toLowerCase();
                        Picasso.with(ProductActivity.this).load("http://img.freeflagicons.com/thumb/round_icon/"+country+"/"+country+"_640.png").into(countryIMG);
                        countryTV.setText(country.toUpperCase());
                        if (country.toLowerCase().equals("antarctica")) countryTV.setText("Country Not Found.");
                        NextB.setVisibility(View.VISIBLE);
                        PreviousB.setVisibility(View.VISIBLE);
                        priceTV.setVisibility(View.VISIBLE);
                        descriptionTV.setVisibility(View.VISIBLE);
                        countryTV.setVisibility(View.VISIBLE);
                        PurchaseB.setVisibility(View.VISIBLE);
                        countryTV.setVisibility(View.VISIBLE);
                        countryIMG.setVisibility(View.VISIBLE);
                        whatsappb.setVisibility(View.VISIBLE);
                        LoopFix=true;
                        dialog.dismiss();
                        if(id==(MaxID-1)) LoopFix=false; //if there is a last product, there will be no next products therfore calling previous product
                    }                                    //will not be needed so we set loopfix to false so there will be no Previous action called (bug fix v.35+-)
                    else {
                    onClickNextB(v);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}});
        }
        else {

            if (LoopFix) {
                LoopFix=false;
                onClickPreviousB(v); //this is to get back to the last product the user was on before trying to move forward without success
            }                       //(moving forward without success changes the current's product id to an incorrect product) (bug fix v.35+-)
            else{
                dialog.dismiss();
                id--;
                Toast.makeText(this, "Can't go anymore forward!", Toast.LENGTH_SHORT).show();
            }

        }
        Toast.makeText(this, "Forward: "+ String.valueOf(id), Toast.LENGTH_SHORT).show();
    }


    public void onClickPreviousB(final View v)
    {
        if(!LoopFix && id==(MaxID-1)) LoopFix=true; //this is if the user wants to go back and from the last product (bug fix v.41)
        id--;
        dialog.show();
        if(id>=0)
        {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usergroupid=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getGroupID();
                    productClass=dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if(productClass.getStatus() &&!(productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&& productClass.getCatagory().equals(category)&&usergroupid.equals(productClass.getGroupid())) {
                        titleTV.setText(productClass.getName());
                        priceTV.setText(productClass.getPrice());
                        descriptionTV.setText(productClass.getDescription());
                        Picasso.with(ProductActivity.this).load(productClass.getImgurl().toString()).into(imageIV);
                        String country = productClass.getCountry().toLowerCase();
                        Picasso.with(ProductActivity.this).load("http://img.freeflagicons.com/thumb/round_icon/" + country + "/" + country + "_640.png").into(countryIMG);
                        countryTV.setText(country.toUpperCase());
                        if (country.toLowerCase().equals("antarctica")) countryTV.setText("Country Not Found.");
                        NextB.setVisibility(View.VISIBLE);
                        PreviousB.setVisibility(View.VISIBLE);
                        priceTV.setVisibility(View.VISIBLE);
                        descriptionTV.setVisibility(View.VISIBLE);
                        countryTV.setVisibility(View.VISIBLE);
                        PurchaseB.setVisibility(View.VISIBLE);
                        countryTV.setVisibility(View.VISIBLE);
                        countryIMG.setVisibility(View.VISIBLE);
                        whatsappb.setVisibility(View.VISIBLE);
                        LoopFix=true;
                        dialog.dismiss();
                    }
                    else
                    {
                        onClickPreviousB(v);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}});
        }
        else {
            if (LoopFix) {
                LoopFix=false;
                onClickNextB(v); //this is to get back to the last product the user was on before trying to move backwards without success
            }                   //(moving forward without success changes the current's product id to a not correct product) (bug fix v.35+-)
            else
            {
                dialog.dismiss();
                id++;
                Toast.makeText(this, "Can't go anymore backwards!", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "Backward: " +String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

    public void onClickPurchaseB(View v)
    {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getBalance() >= Integer.valueOf(productClass.getPrice())) {
                        productClass.setStatus(false);//"Deleting" the product
                        databaseReference.child("Product" + String.valueOf(id)).setValue(productClass);
                        //substructing the product price from the user who bought it
                        UserClass userClass = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class);
                        userClass.SubstructFromBalance(Integer.valueOf(productClass.getPrice()));
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userClass);
                        //
                        //Adding the product price to the owner/seller's balance
                        userClass = dataSnapshot.child(productClass.getOwner()).getValue(UserClass.class);
                        userClass.AddToBalance(Integer.valueOf(productClass.getPrice()));
                        userClass.setProductsoldstatus(true);
                        databaseReference.child(productClass.getOwner()).setValue(userClass);
                        Toast.makeText(ProductActivity.this, "Product was purchased successfuly! The price was deducted from your balance and the product with be shipped to you soon!", Toast.LENGTH_LONG).show();

//===========================================================================================================================================================================================
                        sharedPreferences=getSharedPreferences("MyProducts", Context.MODE_PRIVATE); // saving the purchased product to put it on Bought Products on this device.
                        if(!(sharedPreferences.getInt("ProductNum",0)>=0)) {
                            SharedPreferences.Editor x = sharedPreferences.edit();
                            x.putInt("ProductNum", 0);
                            x.apply();
                        }
                        sharedPreferences.edit().putInt("Product"+String.valueOf(sharedPreferences.getInt("ProductNum",0)),productClass.getId()).apply();

                        sharedPreferences.edit().putInt("ProductNum", sharedPreferences.getInt("ProductNum",0)+1).apply();

//===========================================================================================================================================================================================
                        MediaPlayer.create(getApplicationContext(),R.raw.deez_nuts_sound_effect).start();
                        finish();
                    } else {
                        Toast.makeText(ProductActivity.this, "You don't have enough funds to complete the purchase!", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


    }
    public void onClickWhatsappB(View v)
    {
        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Just wanted to share this awesome product I found! It's on sale on Deez Nuts Bay, it's name is " + productClass.getName() + ", it is in "+ productClass.getCatagory() + " category and it costs " + productClass.getPrice()+"$!";

            pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA); //this is to check if whatsapp is installed, if this gets an error then the catch will start.
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }
}
