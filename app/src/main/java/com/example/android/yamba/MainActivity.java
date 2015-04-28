package com.example.android.yamba;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        TimelineFragment.OnTimelineItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //Global notifier of when timeline is in the foreground
    private static boolean inTimeline = false;
    public static boolean isInTimeline() {
        return inTimeline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inTimeline = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        inTimeline = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yamba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_purge:
                int rows = getContentResolver()
                        .delete(StatusContract.CONTENT_URI, null, null);
                Toast.makeText(this, "Deleted " + rows + " rows",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_post:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Handle list item selections from the timeline
    @Override
    public void onTimelineItemSelected(long id) {
        // Get the details container
        View fragmentContainer = findViewById(R.id.fragment_details);

        // Is details container visible?
        if (fragmentContainer != null) {
            //Place a new instance on the stack
            DetailsFragment fragment = DetailsFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right)
                    .replace(R.id.fragment_details, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(StatusContract.Column.ID, id);

            startActivity(intent);
        }
    }
}
