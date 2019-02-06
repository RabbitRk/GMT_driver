package mark1.gmt.rk_rabbitt.gmt_driver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.dbHelper;
import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.recycleAdapter;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    Switch login;
    RecyclerView job_alert_recycler;
    dbHelper database;
    job_alert_adapter recycler;
    List<recycleAdapter> productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.switch1);
        job_alert_recycler = findViewById(R.id.jobsRecycler);
        login.setOnCheckedChangeListener(this);
        productAdapter = new ArrayList<>();
        //code begins
        database = new dbHelper(this);

        productAdapter = database.getdata();

        recycler = new job_alert_adapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        job_alert_recycler.setLayoutManager(reLayoutManager);
        job_alert_recycler.setItemAnimator(new DefaultItemAnimator());

        job_alert_recycler.setAdapter(recycler);
    }

    public void agreeJob(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);
    }

    public void gotoMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
    }
}