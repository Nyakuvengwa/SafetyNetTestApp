package za.co.sample.safetynettestapp;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        TextView sampleOutput = findViewById(R.id.sample_output);
        sampleOutput.setText(R.string.intro_message);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SafetyNetSampleFragment fragment = new SafetyNetSampleFragment();
            transaction.add(fragment, "FRAGTAG");
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
