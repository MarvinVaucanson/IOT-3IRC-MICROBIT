package com.example.sensorcommand.processes;

import com.example.sensorcommand.viewmodel.SensorViewModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPSender implements Runnable {
    private final int PORT;
    private final String IP_HOST;
    private final DatagramSocket socket;
    private final SensorViewModel viewModel;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private boolean running = false;

    public UDPSender( int port, String ip_host, DatagramSocket socket, SensorViewModel viewModel ) {
        this.PORT = port;
        this.IP_HOST = ip_host;
        this.viewModel = viewModel;
        this.socket = socket;
    }

    // Ajouter un message dans la file d'attente pour l'envoi
    public void sendData( String data ) {
        messageQueue.offer( data );
    }

    @Override
    public void run() {
        running = true;
        try {
            // Tant que l'utilisateur est connecté, on continue à envoyer
            while ( running ) {
                // Attendre qu'un message soit disponible dans la file d'attente (bloquant)
                String message = messageQueue.take();

                if ( !running ) break;

                // Résoudre l'adresse IP du serveur
                InetAddress serverAddress = InetAddress.getByName( IP_HOST );

                byte[] messageToSend = message.getBytes();

                // Créer et envoyer le paquet UDP
                DatagramPacket packet = new DatagramPacket( messageToSend, messageToSend.length, serverAddress, PORT );

                socket.send( packet );

                // Envoyer les informations sur le log
                viewModel.postLog( "[UDP] Envoyé '" + message + "' vers " + IP_HOST + ":" + PORT + " depuis port local " + socket.getLocalPort() );
                viewModel.postConnected( true );

            }

        } catch ( IOException | InterruptedException e ) {
            if ( running ) {
                viewModel.postLog( "[ERR] " + e.getMessage() );
                viewModel.postConnected( false );
            }
        }
    }

    // Fonction pour arrêter le thread
    public void stop() {
        running = false;
        // Débloquer le take() en envoyant un message factice
        messageQueue.offer( "Stop" );

        viewModel.postConnected( false );
        viewModel.postLog( "[SYS] Socket UDP fermé" );
    }

}
