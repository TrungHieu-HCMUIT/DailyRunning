package com.example.dailyrunning.authentication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;

import com.example.dailyrunning.R;
import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.user.UserFragment;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivityTest {
    final String correctEmail = "sv1234@gmail.com";
    final String incorrectEmail = "fdsfsd@gmail.com";
    final String correctPassword = "Password1";
    final String incorrectPassword = "Thisisawrongpassword1";

    public static void setUpBeforeClass() {
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);

        try{
            onView(isRoot()).perform(waitId(R.id.home_fragment, TimeUnit.SECONDS.toMillis(3)));

            onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
            onView(withId(R.id.userFragment)).perform(click());
            onView(isRoot()).perform(waitId(R.id.home_fragment, TimeUnit.SECONDS.toMillis(3)));
            onView(withId(R.id.log_out_button)).perform(click());
            scenario.close();

        }catch (AssertionFailedError | Exception e) {

        }
    }

    @Test
    public  void test_Logout() {
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);

        try{
            SystemClock.sleep(3000);
            onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
            onView(withId(R.id.userFragment)).perform(click());
            onView(withId(R.id.log_out_button)).perform(scrollTo());

            onView(withId(R.id.log_out_button)).perform(clickXY(20,20));
            scenario.close();
            SystemClock.sleep(3000);
        }
        catch (Exception e){
            scenario.close();
            SystemClock.sleep(3000);
        }

//        onView(withId(R.id.userFragment)).perform(click());
//        onView(isRoot()).perform(waitId(R.id.home_fragment, TimeUnit.SECONDS.toMillis(15)));

//        try{
//            onView(isRoot()).perform(waitId(R.id.home_fragment, TimeUnit.SECONDS.toMillis(3)));
//
//            onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
//            onView(withId(R.id.userFragment)).perform(click());
//            onView(isRoot()).perform(waitId(R.id.home_fragment, TimeUnit.SECONDS.toMillis(3)));
//            onView(withId(R.id.log_out_button)).perform(click());
//            scenario.close();
//
//        }catch (AssertionFailedError | Exception e) {
//
//        }
    }

    @Test
    public void test_emptyEmail() {
        ActivityScenario scenario = ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.password_editText)).perform(replaceText(correctPassword));
        onView(withId(R.id.login_button)).perform(click());

        onView(withText("Vui lòng nhập email và mật khẩu")).check(matches(isDisplayed()));
    }

    @Test
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
    public void test_wrongEmail() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.email_editText)).perform(typeText(incorrectEmail));
        onView(withId(R.id.password_editText)).perform(replaceText(correctPassword));
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(100);
        onView(withText("Không tồn tại người dùng này")).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullpassword() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.email_editText)).perform(typeText(correctEmail));
        onView(withId(R.id.password_editText)).perform(replaceText(""));
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(100);

    }

    @Test
    public void test_wrongpassword() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.email_editText)).perform(typeText(correctEmail));
        onView(withId(R.id.password_editText)).perform(replaceText(incorrectPassword));
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(500);
        onView(withText("Sai mật khẩu")).check(matches(isDisplayed()));
    }


    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}


