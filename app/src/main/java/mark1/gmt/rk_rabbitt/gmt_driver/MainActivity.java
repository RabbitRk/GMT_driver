package mark1.gmt.rk_rabbitt.gmt_driver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void agreeJob(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);
    }

    public void gotoMap(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
