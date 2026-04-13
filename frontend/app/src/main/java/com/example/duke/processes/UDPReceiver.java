package com.example.duke.processes;

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

    public interface OnDataReceived {
        void onData( String raw );
    }

    public void start( OnDataReceived callback )
    {
        running = true;
        listener = new Thread( ()->
        {
            try {
                socket = new DatagramSocket( PORT );
                byte[] buffer = new byte[ BUFFER_SIZE ];

                while ( running ) {
                    DatagramPacket packet = new DatagramPacket( buffer, buffer.length );
                    socket.receive( packet );

                    String data = new String( packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8 );
                    callback.onData( data );
                }

            } catch ( IOException e ) {
                if ( running ) {
                    e.printStackTrace();
                }
            }
        } );

        listener.start();
    }

    public void stop() {
        running = false;
        if ( socket != null && !socket.isClosed() ) {
            socket.close();
        }
    }
}
