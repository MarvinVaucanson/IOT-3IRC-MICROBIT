package com.example.duke.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duke.model.Sensor;
import com.example.duke.processes.UDPReceiver;
import com.example.duke.processes.UDPSender;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorViewModel extends ViewModel {

    private UDPSender udpSender;
    private UDPReceiver udpReceiver;
    private DatagramSocket sharedSocket;

    private final MutableLiveData<Map<String, List<Sensor>>> deviceMap = new MutableLiveData<>( new HashMap<>() );
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>( false );
    private final MutableLiveData<String> logEntry = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentServerPort = new MutableLiveData<>();

    public LiveData<Map<String, List<Sensor>>> getDeviceMap() { return deviceMap; }
    public LiveData<Boolean> getIsConnected() { return isConnected; }
    public LiveData<String> getLogEntry() { return logEntry; }
    public LiveData<Integer> getCurrentServerPort() { return currentServerPort; }

    public void postDeviceData( Map<String, List<Sensor>> incoming ) {
        Map<String, List<Sensor>> current = new HashMap<>();
        if ( deviceMap.getValue() != null ) {
            current.putAll( deviceMap.getValue() );
        }
        current.putAll( incoming );
        deviceMap.postValue( current );
    }

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

            currentServerPort.postValue( port );

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