package com.mikefonseta.chatx.Controller;

import android.app.Activity;

import com.mikefonseta.chatx.Activity.AuthenticationActivity;
import com.mikefonseta.chatx.Activity.ChatActivity;
import com.mikefonseta.chatx.Activity.MainActivity;

public class Controller {

    private static Activity currentActivity;

    public static void evaluate_action(String message) {
        if (currentActivity != null) {
            if (currentActivity instanceof AuthenticationActivity) {
                AuthenticationController.evaluate_action(currentActivity, message);
            } else if (currentActivity instanceof MainActivity) {
                ChatController.evaluate_action(currentActivity, message);
            } else if (currentActivity instanceof ChatActivity) {
                ChatController.evaluate_action(currentActivity, message);
            }
        }
    }

    public static void setCurrentActivity(Activity currentActivity) {
        Controller.currentActivity = currentActivity;
    }

}
