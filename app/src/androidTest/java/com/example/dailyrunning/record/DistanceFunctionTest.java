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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.dailyrunning.authentication.LoginActivityTest.clickXY;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
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
import com.google.android.gms.maps.GoogleMap;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DistanceFunctionTest {

    //region distance
    private final double latA1 = -91;
    private final double latA2 = -90;
    private final double latA3 = -89;
    private final double latA4 = 0;
    private final double latA5 = 89;
    private final double latA6 = 90;
    private final double latA7 = 91;

    private final double lngA1 = -181;
    private final double lngA2 = -180;
    private final double lngA3 = -179;
    private final double lngA4 = 0;
    private final double lngA5 = 179;
    private final double lngA6 = 180;
    private final double lngA7 = 181;

    private final double latB1 = -91;
    private final double latB2 = -90;
    private final double latB3 = -89;
    private final double latB4 = 0;
    private final double latB5 = 89;
    private final double latB6 = 90;
    private final double latB7 = 91;

    private final double lngB1 = -181;
    private final double lngB2 = -180;
    private final double lngB3 = -179;
    private final double lngB4 = 0;
    private final double lngB5 = 179;
    private final double lngB6 = 180;
    private final double lngB7 = 181;

    private final int result1=10005;
    private final int result2=9894;
    private final int result3=0;
    private final int result4=19899;
    private final int result5=20010;
    private final int resultError=-1;
    @Test
    public void test_distance1() {
        final int result= (int)(RecordViewModel.distance(latA1,lngA4,latB4,lngB4)/1000);
        Assert.assertEquals(result,resultError,0);
    }
    @Test
    public void test_distance2() {
        final int result=
                (int)( RecordViewModel.distance(latA2,lngA4,latB4,lngB4)/1000);
        Assert.assertEquals(result,result1,0);
    }
    @Test
    public void test_distance3() {
        final int result=
                (int)( RecordViewModel.distance(latA3,lngA4,latB4,lngB4)/1000);
        Assert.assertEquals(result,result2,0);
    }
    @Test
    public void test_distance4() {
        final int result=
                (int)( RecordViewModel.distance(latA4,lngA4,latB4,lngB4)/1000);
        Assert.assertEquals(result,result3,0);
    }
    @Test
    public void test_distance5() {
        final int result= (int)RecordViewModel.distance(latA5,lngA4,latB4,lngB4)/1000;
        Assert.assertEquals(result,result2,0);
    }
    @Test
    public void test_distance6() {
        final int result=
                (int)( RecordViewModel.distance(latA6,lngA4,latB4,lngB4)/1000);
        Assert.assertEquals(result,result1,0);
    }
    @Test
    public void test_distance7() {
        final int result= (int)RecordViewModel.distance(latA7,lngA1,latB4,lngB4)/1000;
        Assert.assertEquals(result,resultError,0);
    }
    @Test
    public void test_distance8() {
        final int result= (int)RecordViewModel.distance(latA4,lngA2,latB4,lngB4)/1000;
        Assert.assertEquals(result,result5,0);
    }
    @Test
    public void test_distance9() {
        final int result= (int)RecordViewModel.distance(latA4,lngA3,latB4,lngB4)/1000;
        Assert.assertEquals(result,result4,0);
    }
    @Test
    public void test_distance10() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB4)/1000;
        Assert.assertEquals(result,result3,0);
    }
    @Test
    public void test_distance11() {
        final int result= (int)RecordViewModel.distance(latA4,lngA5,latB4,lngB4)/1000;
        Assert.assertEquals(result,result4,0);
    }
    @Test
    public void test_distance12() {
        final int result= (int)RecordViewModel.distance(latA4,lngA6,latB4,lngB4)/1000;
        Assert.assertEquals(result,result5,0);
    }
    @Test
    public void test_distance13() {
        final int result= (int)RecordViewModel.distance(latA4,lngA7,latB1,lngB4)/1000;
        Assert.assertEquals(result,resultError,0);
    }
    @Test
    public void test_distance14() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB2,lngB4)/1000;
        Assert.assertEquals(result,result1,0);
    }
    @Test
    public void test_distance15() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB3,lngB4)/1000;
        Assert.assertEquals(result,result2,0);
    }
    @Test
    public void test_distance16() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB4)/1000;
        Assert.assertEquals(result,result3,0);
    }
    @Test
    public void test_distance17() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB5,lngB4)/1000;
        Assert.assertEquals(result,result2,0);
    }
    @Test
    public void test_distance18() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB6,lngB4)/1000;
        Assert.assertEquals(result,result1,0);
    }
    @Test
    public void test_distance19() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB7,lngB1)/1000;
        Assert.assertEquals(result,resultError,0);
    }
    @Test
    public void test_distance20() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB2)/1000;
        Assert.assertEquals(result,result5,0);
    }
    @Test
    public void test_distance21() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB3)/1000;
        Assert.assertEquals(result,result4,0);
    }
    @Test
    public void test_distance22() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB4)/1000;
        Assert.assertEquals(result,result3,0);
    }
    @Test
    public void test_distance23() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB5)/1000;
        Assert.assertEquals(result,result4,0);
    }
    @Test
    public void test_distance24() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB6)/1000;
        Assert.assertEquals(result,result5,0);
    }
    @Test
    public void test_distance25() {
        final int result= (int)RecordViewModel.distance(latA4,lngA4,latB4,lngB7)/1000;
        Assert.assertEquals(result,resultError,0);
    }

    //endregion



}

