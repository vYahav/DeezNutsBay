package vanunu.deeznuts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFundsToBalanceActivity extends Activity {
    private EditText AmountOfFunds;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds_to_balance);
        AmountOfFunds=(EditText) findViewById(R.id.editTextAddFunds);
    }

    public void AddFundsButton(View v)
    {
         final String FundsToAdd=AmountOfFunds.getText().toString().trim();
        if(Integer.valueOf(FundsToAdd)<=500&&Integer.valueOf(FundsToAdd)>0) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserClass userClass = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserClass.class);
                    Integer currentfunds = userClass.getBalance();
                    Integer TotalBalance = Integer.valueOf(FundsToAdd) + currentfunds;//converting FundsToAdd to Integer and adding them together in order to get the final balance;
                    userClass.setBalance(TotalBalance);
                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userClass);
                    Toast.makeText(AddFundsToBalanceActivity.this,"Success!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {/*return;*/}
            });
            finish();
        }
        else
        {
            Toast.makeText(AddFundsToBalanceActivity.this,"Can't add amounts above 500 or under 0!",Toast.LENGTH_SHORT).show();
        }
    }
}
