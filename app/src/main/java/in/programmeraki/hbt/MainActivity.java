package in.programmeraki.hbt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import in.programmeraki.hbt.fragment.HistoryActivity;
import in.programmeraki.hbt.fragment.UserProfileFragment;
import in.programmeraki.hbt.utils.Constant;

/**
 * Created by aishwarydhare on 01/02/18.
 */

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    FrameLayout fragment_fl;
    RelativeLayout mainView;
    private boolean isDrawerOpen;
    ImageView drawer_icon;
    Activity activity;
    Bundle item_list_frag_bundle;

    private boolean wantToExit = false;
    public View.OnClickListener nav_drawer_items_listener;

    private final String TAG = "EMR_LOG";

    public static boolean isAlive = false;


    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlive = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        activity = this;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.getString(Constant.p_min, "").isEmpty()){
            editor.putString(Constant.p_min, Constant.p_min_default);
        }
        if(sharedPreferences.getString(Constant.p_max, "").isEmpty()){
            editor.putString(Constant.p_max, Constant.p_max_default);
        }
        if(sharedPreferences.getString(Constant.t_min, "").isEmpty()){
            editor.putString(Constant.t_min, Constant.t_min_default);
        }
        if(sharedPreferences.getString(Constant.t_max, "").isEmpty()){
            editor.putString(Constant.t_max, Constant.t_max_default);
        }
        if(sharedPreferences.getString(Constant.fname, "").isEmpty()){
            editor.putString(Constant.fname, "John");
        }
        if(sharedPreferences.getString(Constant.lname, "").isEmpty()){
            editor.putString(Constant.fname, "John");
        }
        if(sharedPreferences.getString(Constant.email, "").isEmpty()){
            editor.putString(Constant.email, "john.doe123@yourmail.co");
        }
        if(sharedPreferences.getString(Constant.dob, "").isEmpty()){
            editor.putString(Constant.dob, "09/07/1992");
        }
        editor.apply();

        mainView = findViewById(R.id.main_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        fragment_fl = findViewById(R.id.fragment_fl);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                isDrawerOpen = false;
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

        drawer_icon = findViewById(R.id.drawer_icon);
        View.OnClickListener drawer_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDrawerOpen){
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.closeDrawers();
                }
            }
        };
        drawer_icon.setOnClickListener(drawer_listener);

        nav_drawer_items_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_nav_drawer_item_selected(view);
            }
        };

        findViewById(R.id.home_ll).setOnClickListener(nav_drawer_items_listener);
        findViewById(R.id.live_ll).setOnClickListener(nav_drawer_items_listener);
        findViewById(R.id.alerts_ll).setOnClickListener(nav_drawer_items_listener);
        findViewById(R.id.history_ll).setOnClickListener(nav_drawer_items_listener);
        findViewById(R.id.settings_ll).setOnClickListener(nav_drawer_items_listener);
        findViewById(R.id.signout_ll).setOnClickListener(nav_drawer_items_listener);

        set_fragment(1);

        Bundle notification_bundle = getIntent().getExtras();
        if (notification_bundle != null && !notification_bundle.getString("notification", "").equalsIgnoreCase("")) {
            try {
                JSONObject jsonObject = new JSONObject(notification_bundle.getString("notification", ""));
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle(jsonObject.getString("title"))
                        .setMessage(jsonObject.getString("body"))
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Oops, notiff lapsed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    void set_fragment(int id){
        if(id == Constant.selected_frag_id){
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            Log.d(TAG, "set_fragment: already active");
            return;
        }

        Fragment fragment = null;
        String frag_str = "";

        switch (id){
            case 1:
                fragment = new UserProfileFragment();
                frag_str = "UserProfile";
                break;

            case 2:
                startActivity(new Intent(activity, LiveActivity.class));
                break;

            case 3:
                startActivity(new Intent(activity, AlertsActivity.class));
                break;

            case 4:
                startActivity(new Intent(activity, DebugActivity.class));
                break;

            case 5:
                startActivity(new Intent(activity, HistoryActivity.class));
                break;

            default:
//                fragment = new HomeFragment();
//                frag_str = "HomeFragment";
                break;
        }

        if(frag_str.equalsIgnoreCase("UserProfile") || frag_str.isEmpty()){
            drawer_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer_blue));
        } else {
            drawer_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
        }

        if (fragment != null) {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0 &&
                    !frag_str.equalsIgnoreCase("UserProfile")){
                getSupportFragmentManager().beginTransaction().addToBackStack(frag_str)
                        .replace(R.id.fragment_fl, fragment, frag_str).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_fl, fragment, frag_str).commit();
            }
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    public void on_nav_drawer_item_selected(View view){
        switch (view.getId()){
            case R.id.home_ll:
                set_fragment(1);
                break;

            case R.id.live_ll:
                set_fragment(2);
                break;

            case R.id.alerts_ll:
                set_fragment(3);
                break;

            case R.id.settings_ll:
                set_fragment(4);
                break;

            case R.id.history_ll:
                set_fragment(5);
                break;

            case R.id.signout_ll:
                sign_out_user();
                break;

            default:
                // do nothing
                break;
        }
    }


    public void sign_out_user(){
        //do nothing
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if(Constant.selected_frag_id == 1){
            if (wantToExit) {
                try {
                    super.onBackPressed();
                    this.finish();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                wantToExit = true;
            }

            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    wantToExit = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }

//        if(Constant.selected_frag_id == 1){
//            drawer_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
//        }
    }

}
