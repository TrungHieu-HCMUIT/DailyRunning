package com.example.dailyrunning.record;

import static android.location.LocationManager.GPS_PROVIDER;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.example.dailyrunning.R;
import com.example.dailyrunning.model.LatLng;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.Description;

import java.util.Arrays;
import java.util.List;

public class MapsActivityTest {

/*    void startMocking(double x, double y, Activity activity) {
        LocationManager lm = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = new Location(GPS_PROVIDER);
        location.setLatitude(x);
        location.setLongitude(y);
        location.setAccuracy(2);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(1);

        try {
            // @throws IllegalArgumentException if a provider with the given name already exists
            lm.addTestProvider(GPS_PROVIDER, false, false, false, false, false, true, true, 1, 2);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            // @throws IllegalArgumentException if no provider with the given name exists
            lm.setTestProviderEnabled(GPS_PROVIDER, true);
        } catch (IllegalArgumentException ignored) {
            lm.addTestProvider(GPS_PROVIDER, false, false, false, false, false, true, true, 1, 2);
        }

        try {
            // @throws IllegalArgumentException if no provider with the given name exists
            lm.setTestProviderLocation(GPS_PROVIDER, location);
        } catch (IllegalArgumentException ignored) {
            lm.addTestProvider(GPS_PROVIDER, false, false, false, false, false, true, true, 1, 2);
            lm.setTestProviderEnabled(GPS_PROVIDER, true);
            lm.setTestProviderLocation(GPS_PROVIDER, location);
        }
    }*/

    void startMocking(double lat,double lng,Activity activity){
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy( Criteria.ACCURACY_FINE );

        String mocLocationProvider = LocationManager.GPS_PROVIDER;//lm.getBestProvider( criteria, true );

        lm.addTestProvider(mocLocationProvider, false, false,
                false, false, true, true, true, 1, 2);
        lm.setTestProviderEnabled(mocLocationProvider, true);

        Location loc = new Location(mocLocationProvider);
        Location mockLocation = new Location(mocLocationProvider); // a string
        mockLocation.setLatitude(lat);  // double
        mockLocation.setLongitude(lng);
        mockLocation.setAltitude(loc.getAltitude());
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        lm.setTestProviderLocation( mocLocationProvider, mockLocation);
    }
    void startMockTest(List<LatLng> latLngs,ActivityScenario scenario){
        String oldDistance="0.00 km";
        for(int i=0 ;i<latLngs.size();i++){
            int finalI = i;
            scenario.onActivity(activity -> {
                startMocking(latLngs.get(finalI).getLatitude(), latLngs.get(finalI).getLongitude(), activity);
            });
            SystemClock.sleep(3000);
            onView(withId(R.id.data_distance)).check(matches(not(withText(oldDistance))));
            oldDistance=getText(withId(R.id.data_distance));
        }

    }
    @Test
    public void test_onLocationChanged() {
        ActivityScenario scenario = ActivityScenario.launch(MapsActivity.class);
        SystemClock.sleep(20000);

        onView(withId(R.id.startButton)).perform(click());
        SystemClock.sleep(3000);
        List<LatLng> latLngs= Arrays.asList(
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
        startMockTest(latLngs,scenario);
        SystemClock.sleep(10000);


    }
    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
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
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }
}

