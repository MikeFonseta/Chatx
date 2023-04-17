package com.mikefonseta.chatx.Controller;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mikefonseta.chatx.Activity.AuthenticationActivity;
import com.mikefonseta.chatx.Activity.ChatActivity;
import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Fragment.HomeFragment;
import com.mikefonseta.chatx.Fragment.LoginFragment;
import com.mikefonseta.chatx.Fragment.NewRoomFragment;
import com.mikefonseta.chatx.Fragment.ProfileFragment;
import com.mikefonseta.chatx.Fragment.RegisterFragment;
import com.mikefonseta.chatx.Fragment.UtentiInAttesaFragment;


public class Controller {

    private static Activity currentActivity;
    private static Fragment currentFragment;

    public static void evaluate_action(String message) {
        if(currentActivity != null)
        {
            if(currentActivity instanceof AuthenticationActivity)
            {
                if(currentFragment instanceof LoginFragment || currentFragment instanceof RegisterFragment)
                {
                    AuthenticationController.evaluate_action(currentFragment,message);
                }
            }
            else if(currentActivity instanceof MainActivity)
            {
                if(currentFragment instanceof HomeFragment)
                {
                    ChatController.evaluate_action(currentActivity,currentFragment,message);
                }
                else if(currentFragment instanceof NewRoomFragment)
                {

                }
                else if(currentFragment instanceof ProfileFragment)
                {

                }
                else if(currentFragment instanceof UtentiInAttesaFragment)
                {

                }
            }
            else if(currentActivity instanceof ChatActivity)
            {
                ChatController.evaluate_action(currentActivity,currentFragment,message);
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
