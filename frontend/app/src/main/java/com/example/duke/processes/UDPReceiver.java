package com.example.duke.processes;

import android.util.Log;

import com.example.duke.helpers.SensorDataParser;
import com.example.duke.model.Sensor;
import com.example.duke.viewmodel.SensorViewModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDPReceiver implements Runnable {
    private static final int BUFFER_SIZE = 1024;
    private final SensorViewModel viewModel;
    private DatagramSocket socket;
    private boolean running = false;

    public UDPReceiver( DatagramSocket socket, SensorViewModel viewModel ) {
        this.viewModel = viewModel;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        running = true;
        viewModel.postLog( "[SYS] Écoute UDP sur port local " + socket.getLocalPort() );
        try {
            while ( running ) {
                byte[] dataReceived = new byte[ BUFFER_SIZE ];

                DatagramPacket packet = new DatagramPacket( dataReceived, dataReceived.length );

                socket.receive( packet );

                String data = new String( packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8 ).trim();

                Log.d( "DEBUG", "Received: " + data );

                if ( data.isEmpty() || data.equals( "[]" ) ) {
                    continue;
                }

                if ( data.startsWith( "[" ) && data.endsWith( "]" ) ) {
                    Sensor sensor = SensorDataParser.parse( data );

                    if ( sensor != null ) {
                        viewModel.postSensor( sensor );
                        viewModel.postLog(
                            "[UDP] Reçu : " +
                            sensor.getValue() + " " + sensor.getUnit() +
                            " (" + sensor.getName() + ")" );
                    } else {
                        viewModel.postLog( "[WARN] Paquet JSON invalide : " + data );
                    }
                } else {
                    viewModel.postLog( "[UDP] Reçu données brutes : " + data );
                }
            }

        } catch ( IOException e ) {
            if ( running ) {
                viewModel.postLog( "[ERR] " + e.getMessage() );
                viewModel.postConnected( false );
            }
        }
    }

    public void stop()
    {
        running = false;
        viewModel.postLog( "[SYS] Socket UDP fermé" );
    }
}
