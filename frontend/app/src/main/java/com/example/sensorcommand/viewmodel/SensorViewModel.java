package com.example.sensorcommand.viewmodel;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sensorcommand.model.Sensor;
import com.example.sensorcommand.processes.UDPReceiver;
import com.example.sensorcommand.processes.UDPSender;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

public class SensorViewModel extends ViewModel {

    private UDPSender udpSender;
    private UDPReceiver udpReceiver;
    private DatagramSocket sharedSocket;

    private final MutableLiveData<Map<String, List<Sensor>>> deviceMap = new MutableLiveData<>( new HashMap<>() );
    private final MutableLiveData<List<String>> logListEntries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>( false );
    private final MutableLiveData<Integer> currentServerPort = new MutableLiveData<>();

    public LiveData<Map<String, List<Sensor>>> getDeviceMap() { return deviceMap; }
    public LiveData<List<String>> getListEntries() { return logListEntries; }
    public LiveData<Boolean> getIsConnected() { return isConnected; }
    public LiveData<Integer> getCurrentServerPort() { return currentServerPort; }

    public void postDeviceData( Map<String, List<Sensor>> incomingData ) {
        Map<String, List<Sensor>> current = new HashMap<>();
        if ( deviceMap.getValue() != null ) {
            current.putAll( deviceMap.getValue() );
        }

        current.putAll( incomingData );
        deviceMap.postValue( current );
    }

    public void postLog( String message ) {
        new Handler( Looper.getMainLooper() ).post( () ->
        {
            List<String> current = logListEntries.getValue();

            if ( current == null ) current = new ArrayList<>();

            List<String> updated = new ArrayList<>( current );
            updated.add( message );

            logListEntries.setValue( updated );
        } );
    }
    public void postConnected( boolean bool ) { isConnected.postValue( bool ); }

    public void connectServer( int port, String ip ) {
        stopAll();
        try {
            sharedSocket = new DatagramSocket( port );

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