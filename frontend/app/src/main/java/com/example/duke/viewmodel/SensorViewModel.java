package com.example.duke.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duke.model.Sensor;

public class SensorViewModel extends ViewModel {
    private final MutableLiveData<Sensor> lastSensor = new MutableLiveData<>();
    private final MutableLiveData<String> logEntry = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>( false );

    public LiveData<Sensor> getLastSensor() { return lastSensor; }
    public LiveData<String> getLogEntry() { return logEntry; }
    public LiveData<Boolean> getIsConnected() { return isConnected; }

    public void postSensor( Sensor sensor ) { lastSensor.postValue( sensor ); }
    public void postLog( String entry ) { logEntry.postValue( entry ); }
    public void postConnected( boolean bool ) { isConnected.postValue( bool ); }

}
