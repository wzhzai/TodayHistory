package com.example.wangzhengze.todayhistory;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wangzhengze.todayhistory.fragment.TodayHistoryFragment;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        printAllFileNameStartWithM();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            setCurrentFragment(id);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCurrentFragment(int id) {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_camara:
                fragment = TodayHistoryFragment.newInstance("", "");
                break;
        }
        fm.beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    private void printAllFileNameStartWithM() {
        Observable<File> observable = Observable.from(Environment.getExternalStorageDirectory().listFiles());
        observable
                .flatMap(new FlatFileList())
                .filter(file -> file.getName().toLowerCase().startsWith("m"))
                .subscribe(s1 -> Log.e(TAG, "name = " + s1));
    }

    class FlatFileList implements Func1<File, Observable<File>> {

        @Override
        public Observable<File> call(File file) {
            if (checkIsDirectory(file)) {
                if (file.getName().toLowerCase().startsWith("m")) {
                    Log.e(TAG, "name = " + file);
                }
                return Observable.from(file.listFiles()).flatMap(new FlatFileList());
            }
            return Observable.just(file);
        }
    }

    private String[] getFileList(String path) {
        File file = new File(path);
        return file.list();
    }

    private boolean checkIsDirectory(File file) {
        return file.isDirectory();
    }
}
