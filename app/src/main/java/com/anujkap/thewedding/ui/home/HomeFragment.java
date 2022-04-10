package com.anujkap.thewedding.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anujkap.thewedding.Helpers.SharedPrefs;
import com.anujkap.thewedding.classes.Event;
import com.anujkap.thewedding.Helpers.ObjectSerializer;
import com.anujkap.thewedding.R;
import com.anujkap.thewedding.adapters.EventTimelineAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    SharedPrefs prefs;
    ArrayList<Event> list = new ArrayList<>();
    RecyclerView events;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        prefs = SharedPrefs.getInstance(getActivity());
        events = root.findViewById(R.id.timeline_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        events.setLayoutManager(new LinearLayoutManager(getActivity()));
        try{
            list = (ArrayList<Event>) ObjectSerializer.deserialize(prefs.getEvents());
            Log.d("Here", list.get(0).getDescription());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        events.setAdapter(new EventTimelineAdapter(list, getActivity()));

    }
}