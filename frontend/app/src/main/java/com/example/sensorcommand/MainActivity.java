package com.example.sensorcommand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sensorcommand.ui.fragments.PrioritiesFragment;
import com.example.sensorcommand.ui.fragments.ServersFragment;
import com.example.sensorcommand.ui.fragments.SystemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        bottomNav = findViewById( R.id.bottom_nav );
        fragmentManager = getSupportFragmentManager();

        loadFragment( new ServersFragment() );
        bottomNav.setSelectedItemId( R.id.nav_servers );

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;

            if ( item.getItemId() == R.id.nav_servers ) {
                selected = new ServersFragment();
            } else if ( item.getItemId() == R.id.nav_priorities ) {
                selected = new PrioritiesFragment();
            } else if ( item.getItemId() == R.id.nav_system ) {
                selected = new SystemFragment();
            } else {
                return false;
            }

            loadFragment( selected );
            return true;
        } );
    }

    private void loadFragment( Fragment fragment ) {
        fragmentManager.popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
        fragmentManager.beginTransaction()
                .replace( R.id.fragment_container, fragment )
                .commit();
    }
}