package com.example.duke.processes;

import com.example.duke.viewmodel.SensorViewModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPSender implements Runnable {
    private final int PORT;
    private final String IP_HOST;
    private final SensorViewModel viewModel;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private DatagramSocket socket;
    private boolean running = false;

    public UDPSender( int port, String ip_host, DatagramSocket socket, SensorViewModel viewModel ) {
        this.PORT = port;
        this.IP_HOST = ip_host;
        this.viewModel = viewModel;
        this.socket = socket;
    }

    public void sendData( String data ) {
        messageQueue.offer( data );
    }

    @Override
    public void run() {
        running = true;
        try {
            while ( running ) {
                String message = messageQueue.take();

                if ( !running ) break;

                InetAddress serverAddress = InetAddress.getByName( IP_HOST );

                byte[] messageToSend = message.getBytes();

                DatagramPacket packet = new DatagramPacket( messageToSend, messageToSend.length, serverAddress, PORT );

                socket.send( packet );

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

    public void stop() {
        running = false;
        messageQueue.offer( "Stop" );

        viewModel.postConnected( false );
        viewModel.postLog( "[SYS] Socket UDP fermé" );
    }

}
