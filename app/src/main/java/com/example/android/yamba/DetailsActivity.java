package com.example.android.yamba;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Check if this activity was created before
		if (savedInstanceState == null) {
			// Create a fragment
            long statusId = getIntent().getLongExtra(StatusContract.Column.ID, -1);
			DetailsFragment fragment = DetailsFragment.newInstance(statusId);
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment,
							fragment.getClass().getSimpleName()).commit();
		}
	}
}
