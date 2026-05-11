package com.example.sensorcommand.processes;

import com.example.sensorcommand.helpers.SensorDataParser;
import com.example.sensorcommand.model.Sensor;
import com.example.sensorcommand.viewmodel.SensorViewModel;
import com.goterl.lazysodium.LazySodiumAndroid;
import com.goterl.lazysodium.SodiumAndroid;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.utils.Key;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPReceiver implements Runnable {
    private static final int BUFFER_SIZE = 4096;
    private final SensorViewModel viewModel;
    private final DatagramSocket socket;
    private boolean running = false;

    public UDPReceiver( DatagramSocket socket, SensorViewModel viewModel ) {
        this.viewModel = viewModel;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        running = true;

        // Envoyer l'information au log
        viewModel.postLog( "[SYS] Écoute UDP sur port local " + socket.getLocalPort() );

        try {
            // Tant que l'utilisateur est connecté, on continue à recevoir
            while ( running )
            {
                // Initialiser un buffer les données reçues
                byte[] dataReceived = new byte[ BUFFER_SIZE ];

                // Initialiser un paquet
                DatagramPacket packet = new DatagramPacket( dataReceived, dataReceived.length );

                // Recevoir le paquet (bloquant)
                socket.receive( packet );

                // Initialiser un objet Lazy Sodium pour le déchiffrement
                LazySodiumAndroid lazySodium = new LazySodiumAndroid( new SodiumAndroid() );

                byte[] receivedBytes = packet.getData();
                byte[] nonce = Arrays.copyOfRange( receivedBytes, 0, 24 );
                byte[] cipherBytes = Arrays.copyOfRange( receivedBytes, 24, packet.getLength() );

                String cipherText = lazySodium.toHexStr( cipherBytes );

                // Récupérer la clé
                Key key = Key.fromBytes( "93vrSd0Ne1dDOa50ZQDum7FzZPTmmPvw".getBytes( StandardCharsets.UTF_8 ) );

                String decryptedData = lazySodium.cryptoSecretBoxOpenEasy( cipherText, nonce, key );

                // Vérifier si les données sont bien de format JSON et elles ne sont pas vides
                if ( decryptedData != null && decryptedData.startsWith( "[" ) && decryptedData.endsWith( "]" ) )
                {
                    Map<String, List<Sensor>> parsedData = SensorDataParser.parse( decryptedData );

//                    Log.d( "DEBUG", "Data parsed: " + decryptedData );

                    // Vérifier si le parseur a été réussi
                    if ( !parsedData.isEmpty() )
                    {
                        // Envoyer les données
                        viewModel.postDeviceData( parsedData );

                        // Envoyer les informations sur le log
                        for ( Map.Entry<String, List<Sensor>> deviceEntry : parsedData.entrySet() ) {
                            viewModel.postLog(
                                "[UDP] Reçu " + deviceEntry.getValue().size() +
                                " capteur" + ( deviceEntry.getValue().size() > 1 ? "s" : "" ) +
                                " de " + deviceEntry.getKey() );
                        }
                    } else {
                        viewModel.postLog( "[WARN] Paquet JSON invalide : " + decryptedData );
                    }
                }
            }

        } catch ( IOException | SodiumException e ) {
            if ( running ) {
                viewModel.postLog( "[ERR] " + e.getMessage() );
                viewModel.postConnected( false );
            }
        }
    }

    // Fonction pour arrêter le thread
    public void stop()
    {
        running = false;
        viewModel.postLog( "[SYS] Socket UDP fermé" );
    }
}
