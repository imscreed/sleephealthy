package example.com.sleephealthy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class homeScreen extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "example.com.sleephealthy.MODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Button startDriving = (Button) findViewById(R.id.startDriving);
        Button startSleeping = (Button) findViewById(R.id.startSleeping);

        startDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeScreen.this, MainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "DRIVE");
                startActivity(intent);

            }
        });

        startSleeping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeScreen.this, MainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "SLEEP");
                startActivity(intent);
            }
        });

    }
}
