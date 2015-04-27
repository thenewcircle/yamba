package com.example.android.yamba;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.CoreMatchers.*;

public class TimelineActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity> {

    public TimelineActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //Initializes the instance under test
        getActivity();
    }

    public void testList() {
        //Find the last list item position
        ListView timeline =
                (ListView) getActivity().findViewById(android.R.id.list);
        final int testPosition = timeline.getAdapter().getCount() - 1;
        Cursor cursor = (Cursor) timeline.getAdapter().getItem(testPosition);

        //Get the data found in that list item
        String itemUser = cursor
                .getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String itemMessage = cursor
                .getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));

        //Scroll the list and click on that item
        onData(anything())
                .inAdapterView(withId(android.R.id.list))
                .atPosition(testPosition)
                .perform(click());


        /*
         * We don't have to do anything to handle the activity transition.
         * Espresso does that for use and starts passing new events once the
         * activity is resumed and laid out.
         */

        //Validate the the same data is shown in the new activity
        onView(withId(R.id.text_user))
                .check(matches(withText(itemUser)));

        onView(withId(R.id.text_message))
                .check(matches(withText(itemMessage)));
    }
}
