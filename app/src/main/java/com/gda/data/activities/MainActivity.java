package com.gda.data.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gda.data.R;
import com.gda.data.entities.Course;
import com.gda.data.fragments.BaseFragment;
import com.gda.data.fragments.MainFragment;
import com.gda.data.utilities.AppStateUtils;
import com.gda.data.utilities.ToolbarUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ToolbarUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;

    private SharedPreferences sharedPreferences;

    private Gson gson;

    private ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.textViewToolbarTitle);
        showHideToolbarListener = new ToolbarUtils.ShowHideToolbarOnScrollingListener(toolbar);

        sharedPreferences = getSharedPreferences(getApplicationInfo().name, Context.MODE_PRIVATE);

        gson = new GsonBuilder().create();

        courses = loadData();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("courses", courses);

        if (savedInstanceState == null) {
            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, mainFragment, "MainFragment")
                    .commit();
        } else {
            getFragmentManager().findFragmentByTag("MainFragment").getArguments().putParcelableArrayList("courses", courses);
        }
    }

    private ArrayList<Course> loadData() {
        String coursesData = sharedPreferences.getString(getString(R.string.key_courses), null);

        if (coursesData == null) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(coursesData, new TypeToken<ArrayList<Course>>() {
            }.getType());
        }
    }

    @Override
    public void onBackPressed() {
        if (AppStateUtils.isItemDeletionState) {
            ((BaseFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer)).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeData(courses);
    }

    private void writeData(ArrayList<Course> courses) {
        try {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(getString(R.string.key_courses), gson.toJson(courses));
            sharedPreferencesEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trimCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    public ToolbarUtils.ShowHideToolbarOnScrollingListener getShowHideToolbarListener() {
        return showHideToolbarListener;
    }
}