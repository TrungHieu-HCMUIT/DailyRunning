package com.example.dailyrunning;

import com.example.dailyrunning.authentication.LoginActivityTest;
import com.example.dailyrunning.record.DistanceFunctionTest;
import com.example.dailyrunning.record.MapsActivityTest;
import com.example.dailyrunning.user.RegisterActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                LoginActivityTest.class,
                RegisterActivityTest.class,
                DistanceFunctionTest.class,
                MapsActivityTest.class
        })
public class AppTestSuite {
}
