package com.example.duke.processes;

import com.example.duke.helpers.SensorDataParser;
import com.example.duke.model.Sensor;
import com.example.duke.viewmodel.SensorViewModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPReceiver {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 1024;

    private Thread listener;
    private DatagramSocket socket;
    private boolean running = false;

    private SensorViewModel viewModel;

    public UDPReceiver( SensorViewModel viewModel ) {
        this.viewModel = viewModel;
    }

    public void start()
    {
        running = true;
        listener = new Thread( ()->
        {
            try {
                socket = new DatagramSocket( PORT );
                viewModel.postConnected( true );
                viewModel.postLog("[SYS] Socket UDP ouvert sur port " + PORT );

                byte[] buffer = new byte[ BUFFER_SIZE ];

                while ( running ) {
                    DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
                    socket.receive( packet );

                    String data = new String( packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8 ).trim();

                    if ( data.startsWith( "[" ) && data.endsWith( "]" ) ) {
                        Sensor sensor = SensorDataParser.parse( data );

                        if ( sensor != null ) {
                            viewModel.postSensor( sensor );
                            viewModel.postLog(
                                "[UDP] Reçu : " +
                                sensor.getValue() + " " + sensor.getUnit() +
                                " (" + sensor.getName() + ")" );
                        } else {
                            viewModel.postLog( "[WARN] Paquet JSON invalide" );
                        }
                    }
                }

            } catch ( IOException e ) {
                if ( running ) {
                    viewModel.postLog( "[ERR] " + e.getMessage() );
                    viewModel.postConnected( false );
                }
            }
        } );

        listener.start();
    }

    public void stop()
    {
        running = false;

        viewModel.postConnected( false );
        viewModel.postLog( "[SYS] Socket UDP fermé" );

        if ( socket != null && !socket.isClosed() ) {
            socket.close();
        }
    }
}
