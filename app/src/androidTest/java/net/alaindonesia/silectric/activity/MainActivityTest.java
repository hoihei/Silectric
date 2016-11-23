package net.alaindonesia.silectric.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import net.alaindonesia.silectric.MainActivity;
import net.alaindonesia.silectric.R;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void before() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity();
    }

    @Test
    public void testEditDays() {
        final int[] prevDays = new int[1];
        onView(withId(R.id.daysEditText)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                EditText editText = (EditText) view;
                prevDays[0] = Integer.valueOf(String.valueOf(editText.getText()));
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        }));

        onView(withId(R.id.daysEditText)).check(matches(isClickable()));
        onView(withId(R.id.daysEditText)).perform(click());

        onView(withText("Number of Days: ")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(NumberPicker.class.getName()))).perform(swipeDown());
        onView(withText("Ok")).perform(click());
        onView(withId(R.id.daysEditText)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                EditText editText = (EditText) view;
                int newDays = Integer.valueOf(String.valueOf(editText.getText()));
                if (prevDays[0] == 1 || prevDays[0] == 2) return true;
                newDays += 2;
                return newDays == prevDays[0];
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void testNewActivity() {
        onView(withId(R.id.addUsageButton)).check(matches(isClickable()));
        onView(withId(R.id.usageListView)).check(matches(isCompletelyDisplayed()));
        int prevElements = getListViewElements(R.id.usageListView);
        onView(withId(R.id.addUsageButton)).perform(click());
        onView(withId(R.id.saveUsageButton)).perform(click());
        assertEquals(prevElements + 1, getListViewElements(R.id.usageListView));
    }

    @Test
    public void testDeleteActivity() {
        int prevElements = getListViewElements(R.id.usageListView);
        onView(withId(R.id.usageListView)).perform(click());
        onView(withId(R.id.deleteUsageButton)).perform(click());
        assertEquals(prevElements - 1, getListViewElements(R.id.usageListView));
    }


    private int getListViewElements(int id) {
        final int[] counts = new int[1];
        onView(withId(id)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                counts[0] = listView.getCount();
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        return counts[0];
    }


}
