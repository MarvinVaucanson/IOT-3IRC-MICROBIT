package com.example.duke.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duke.model.Sensor;
import com.example.duke.processes.UDPReceiver;
import com.example.duke.processes.UDPSender;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SensorViewModel extends ViewModel {

    private UDPSender udpSender;
    private UDPReceiver udpReceiver;
    private DatagramSocket sharedSocket;

    private final MutableLiveData<Sensor> lastSensor = new MutableLiveData<>();
    private final MutableLiveData<String> logEntry = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>( false );

    public LiveData<Sensor> getLastSensor() { return lastSensor; }
    public LiveData<String> getLogEntry() { return logEntry; }
    public LiveData<Boolean> getIsConnected() { return isConnected; }

    public void postSensor( Sensor sensor ) { lastSensor.postValue( sensor ); }
    public void postLog( String entry ) { logEntry.postValue( entry ); }
    public void postConnected( boolean bool ) { isConnected.postValue( bool ); }

    public void connectServer( int port, String ip ) {
        stopAll();

        try {
            sharedSocket = new DatagramSocket();

            udpReceiver = new UDPReceiver( sharedSocket, this );
            udpSender = new UDPSender( port, ip, sharedSocket, this );

            new Thread( udpReceiver ).start();
            new Thread( udpSender ).start();

            udpSender.sendData( "data" );

        } catch ( SocketException e ) {
            postLog( "[ERR] Impossible de créer le socket : " + e.getMessage() );
            postConnected( false );
        }
    }

    public void sendCommand( String command ) {
        if ( udpSender != null ) {
            udpSender.sendData( command );
            postLog( "[SYS] Commande envoyée : " + command );
        } else {
            postLog( "[WARN] Pas de connexion active" );
        }
    }

    private void stopAll() {
        if ( udpSender != null ) {
            udpSender.stop();
            udpSender = null;
        }
        if ( udpReceiver != null ) {
            udpReceiver.stop();
            udpReceiver = null;
        }

        if ( sharedSocket != null && !sharedSocket.isClosed() ) {
            sharedSocket.close();
            sharedSocket = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopAll();
    }
}
