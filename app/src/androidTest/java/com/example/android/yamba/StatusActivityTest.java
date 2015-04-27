package com.example.android.yamba;


import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.CoreMatchers.*;

public class StatusActivityTest extends ActivityInstrumentationTestCase2<StatusActivity> {

    public StatusActivityTest() {
        super(StatusActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //Initializes the instance under test
        getActivity();
    }

    //Validate the character counter returns the correct value
    public void testCharacterCounter() {
        int maxChars = 140;
        String testString = "Test status update";

        //Check for proper initial conditions
        onView(withId(R.id.status_text_count))
                .check(matches(withText(String.valueOf(maxChars))));

        //Enter test string
        onView(withId(R.id.status_text))
                .perform(typeText(testString));

        int count = maxChars - testString.length();
        //Validate the change
        onView(withId(R.id.status_text_count))
                .check(matches(withText(String.valueOf(count))));
    }

    //Validate the character counter handles zero conditions correctly
    public void testZeroCondition() {
        String testString = "This status update is exactly 140 characters."
                + " Are you impressed that I was able to make this match"
                + " without extra chars? I worked really hard";

        //Enter test string
        onView(withId(R.id.status_text))
                .perform(typeText(testString));

        //Validate the change
        onView(withId(R.id.status_text_count))
                .check(matches(withText(String.valueOf(0))));

        //User should be able to send this status
        onView(withId(R.id.status_button))
                .check(matches(isEnabled()));
    }

    //Validate the counter disables status overflows
    public void testCounterOverflow() {
        //Type a really long test string, or use a lorem ipsum generator…
        String testString = "Bacon ipsum dolor amet tongue meatball bresaola,"
                + " corned beef chicken short ribs ham ham hock cupim shankle"
                + " pork short loin tenderloin chuck. Salami leberkas cow ribeye pancetta.";

        //Enter test string
        onView(withId(R.id.status_text))
                .perform(typeText(testString));

        //User should NOT be able to send this status
        onView(withId(R.id.status_button))
                .check(matches(not(isEnabled())));

        //Clear entry box
        onView(withId(R.id.status_text))
                .perform(clearText());

        //Button should be active again
        onView(withId(R.id.status_button))
                .check(matches(isEnabled()));
    }
}
