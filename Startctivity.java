package asatkeeva.finalfotoswish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Startctivity extends AppCompatActivity {
Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startctivity);
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            //on click takes you to sorting activity
            public void onClick(View view) {
                Intent intent = new Intent (Startctivity.this, AccountActivity.class);
                startActivity(intent);

            }
        });
    }
}
