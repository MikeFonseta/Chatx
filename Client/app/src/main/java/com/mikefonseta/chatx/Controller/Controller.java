package com.mikefonseta.chatx.Controller;

import android.app.Activity;

import com.mikefonseta.chatx.Activity.AuthenticationActivity;

public class Controller {

    private static Activity currentActivity;

    public static void evaluate_action(String message) {
        if (currentActivity != null) {
            if (currentActivity instanceof AuthenticationActivity)
                AuthenticationController.evaluate_action(currentActivity, message);
            else
                ChatController.evaluate_action(currentActivity, message);
        }
    }

    public static void setCurrentActivity(Activity currentActivity) {
        Controller.currentActivity = currentActivity;
    }

}
