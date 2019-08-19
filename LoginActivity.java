package vanunu.deeznuts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email,pass;
    private CustomProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();
        progress= new CustomProgressDialog(LoginActivity.this,2);
        email=(EditText) findViewById(R.id.username);
        pass=(EditText) findViewById(R.id.password);

    }



    public void onClickLogin(View v) {
        String mail=email.getText().toString().trim();
        String password=pass.getText().toString().trim();

        if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter email & password!!", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.show();

        firebaseAuth.signInWithEmailAndPassword(mail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progress.cancel();
                            Toast.makeText(LoginActivity.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            progress.cancel();
                            Toast.makeText(LoginActivity.this,"Login Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void onClickSignup(View v) {
        Toast.makeText(getApplicationContext(),"signup",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }
    public void onClickRecover(View v) {
        String mail = email.getText().toString().trim();
        if (!mail.isEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Recovery email sent! Please check your email to recover your password!", Toast.LENGTH_LONG).show();
                            } else
                            {
                                Toast.makeText(getApplicationContext(), "There was an error recovering this account.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else
        {
            Toast.makeText(getApplicationContext(), "To recover your password please enter your email in the field above.", Toast.LENGTH_LONG).show();
        }
    }
}
