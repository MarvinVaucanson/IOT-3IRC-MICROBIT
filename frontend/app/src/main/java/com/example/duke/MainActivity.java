package com.example.duke;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.duke.processes.UDPReceiver;
import com.example.duke.ui.fragments.PrioritiesFragment;
import com.example.duke.ui.fragments.ServersFragment;
import com.example.duke.ui.fragments.SystemFragment;
import com.example.duke.viewmodel.SensorViewModel;
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
        fragmentManager.beginTransaction()
                .replace( R.id.fragment_container, fragment )
                .commit();
    }
}