package vanunu.deeznuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchProductActivity extends Activity {
    private EditText searchbox;
    private TextView tv;
    private ListView list;
    private String usergroupid;
    private String[] TitleArr, PriceArr, ImageUrlArr, IdArr;
    private DatabaseReference databaseReference;
    private Integer id=0,MaxID;
    private ProductClass productClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        searchbox = findViewById(R.id.editTextSearchBox);
         tv=findViewById(R.id.textViewsearch);
        list = findViewById(R.id.ListViewsearch);
        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Populate(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
    private void Populate(final CharSequence charSequence) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        id=0;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usergroupid=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class).getGroupID();
                Integer count=0;
                MaxID = dataSnapshot.child("ProductsID").getValue(Integer.class);
                while (id < MaxID) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if (productClass.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) && productClass.getStatus() &&!(productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&&usergroupid.equals(productClass.getGroupid())) {
                        count++;
                    }
                    id++;
                }
                //======================================================================
                Integer i = 0;
                String[] status = new String[count];
                ProductClass[] Products=new ProductClass[count];
                id=0;
                while (id < MaxID) {
                    productClass = dataSnapshot.child("Product" + String.valueOf(id)).getValue(ProductClass.class);
                    if (productClass.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) && productClass.getStatus()&&!(productClass.getOwner()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&&usergroupid.equals(productClass.getGroupid())) {
                        Products[i] = productClass;
                        status[i] = "neutral";
                        i++;
                    }
                    id++;
                }

                TitleArr = ProductsArrayToTitleArray(Products);
                PriceArr = ProductsArrayToPriceArray(Products);
                ImageUrlArr = ProductsArrayToImageLinkArray(Products);
                IdArr = ProductsArrayToProductIdArray(Products);
                ProductListViewClass productListViewClass = new ProductListViewClass(SearchProductActivity.this, TitleArr, PriceArr, ImageUrlArr, IdArr, status);
                list.setAdapter(productListViewClass);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedproductid=IdArr[i];
                        Toast.makeText(getApplicationContext(),selectedproductid,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SearchProductActivity.this, ProductActivity.class);
                        intent.putExtra("intention","Search");
                        intent.putExtra("id", selectedproductid);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}