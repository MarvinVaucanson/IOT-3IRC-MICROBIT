package com.example.duke.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
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

    private final Handler handler = new Handler( Looper.getMainLooper() );
    private TextView textViewLog, textViewLastSync,
            textViewPortSystem, textViewConnectionType, textViewNodeId;
    private SensorViewModel viewModel;
    private Runnable syncRunnable;
    private int secondsAgo = 0;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_system, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        textViewLog = view.findViewById( R.id.textViewLog );
        textViewLastSync = view.findViewById( R.id.textViewLastSync );
        textViewPortSystem = view.findViewById( R.id.textViewPortSystem );
        textViewConnectionType = view.findViewById( R.id.textViewConnectionType );
        textViewNodeId = view.findViewById( R.id.textViewNodeId );

        new Thread( () -> {
            while ( isAdded() )
            {
                Context context = getContext();
                if ( context == null ) break;

                String connectionStatus = "Non-connecté";

                ConnectivityManager connectivityManager = (ConnectivityManager) context
                                                            .getSystemService( Context.CONNECTIVITY_SERVICE );

                if ( connectivityManager != null )
                {
                    Network activeNetwork = connectivityManager.getActiveNetwork();
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities( activeNetwork );

                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );

                    boolean hasPhoneStatePermission = androidx.core.content.ContextCompat.checkSelfPermission( context,
                            Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED;

                    if ( !hasPhoneStatePermission ) {
                        connectionStatus = "Inconnu (il faut donner la permission)";
                    }

                    if ( networkCapabilities != null ) {
                        if ( networkCapabilities.hasTransport( NetworkCapabilities.TRANSPORT_WIFI ) ) {
                            connectionStatus = "WIFI";
                        }
                        else if ( networkCapabilities.hasTransport( NetworkCapabilities.TRANSPORT_CELLULAR ) )
                        {
                            if ( telephonyManager != null && hasPhoneStatePermission ) {
                                if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP ) {
                                    connectionStatus = "3G";
                                } else if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_LTE ) {
                                    connectionStatus = "4G";
                                } else if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_NR ) {
                                   connectionStatus = "5G";
                                }
                                else {
                                    connectionStatus = "Cellulaire";
                                }
                            }
                        }
                        else {
                            connectionStatus = "Inconnu";
                        }
                    }
                }

                String finalConnectionStatus = connectionStatus;

                if ( getActivity() != null ) {
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            textViewConnectionType.setText( finalConnectionStatus );
                        }
                    } );
                }

                try {
                    Thread.sleep( 5000 );
                } catch ( InterruptedException e ) {
                    break;
                }
            }
        }).start();

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        viewModel.getListEntries().observe( getViewLifecycleOwner(), logLines-> {

            for ( int index = 0; index < logLines.size(); index++ ) {
                String time = new SimpleDateFormat( "HH:mm:ss", Locale.getDefault() ).format( new Date() );
                textViewLog.append( time + " " + logLines.get( index ) + "\n" );
            }

//            if ( logLine.startsWith( "[UDP]" ) ) {
//                secondsAgo = 0;
//            }
        } );

        viewModel.getIsConnected().observe( getViewLifecycleOwner(), connected -> {
            if ( connected ) {
                textViewLastSync.setText( "En cours de synchronisation" );
            } else {
                textViewLastSync.setText( "Non connecté" );
            }
        } );

        viewModel.getCurrentServerPort().observe( getViewLifecycleOwner(), port -> {
            if ( port != null ) {
                textViewPortSystem.setText( String.valueOf( port ) );
            } else {
                textViewPortSystem.setText( "Port inconnu" );
            }
        } );

        view.findViewById( R.id.btnDisconnect ).setOnClickListener( v -> {
            viewModel.postLog( "[SYS] Déconnexion demandée par l'utilisateur" );
            viewModel.postConnected( false );
        } );

        startSyncTimer();

    }

    private void startSyncTimer() {
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                secondsAgo++;
                textViewLastSync.setText( "Il y a " + secondsAgo + " seconde" + ( secondsAgo > 1 ? "s" : "" ) );
                handler.postDelayed( this, 1000 );
            }
        };

        handler.postDelayed( syncRunnable, 1000 );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( syncRunnable != null ) {
            handler.removeCallbacks( syncRunnable );
        }
    }
}
