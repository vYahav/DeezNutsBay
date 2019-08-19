package vanunu.deeznuts;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.jar.Attributes;

public class ProductEditActivity extends AppCompatActivity {
    private EditText NameET, PriceET, DescriptionET;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private ProductClass productClass;
    private String id,url,intent1successcode="null";
    private Intent intent1;
    private CustomProgressDialog dialog;
    private StorageReference mStorage;
    private static final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        id = (String) getIntent().getExtras().get("productID");
        NameET = (EditText) findViewById(R.id.editTextName56);
        PriceET = (EditText) findViewById(R.id.editTextPrice56);
        DescriptionET = (EditText) findViewById(R.id.editTextDescription56);
        imageView = (ImageView) findViewById(R.id.imageViewImage5);
        dialog=new CustomProgressDialog(ProductEditActivity.this,2);
        mStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productClass = dataSnapshot.child("Product" + id).getValue(ProductClass.class);
                NameET.setText(productClass.getName());
                PriceET.setText(productClass.getPrice());
                DescriptionET.setText(productClass.getDescription());
                Picasso.with(ProductEditActivity.this).load(productClass.getImgurl().toString()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        intent1successcode="null";
    }

    public void onClickProductSaveButton6(View v) {
        if (!NameET.getText().toString().isEmpty() && !PriceET.getText().toString().isEmpty() && !DescriptionET.getText().toString().isEmpty()) {
            if (Integer.valueOf(PriceET.getText().toString().trim()) > 0 && Integer.valueOf(PriceET.getText().toString().trim()) <=1000) {
                productClass.setName(NameET.getText().toString().trim());
                productClass.setPrice(PriceET.getText().toString().trim());
                productClass.setDescription(DescriptionET.getText().toString().trim());
                if(intent1successcode.equals("okay")) {
                    dialog.show();
                    Toast.makeText(ProductEditActivity.this, "test1", Toast.LENGTH_SHORT).show();
                    Uri ImageData = intent1.getData();
                    StorageReference filepath = mStorage.child("Product" + String.valueOf(id));
                    filepath.putFile(ImageData).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductEditActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProductEditActivity.this, "Uploaded image successfully!", Toast.LENGTH_SHORT).show();
                            url = taskSnapshot.getDownloadUrl().toString();//PICTURE
                            productClass.setImgurl(url);
                            dialog.dismiss();
                        }
                    });
                }
                databaseReference.child("Product" + id).setValue(productClass);
                finish();
                startActivity(new Intent(ProductEditActivity.this, MainActivity.class));
            } else {
                Toast.makeText(ProductEditActivity.this, "Price must be above 0 & under 1000!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ProductEditActivity.this, "All fields must be entered!", Toast.LENGTH_SHORT).show();
        }
    }



    public void onClickAddImageFromGallery(View v)
    {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                 Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            Toast.makeText(ProductEditActivity.this,"You didn't allow us to access your storage :(",Toast.LENGTH_SHORT).show();
        }

    }
    public void onClickAddImageFromCamera(View v)
    {
        Toast.makeText(ProductEditActivity.this,"lol",Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE);
            Toast.makeText(ProductEditActivity.this,":)",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ProductEditActivity.this,"You didn't allow us to access your camera :(",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK) {
            intent1 = data;
            intent1successcode="okay";
            imageView.setImageURI(intent1.getData());
        }
    }
}
