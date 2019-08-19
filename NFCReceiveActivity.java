package vanunu.deeznuts;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

public class NFCReceiveActivity extends Activity {
private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreceive);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            ID=new String(message.getRecords()[0].getPayload());
        }
    }
    public void onClickBB(View v)
    {
        Intent intent = new Intent(NFCReceiveActivity.this, ProductActivity.class);
        intent.putExtra("intention","Search");
        intent.putExtra("id", ID);
        startActivity(intent);
        finish();
    }
}
