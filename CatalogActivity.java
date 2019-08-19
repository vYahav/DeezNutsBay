package vanunu.deeznuts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CatalogActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(CatalogActivity.this, MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_catalog:
                    Toast.makeText(getApplicationContext(),"Already on this page!",Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_myaccount:
                    Intent intent1 = new Intent(CatalogActivity.this, ProfileActivity.class);
                    intent1.putExtra("count", (String) getIntent().getExtras().get("count"));
                    startActivity(intent1);
                    finish();
                    return true;
                case R.id.navigation_myproducts:
                    if(Integer.valueOf( (String) getIntent().getExtras().get("count"))>0) {
                        Intent intent = new Intent(CatalogActivity.this, ProductsListActivity.class);
                        intent.putExtra("count",  (String) getIntent().getExtras().get("count"));
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
                    Intent intent3 = new Intent(CatalogActivity.this, AddProductActivity.class);
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
        navigation.getMenu().getItem(1).setChecked(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        PopulateListView();
        registerClicks();

    }

    private void PopulateListView() {
        //Create list of items
        String[] items= getResources().getStringArray(R.array.CatalogListArray);
        //Build adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.listlayout,items);
        //Configure the list view
        ListView list= (ListView) findViewById(R.id.listview);
        list.setAdapter(adapter);
    }

    private void registerClicks() {
        ListView list= (ListView) findViewById(R.id.listview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewclicked, int position, long id) {
                TextView tv= (TextView) viewclicked;
                Intent intent=new Intent(CatalogActivity.this, ProductActivity.class);
                intent.putExtra("Category",tv.getText().toString());
                intent.putExtra("intention","Category");
                startActivity(intent);
            }
        });
    }
    public void openSearch(View v) {
        Intent intent = new Intent(CatalogActivity.this, SearchProductActivity.class);
        startActivity(intent);
    }
}
