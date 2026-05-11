package com.example.sensorcommand.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sensorcommand.R;
import com.example.sensorcommand.viewmodel.SensorViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SystemFragment extends Fragment {

    private final Handler handler = new Handler( Looper.getMainLooper() );
    private SensorViewModel viewModel;
    private Runnable syncRunnable;
    private TextView textViewLog, textViewLastSync, textViewPortSystem, textViewConnectionType, textViewConnectionStatus, textViewNodeId;
    private ImageView imageViewConnectionType, imageViewDot;
    private int secondsAgo = 0;
    private int displayedLogCount = 0;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_system, container, false );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        textViewLog = view.findViewById( R.id.textViewLog );
        textViewLastSync = view.findViewById( R.id.textViewLastSync );
        textViewPortSystem = view.findViewById( R.id.textViewPortSystem );
        textViewConnectionType = view.findViewById( R.id.textViewConnectionType );
        textViewConnectionStatus = view.findViewById( R.id.textViewConnectionStatus );
        textViewNodeId = view.findViewById( R.id.textViewNodeId );

        imageViewConnectionType = view.findViewById( R.id.imageViewConnectionType );
        imageViewDot = view.findViewById( R.id.imageViewDot );

        // Lancer un thread pour récupérer le type de connexion périodiquement
        new Thread( () -> {
            // Tant que l'utilisateur sur l'écran de Log Système
            while ( isAdded() )
            {
                Context context = getContext();
                if ( context == null ) break;

                String connectionStatus = "Non-connecté";

                int imageConnectionType = -1;
                int imageConnectionStatus = -1;

                String connectionLabel = "NON CONNECTÉ";
                int connectionColor = Color.RED;

                // Initilisation une gestion de connexion
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                                                            .getSystemService( Context.CONNECTIVITY_SERVICE );

                // Vérifier si la gestion a été bien créée
                if ( connectivityManager != null )
                {
                    // Récupérer l'objet du réseau connecté actuellement
                    Network activeNetwork = connectivityManager.getActiveNetwork();

                    // Récupérer les informations sur le réseau connecté
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities( activeNetwork );

                    // Initialiser une gestion de téléphone
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );

                    // Vérifier si l'utilisateur a donnée la permission
                    boolean hasPhoneStatePermission = androidx.core.content.ContextCompat.checkSelfPermission( context,
                            Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED;

                    if ( !hasPhoneStatePermission && activeNetwork != null ) {
                        connectionStatus = "Inconnu (il faut donner la permission)";
                        imageConnectionType = R.drawable.ic_cellular;
                        imageConnectionStatus = R.drawable.ic_dot_green;
                    }

                    // Vérifier si les informations du réseau a été bien récupérées
                    if ( networkCapabilities != null )
                    {
                        connectionLabel = "LIAISON STABLE";
                        connectionColor = Color.parseColor( "#00E5CC");

                        // Si c'est du wifi
                        if ( networkCapabilities.hasTransport( NetworkCapabilities.TRANSPORT_WIFI ) ) {
                            connectionStatus = "WiFi (Connecté)";
                            imageConnectionType = R.drawable.ic_wifi;
                        }
                        // Si c'est des données cellulaires
                        else if ( networkCapabilities.hasTransport( NetworkCapabilities.TRANSPORT_CELLULAR ) )
                        {
                            imageConnectionType = R.drawable.ic_cellular;
                            // Vérifier la permission
                            if ( telephonyManager != null && hasPhoneStatePermission ) {
                                // Connexion 3G
                                if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP ) {
                                    connectionStatus = "3G (Connecté)";
                                }
                                // Connexion 4G
                                else if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_LTE ) {
                                    connectionStatus = "4G (Connecté)";
                                }
                                // Connexion 5G
                                else if ( telephonyManager.getDataNetworkType() == TelephonyManager.NETWORK_TYPE_NR ) {
                                   connectionStatus = "5G (Connecté)";
                                }
                                // Les autres types de connexion
                                else {
                                    connectionStatus = "Cellulaire (Connecté)";
                                }
                            }
                        }
                        else {
                            connectionStatus = "Connexion inconnu";
                        }
                    }
                }

                String finalConnectionStatus = connectionStatus;
                String finalConnectionLabel = connectionLabel;

                int finalImageConnectionType = imageConnectionType;
                int finalImageConnectionStatus = imageConnectionStatus;
                int finalConnectionColor = connectionColor;

                if ( getActivity() != null )
                {
                    getActivity().runOnUiThread( () ->
                    {
                        // Mettre le type de connexion du réseau connecté
                        textViewConnectionType.setText( finalConnectionStatus );
                        textViewConnectionStatus.setText( finalConnectionLabel );
                        textViewConnectionStatus.setTextColor( finalConnectionColor );

                        // Mettre l'icône du type de connexion
                        if ( finalImageConnectionType != -1 ) {
                            imageViewConnectionType.setImageResource( finalImageConnectionType );
                        }

                        if ( finalImageConnectionStatus != -1 ) {
                            imageViewDot.setImageResource( finalImageConnectionStatus );
                        }
                    } );
                }

                // Rafraîchir le type de connexion tous les 5 secondes
                try {
                    Thread.sleep( 5000 );
                } catch ( InterruptedException e ) {
                    break;
                }
            }
        } ).start();

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        // Observer le log
        viewModel.getListEntries().observe( getViewLifecycleOwner(), logLines-> {

            for ( int index = displayedLogCount; index < logLines.size(); index++ ) {
                String time = new SimpleDateFormat( "HH:mm:ss", Locale.getDefault() ).format( new Date() );
                textViewLog.append( time + " " + logLines.get( index ) + "\n" );
            }
            displayedLogCount = logLines.size();

        } );

        // Observer l'état de synchronisation
        viewModel.getIsConnected().observe( getViewLifecycleOwner(), connected -> {
            if ( connected ) {
                textViewLastSync.setText( "En cours de synchronisation" );
            }
            else {
                textViewLastSync.setText( "Non connecté au serveur" );
            }
        } );

        // Observer le port du réseau connecté
        viewModel.getCurrentServerPort().observe( getViewLifecycleOwner(), port -> {
            if ( port != null ) {
                textViewPortSystem.setText( "PORT: " + String.valueOf( port ) );
            }
        } );

        // Bouton pour se déconnecter du serveur
        view.findViewById( R.id.btnDisconnect ).setOnClickListener( v -> {
            viewModel.postLog( "[SYS] Déconnexion demandée par l'utilisateur" );
            viewModel.postConnected( false );

            getParentFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container, new ServersFragment() )
                    .addToBackStack( null )
                    .commit();

            stopSync();

        } );

        // Appel la synchronisation du compteur de synchronisation
        startSync();

    }

    private void startSync() {

        // Initiliser le thread pour synchroniser les données
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                secondsAgo++;
                textViewLastSync.setText( "Il y a " + secondsAgo + " seconde" + ( secondsAgo > 1 ? "s" : "" ) );
                handler.postDelayed( this, 10000 );
            }
        };

        handler.postDelayed( syncRunnable, 10000 );
    }

    private void stopSync() {
        if ( syncRunnable != null ) {
            handler.removeCallbacks( syncRunnable );
            syncRunnable = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Enlever la synchronisation si quitter de l'écran
        stopSync();
    }
}
