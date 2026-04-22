package com.example.duke.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duke.R;
import com.example.duke.helpers.SensorAdapter;
import com.example.duke.model.Sensor;
import com.example.duke.processes.TestDataLoader;
import com.example.duke.processes.UDPReceiver;
import com.example.duke.processes.UDPSender;
import com.example.duke.viewmodel.SensorViewModel;

import java.util.ArrayList;
import java.util.List;

public class PrioritiesFragment extends Fragment {

    private static final boolean TEST_MODE = false;
    private SensorAdapter adapter;
    private List<Sensor> sensors = new ArrayList<>();

    private SensorViewModel viewModel;

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        return inflater.inflate( R.layout.fragment_priorities, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById( R.id.recyclerViewSensors );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );
        adapter = new SensorAdapter( sensors );
        recyclerView.setAdapter( adapter );

        viewModel = new ViewModelProvider( requireActivity() ).get( SensorViewModel.class );

        viewModel.getLastSensor().observe( getViewLifecycleOwner(), sensor -> {
            updateSensor( sensor );
        });

        if ( TEST_MODE ) {
            loadTestData();
        }
    }

    private void loadTestData() {
        String json = TestDataLoader.loadRawJson(requireContext());
        if (json == null) return;

        List<Sensor> testSensors = TestDataLoader.parseAll(json);
        sensors.addAll( testSensors );
        adapter.notifyDataSetChanged();
    }

    private void updateSensor( Sensor newSensor ) {
        for ( int i = 0; i < sensors.size(); i++ ) {
            if ( sensors.get( i ).getNumber() == newSensor.getNumber() ) {
                sensors.set( i, newSensor );
                adapter.notifyItemChanged( i );
                return;
            }
        }

        sensors.add( newSensor );
        adapter.notifyItemInserted( sensors.size() -1 );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
