package com.fyp.inab.object;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.fyp.inab.view.about.AboutActivity;
import com.fyp.inab.view.student.StudentHomeActivity;
import com.fyp.inab.view.student.StudentLibraryActivity;
import com.fyp.inab.view.teacher.TeacherHomeActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
    This is a global class

    the main reason is to simplify the code
    and to avoid redundant same method IPO (input - process - output)
 */
public class Util {

    /*
    code:
    0 == Teacher
    1 == Student
     */

    // go back homepage
    public static void backHome (LifecycleOwner owner, FragmentActivity activity, int code) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToActivity(activity,
                        (code > 1) ? AboutActivity.class :
                        ((code == 0) ? TeacherHomeActivity.class : StudentHomeActivity.class));
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(owner, callback);
    }

    // go back homepage
    public static void backHomeActivity (LifecycleOwner owner, Activity activity, int code) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToActivity(activity,
                        (code > 1) ? AboutActivity.class :
                                ((code == 0) ? TeacherHomeActivity.class : StudentHomeActivity.class));
            }
        };
    }

    // go to any menu options *for activity
    public static void goToActivity (Activity from, Class<?> to) {
        from.startActivity(new Intent(from, to));
        from.finish();
        from.overridePendingTransition(androidx.transition.R.anim.abc_grow_fade_in_from_bottom, androidx.transition.R.anim.abc_shrink_fade_out_from_bottom);
    }

    // get current time in format
    @SuppressLint("SimpleDateFormat")
    public static String getDateCurrent() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return format.format(date);
    }

    // convert id to date format
    @SuppressLint("SimpleDateFormat")
    public static String convertIdToDate (String id) {
        long result = Long.parseLong(id);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(result);

        return format.format(date);
    }

    // get date for 14 days from now
    @SuppressLint("SimpleDateFormat")
    public static String get14DaysReturnDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 14);
        Date newDate = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(newDate);
    }

    // device android version
    public static File getVersion(Fragment fragment) {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                : fragment.getContext().getExternalFilesDir(null);
    }


}
