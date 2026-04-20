package com.example.duke.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.duke.R;
import com.example.duke.viewmodel.SensorViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SystemFragment extends Fragment {

    private TextView textViewLog, textViewLastSync;
    private Handler handler = new Handler( Looper.getMainLooper() );
    private int secondsAgo = 0;

    private SensorViewModel viewModel;
    private Runnable syncRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_system, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewLog = view.findViewById( R.id.textViewLog );
        textViewLastSync = view.findViewById( R.id.textViewLastSync );

        viewModel = new ViewModelProvider( requireActivity() )
                .get( SensorViewModel.class );

        viewModel.getLogEntry().observe( getViewLifecycleOwner(), logLine-> {
            String time = new SimpleDateFormat( "HH:mm:ss", Locale.getDefault() ).format( new Date() );
            textViewLog.append( time + " " + logLine + "\n" );

            if ( logLine.startsWith( "[UDP]" ) ) {
                secondsAgo = 0;
            }
        });

        viewModel.getIsConnected().observe( getViewLifecycleOwner(), connected -> {
            if ( connected ) {
                textViewLastSync.setText( "en cours de synchronisation" );
            } else {
                textViewLastSync.setText( "non connecté" );
            }
        });

        view.findViewById( R.id.btnDisconnect ).setOnClickListener( v -> {
            viewModel.postLog( "[SYS] Déconnexion demandée par l'utilisateur" );
        });

        startSyncTimer();

    }



    private void startSyncTimer() {
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                secondsAgo++;
                textViewLastSync.setText( "il y a " + secondsAgo + " seconde" + ( secondsAgo > 1 ? "s" : "" ) );
                handler.postDelayed( this, 1000 );
            }
        };

        handler.postDelayed( syncRunnable, 1000 );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages( syncRunnable );
    }
}
