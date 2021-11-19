package com.example.dailyrunning.user;

import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Author: http://www.qaautomated.com/2016/01/how-to-test-toast-message-using-espresso.html
 */
public class ToastMatcher extends TypeSafeMatcher<Root> {

    @Override
    public boolean matchesSafely(Root item) {
        int type = item.getWindowLayoutParams().get().type;
        if(type== WindowManager.LayoutParams.TYPE_TOAST){
            IBinder windowToken = item.getDecorView().getWindowToken();
            IBinder appToken = item.getDecorView().getApplicationWindowToken();
            // means this window isn't contained by any other windows.
            return windowToken == appToken;
        }
        return false;
    }

    @Override
    public void describeTo(org.hamcrest.Description description) {
        description.appendText("is toast");
    }

    public static Matcher<View> isPasswordVisibilityToggleEnabled() {
        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {

            @Override public void describeTo(Description description) {
                description.appendText("is password visibility toggle enabled");
            }

            @Override protected boolean matchesSafely(TextInputLayout view) {
                return view.isPasswordVisibilityToggleEnabled();
            }
        };
    }
    public static Matcher<View> matchesDate(final int year, final int month, final int day) {
        return new BoundedMatcher<View, DatePicker>(DatePicker.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("matches date:");
            }

            @Override
            protected boolean matchesSafely(DatePicker item) {
                return (year == item.getYear() && month == item.getMonth() && day == item.getDayOfMonth());
            }
        };
    }

}