package com.example.dailyrunning.user;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.dailyrunning.authentication.LoginActivityTest.clickXY;
import static com.example.dailyrunning.authentication.LoginActivityTest.test_Logout;

import static org.hamcrest.Matchers.not;

import android.os.SystemClock;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;


import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginActivityTest;
import com.example.dailyrunning.home.HomeActivity;

import org.hamcrest.Matchers;
import org.junit.Test;

public class RegisterActivityTest {

    final String password1= "Tes1";
    final String password2= "Test1";
    final String password3= "Te1";
    final String password4= "TEst1";
    final String password5= "tes1";
    final String password6= "Tet1";
    final String password7= "T111";
    final String password8= "Te12";
    final String password9= "Test";
    final String password10= "Te1*";

    final String correctEmail = "sv123@gmail.com";
    final String incorrectEmail = "sv123.com";
    final String correctPassword = "Password1";
    final String incorrectPassword = "pass1";
    final String correctName= "Test";


    //region isValidatePasswordTest

    @Test
    public void test_password1() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password1));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
    @Test
    public void test_password2() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password2));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
    @Test
    public void test_password3() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password3));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
    }
    @Test
    public void test_password4() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password4));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
    @Test
    public void test_password5() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password5));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
    }
    @Test
    public void test_password6() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password6));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
    @Test
    public void test_password7() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password7));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
    }
    @Test
    public void test_password8() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password8));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
    @Test
    public void test_password9() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password9));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
    }
    @Test
    public void test_password10() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(password10));
        onView(withText("Mật khẩu không hợp lệ")).check(doesNotExist());
    }
//endregion

    //region textChangeCheck


    @Test
    public void test_nullEmailAndPwdAndRePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withText("Email không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));
        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_normalCase() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText("test134@gmail.com"));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerName)).perform(replaceText("Testing123"));
        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void test_wrongAll() {
        LoginActivityTest.test_Logout();

        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withText("Email không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));
    }

    //endregion

    //region validateData
    @Test
    public void test_nullEmailPwdRePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullNamePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullNamePwdWrongEmailRePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullRePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test// ko hien toast
    public void test_wrongEmail() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withText("Email không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));


    }

    @Test
    public void test_nullNameEmailWrongRePwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullNameRePWdWrongEmailPwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_nullNameEmailWrongPwd() {
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_wrongPwdRePwd() {// ko hien toast
        LoginActivityTest.test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));



    }

    //endregion

    //region onregisterclick


    @Test
    public void test_registerNormalCase() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText("test134@gmail.com"));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerName)).perform(replaceText("Testing123"));
        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullEmailPwdRePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullNamePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullNamePwdWrongEmailRePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullRePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test// ko hien toast
    public void test_registerWrongEmail() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withText("Email không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));


    }

    @Test
    public void test_registerNullNameEmailWrongRePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullNameRePWdWrongEmailPwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(incorrectEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(""));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerNullNameEmailWrongPwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(""));
        onView(withId(R.id.registerEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));

        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(800);
        onView(withText("Vui lòng điền đầy đủ thông tin!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void test_registerWrongPwdRePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));
        onView(withId(R.id.registerPassword)).perform(replaceText(incorrectPassword));
        onView(withText("Mật khẩu không hợp lệ")).check(matches(isDisplayed()));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(incorrectPassword));

    }

    //endregion

    //region clearTextBox
    @Test
    public void test_clearTextBox() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerName)).perform(replaceText(correctName));
        onView(withId(R.id.registerEmail)).perform(replaceText(correctEmail));

        onView(withId(R.id.registerName)).perform(clearText());
        onView(withId(R.id.registerEmail)).perform(clearText());

        onView(withId(R.id.registerName)).check(matches(withText("")));
        onView(withId(R.id.registerEmail)).check(matches(withText("")));

    }

    //endregion

    //region togglePasswordVisible
    @Test
    public void test_togglePwd() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());

        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));

        onView(withId(R.id.registerPassword))
                .check(matches(not(ToastMatcher.isPasswordVisibilityToggleEnabled())));
        onView(withId(R.id.registerRetypePassword))
                .check(matches(not(ToastMatcher.isPasswordVisibilityToggleEnabled())));
    }

    //endregion

    //region pickDateOfBirth
    @Test
    public void test_pickDateOfBirth() {
        test_Logout();

        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.email_editText)).perform(typeText(correctEmail));
        onView(withId(R.id.password_editText)).perform(replaceText(correctPassword));
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.loading_dialog)).check(matches(isDisplayed()));
        SystemClock.sleep(3000);
        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.userFragment)).perform(click());
        onView(withId(R.id.setting_imageButton)).perform(click());
        onView(withId(R.id.updateInfo)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        SystemClock.sleep(300);
        onView(withText("OK")).perform(click());

     /*   onView(withId(R.id.updateInfo)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).check(matches(ToastMatcher.matchesDate(2017, 6, 30)));*/
        onView(withText("30/06/2017")).check(matches(isDisplayed()));


    }

    //endregion

    //region onConfirmClick
    @Test
    public void test_onConfirmClickNormalCase() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText("test3@gmail.com"));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerName)).perform(replaceText("Testing123"));
        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.registerAddInfo)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 6, 30));
        SystemClock.sleep(300);
        onView(withText("OK")).perform(click());
        onView(withText("Xác nhận")).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void test_onConfirmClickAbnormalCase() {
        test_Logout();
        ActivityScenario scenario = ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.registerClickable_textView)).perform(click());
        onView(withId(R.id.registerEmail)).perform(replaceText("test5@gmail.com"));
        onView(withId(R.id.registerPassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerRetypePassword)).perform(replaceText(correctPassword));
        onView(withId(R.id.registerName)).perform(replaceText("Testing123"));
        onView(withId(R.id.register_button)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));

        onView(withText("Xác nhận")).perform(click());
        onView(withText("Vui lòng nhập đầy đủ thông tin")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

    }
    //endregion




}


