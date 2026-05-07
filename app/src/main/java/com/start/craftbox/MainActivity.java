package com.start.craftbox;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.navigation.NavigationView;
import com.start.craftbox.Page.AboutFragment;
import com.start.craftbox.Page.CodeEditorFragment;
import com.start.craftbox.Page.HistoryFragment;
import com.start.craftbox.Page.HomeFragment;
import com.start.craftbox.Page.PixelEditorFragment;
import com.start.craftbox.Page.SettingsFragment;
import com.start.craftbox.Page.TexturePackGeneratorFragment;
import com.start.craftbox.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment homeFragment, aboutFragment, historyFragment, settingsFragment, pixelFragment,textureFragment,codeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DynamicColors.applyToActivityIfAvailable(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(homeFragment);
            } else if (id == R.id.nav_history) {
                replaceFragment(historyFragment);
            } else if (id == R.id.nav_settings) {
                replaceFragment(settingsFragment);
            } else if (id == R.id.nav_about) {
                replaceFragment(codeFragment);
            }
            drawerLayout.closeDrawers();
            return true;
        });




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void initFragment() {
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (aboutFragment == null) aboutFragment = new AboutFragment();
        if (historyFragment == null) historyFragment = new HistoryFragment();
        if (settingsFragment == null) settingsFragment = new SettingsFragment();
        if (pixelFragment == null) pixelFragment = new PixelEditorFragment();
        if (textureFragment == null) textureFragment = new TexturePackGeneratorFragment();
        if (codeFragment == null) codeFragment = new CodeEditorFragment();
        replaceFragment(homeFragment);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


}