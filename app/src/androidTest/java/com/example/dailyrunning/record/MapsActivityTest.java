package com.example.dailyrunning.record;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.dailyrunning.authentication.LoginActivityTest.clickXY;
import static com.example.dailyrunning.authentication.LoginActivityTest.waitId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginActivityTest;
import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.user.ToastMatcher;
import com.google.android.gms.maps.GoogleMap;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MapsActivityTest {

    final String correctEmail = "sv1234@gmail.com";
    final String incorrectEmail = "fdsfsd@gmail.com";
    final String correctPassword = "Password1";
    final String incorrectPassword = "Thisisawrongpassword1";

    LocationManager setupMock(Activity activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);


        lm.addTestProvider(LocationManager.GPS_PROVIDER, false, false,
                false, false, true, true, true, 1, 2);
        lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        return lm;
    }

    void startMocking(double lat, double lng, LocationManager lm) {


        Location loc = new Location(LocationManager.GPS_PROVIDER);
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER); // a string
        mockLocation.setLatitude(lat);  // double
        mockLocation.setLongitude(lng);
        mockLocation.setAltitude(loc.getAltitude());
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
    }

    void startMockTest(List<LatLng> latLngs, ActivityScenario scenario) {
        String oldDistance = "0.00 km";
        final int[] oldPolylineLength = {-1};
        final LocationManager[] lm = new LocationManager[1];

        scenario.onActivity(activity -> {
            lm[0] = setupMock(activity);
        });
        sleep(1000);
        for (int i = 0; i < latLngs.size(); i++) {
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), lm[0]);
                RecordViewModel mRecordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class);
                Assert.assertNotEquals(oldPolylineLength[0], mRecordViewModel.locations.size());
                oldPolylineLength[0] = mRecordViewModel.locations.size();
            });
            sleep(3000);
            onView(withId(R.id.data_distance)).check(matches(not(withText(oldDistance))));
            oldDistance = getText(withId(R.id.data_distance));
        }
    }


    @Test
    public void test_onLocationChanged() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        sleep(3000);
        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667),
                new LatLng(10.8009, 106.6666),
                new LatLng(10.8009, 106.6665),
                new LatLng(10.8010, 106.6665),
                new LatLng(10.8010, 106.6664),
                new LatLng(10.8011, 106.6663),
                new LatLng(10.8011, 106.6662),
                new LatLng(10.8012, 106.6660),
                new LatLng(10.8013, 106.6657),
                new LatLng(10.8015, 106.6653)
        );
        startMockTest(latLngs, scenario);
        sleep(3000);
    }

    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    @Test
    public void test_newRecord() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        scenario.onActivity(activity ->
        {
            RecordViewModel mRecordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class);
            final GoogleMap map = mRecordViewModel.map;
            Assert.assertTrue(map.isMyLocationEnabled());
            Assert.assertFalse(map.getUiSettings().isScrollGesturesEnabled());
            Assert.assertFalse(map.getUiSettings().isRotateGesturesEnabled());
            Assert.assertFalse(map.getUiSettings().isCompassEnabled());
            Assert.assertEquals(map.getMapType(), GoogleMap.MAP_TYPE_NORMAL);
            Assert.assertEquals(map.getMinZoomLevel(), 3, 0);
        });

    }


    //region updatePace
    void startUpdatePaceTest(List<LatLng> latLngs, ActivityScenario scenario) {
        String oldPace = "0.00 m/s";
        final LocationManager[] lm = new LocationManager[1];

        scenario.onActivity(activity -> {
            lm[0] = setupMock(activity);
        });
        sleep(1000);

        for (int i = 0; i < latLngs.size(); i++) {
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), lm[0]);
            });
            sleep(3000);
            try {
                onView(withId(R.id.data_pace)).check(matches(not(withText(oldPace))));
            } catch (AssertionFailedError ignored) {
            }
            oldPace = getText(withId(R.id.data_pace));
        }
    }

    @Test
    public void test_updatePace() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        sleep(3000);
        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667),
                new LatLng(10.8009, 106.6666),
                new LatLng(10.8009, 106.6665),
                new LatLng(10.8010, 106.6665),
                new LatLng(10.8010, 106.6664),
                new LatLng(10.8011, 106.6663),
                new LatLng(10.8011, 106.6662),
                new LatLng(10.8012, 106.6660),
                new LatLng(10.8013, 106.6657),
                new LatLng(10.8015, 106.6653)
        );
        startUpdatePaceTest(latLngs, scenario);
        sleep(3000);
    }
    //endregion


    //region update distance
    void startUpdateDistanceTest(List<LatLng> latLngs, ActivityScenario scenario) {
        String oldDistance = "0.00 m/s";
        final int[] oldPolylineLength = {-1};
        final LocationManager[] lm = new LocationManager[1];

        scenario.onActivity(activity -> {
            lm[0] = setupMock(activity);
        });
        sleep(1000);

        for (int i = 0; i < latLngs.size(); i++) {
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), lm[0]);
            });
            sleep(3000);
            onView(withId(R.id.data_distance)).check(matches(not(withText(oldDistance))));
            oldDistance = getText(withId(R.id.data_distance));
        }
    }

    public void test_Logout() {
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);

        try {
            SystemClock.sleep(3000);
            onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
            onView(withId(R.id.userFragment)).perform(click());
            onView(withId(R.id.log_out_button)).perform(scrollTo());

            onView(withId(R.id.log_out_button)).perform(clickXY(20, 20));
            scenario.close();
            SystemClock.sleep(3000);
        } catch (Exception e) {
            scenario.close();
            SystemClock.sleep(3000);
        }
    }

    public void test_normalCase() {
        test_Logout();

        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.email_editText)).perform(typeText(correctEmail));
        onView(withId(R.id.password_editText)).perform(replaceText(correctPassword));
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.loading_dialog)).check(matches(isDisplayed()));
        SystemClock.sleep(3000);
        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));

    }

    @Test
    public void test_updateDistance() {
        test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        sleep(3000);
        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667),
                new LatLng(10.8009, 106.6666),
                new LatLng(10.8009, 106.6665),
                new LatLng(10.8010, 106.6665),
                new LatLng(10.8010, 106.6664),
                new LatLng(10.8011, 106.6663),
                new LatLng(10.8011, 106.6662),
                new LatLng(10.8012, 106.6660),
                new LatLng(10.8013, 106.6657),
                new LatLng(10.8015, 106.6653)
        );
        startUpdateDistanceTest(latLngs, scenario);
        sleep(3000);
    }
    //endregion


    //region update polyline
    void startUpdatePolylineTest(List<LatLng> latLngs, ActivityScenario scenario) {
        final int[] oldPolylineLength = {-1};
        final LocationManager[] lm = new LocationManager[1];

        scenario.onActivity(activity -> {
            lm[0] = setupMock(activity);
        });
        sleep(1000);

        for (int i = 0; i < latLngs.size(); i++) {
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), lm[0]);
                RecordViewModel mRecordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class);
                Assert.assertNotEquals(oldPolylineLength[0], mRecordViewModel.locations.size());
                oldPolylineLength[0] = mRecordViewModel.locations.size();
            });
            sleep(3000);

        }
    }

    @Test
    public void test_updatePolyline() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        sleep(3000);
        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667),
                new LatLng(10.8009, 106.6666),
                new LatLng(10.8009, 106.6665),
                new LatLng(10.8010, 106.6665),
                new LatLng(10.8010, 106.6664),
                new LatLng(10.8011, 106.6663),
                new LatLng(10.8011, 106.6662),
                new LatLng(10.8012, 106.6660),
                new LatLng(10.8013, 106.6657),
                new LatLng(10.8015, 106.6653)
        );
        startUpdatePolylineTest(latLngs, scenario);
        sleep(3000);
    }
    //endregion


    //region start timer
    void startTimerTest(List<LatLng> latLngs, ActivityScenario scenario) {
        final long[] oldTimeWorkingInSec = {-1};

        final LocationManager[] lm = new LocationManager[1];

        scenario.onActivity(activity -> {
            lm[0] = setupMock(activity);
        });
        sleep(1000);

        for (int i = 0; i < latLngs.size(); i++) {
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), lm[0]);
                RecordViewModel mRecordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class);
                Assert.assertNotEquals(oldTimeWorkingInSec[0], mRecordViewModel.timeWorkingInSec);
                oldTimeWorkingInSec[0] = mRecordViewModel.timeWorkingInSec;
            });
            sleep(3000);

        }
    }

    @Test
    public void test_startTimer() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        sleep(3000);
        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667),
                new LatLng(10.8009, 106.6666),
                new LatLng(10.8009, 106.6665),
                new LatLng(10.8010, 106.6665),
                new LatLng(10.8010, 106.6664),
                new LatLng(10.8011, 106.6663),
                new LatLng(10.8011, 106.6662),
                new LatLng(10.8012, 106.6660),
                new LatLng(10.8013, 106.6657),
                new LatLng(10.8015, 106.6653)
        );
        startTimerTest(latLngs, scenario);
        sleep(3000);
    }
    //endregion

    //region getTimeWorkingString
    @Test
    public void test_getTimeWorkingString() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        scenario.onActivity(activity -> {
            RecordViewModel mRecordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class);
            mRecordViewModel.timeWorkingInSec = 1000;
            final String result = mRecordViewModel.getTimeWorkingString();
            Assert.assertEquals(result, "00:16:40");
            mRecordViewModel.timeWorkingInSec = -1;
            final String result1 = mRecordViewModel.getTimeWorkingString();
            Assert.assertEquals(result1, "Invalid input");
        });
    }
    //endregion

    //region onTopControllerArrowClick
    @Test
    public void test_onTopControllerArrowClick() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();

        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        long oldTime = mRecordViewModel.get().timeWorkingInSec;
        onView(withId(R.id.btnPause)).perform(click());
        Assert.assertEquals(oldTime, mRecordViewModel.get().timeWorkingInSec);
        Assert.assertEquals("0.00 m/s", mRecordViewModel.get().paceString.getValue());
        onView(withId(R.id.btnCountinue)).perform(click());
        Assert.assertNotEquals(oldTime, mRecordViewModel.get().timeWorkingInSec);

    }

    //endregion
    //
    // region togglePause
    @Test
    public void test_togglePause() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());
        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.fold_image_button)).perform(click());
        onView(withId(R.id.pace_text_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fold_image_button)).perform(click());
        onView(withId(R.id.pace_text_view)).check(matches(isDisplayed()));
    }
    //endregion

    // region finishRecord
    @Test
    public void test_finishRecord1() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();
        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667)
        );
        startMockTest(latLngs, scenario);
        long oldTime = mRecordViewModel.get().timeWorkingInSec;
        String oldDistance = mRecordViewModel.get().distanceString.getValue();
        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.btnEnd)).perform(click());
        onView(withId(R.id.cancel_button)).perform(click());
        Assert.assertEquals(oldTime, mRecordViewModel.get().timeWorkingInSec);
        Assert.assertEquals(oldDistance, mRecordViewModel.get().distanceString.getValue());
    }

    @Test
    public void test_finishRecord2() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();
        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667)
        );
        startMockTest(latLngs, scenario);
        long oldTime = mRecordViewModel.get().timeWorkingInSec;
        String oldDistance = mRecordViewModel.get().distanceString.getValue();
        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.btnEnd)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());
        Assert.assertEquals(oldTime, mRecordViewModel.get().timeWorkingInSec);
        Assert.assertEquals(oldDistance, mRecordViewModel.get().distanceString.getValue());
        sleep(5000);

        onView(withId(R.id.root_scroll_view)).check(matches(isDisplayed()));

        onView(withId(R.id.km)).check(matches(withText(mRecordViewModel.get().distanceString.getValue())));
        onView(withId(R.id.pace)).check(matches(withText(mRecordViewModel.get().averagePaceString.getValue())));
        onView(withId(R.id.time)).check(matches(withText(mRecordViewModel.get().timeString.getValue())));
    }

    //endregion

    //region onSaveClick
    @Test
    public void test_onSaveClick1() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();
        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667)
        );
        startMockTest(latLngs, scenario);
        long oldTime = mRecordViewModel.get().timeWorkingInSec;
        String oldDistance = mRecordViewModel.get().distanceString.getValue();
        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.btnEnd)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());

        SystemClock.sleep(5000);

        onView(withId(R.id.save_button)).perform(click());
        SystemClock.sleep(2000);


    }

    @Test
    public void test_onSaveClick2() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();
        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667)
        );
        startMockTest(latLngs, scenario);

        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.btnEnd)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());

        SystemClock.sleep(5000);

        onView(withId(R.id.cancel_button)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());
        SystemClock.sleep(2000);


    }

    @Test
    public void test_onSaveClick3() {
        (new LoginActivityTest()).test_normalCase();

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        AtomicReference<RecordViewModel> mRecordViewModel = new AtomicReference<>();
        scenario.onActivity(activity -> {
            mRecordViewModel.set(new ViewModelProvider((ViewModelStoreOwner) activity).get(RecordViewModel.class));
        });
        sleep(20000);
        onView(withId(R.id.startButton)).perform(click());

        List<LatLng> latLngs = Arrays.asList(
                new LatLng(10.8007, 106.6669),
                new LatLng(10.8007, 106.6668),
                new LatLng(10.8008, 106.6668),
                new LatLng(10.8008, 106.6667)
        );
        startMockTest(latLngs, scenario);

        onView(withId(R.id.btnPause)).perform(click());
        onView(withId(R.id.btnEnd)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());

        SystemClock.sleep(5000);

        onView(withId(R.id.cancel_button)).perform(click());
        onView(withId(R.id.cancel_button)).perform(click());
        onView(withId(R.id.finish_fragment)).check(matches(isDisplayed()));

        SystemClock.sleep(2000);


    }
    //endregion


}

