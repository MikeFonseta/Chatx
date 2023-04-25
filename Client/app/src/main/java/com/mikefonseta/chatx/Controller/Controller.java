package com.mikefonseta.chatx.Controller;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Activity.AuthenticationActivity;
import com.mikefonseta.chatx.Activity.ChatActivity;
import com.mikefonseta.chatx.Activity.MainActivity;

public class Controller {

    private static Activity currentActivity;
    private static Fragment currentFragment;

    public static void evaluate_action(String message) {
        if (currentActivity != null) {
            if (currentActivity instanceof AuthenticationActivity) {
                AuthenticationController.evaluate_action(currentFragment, message);
            } else if (currentActivity instanceof MainActivity) {
                ChatController.evaluate_action(currentActivity, currentFragment, message);
            } else if (currentActivity instanceof ChatActivity) {
                ChatController.evaluate_action(currentActivity, currentFragment, message);
            }
        }
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        Controller.currentActivity = currentActivity;
    }

    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(Fragment currentFragment) {
        Controller.currentFragment = currentFragment;
    }
}
