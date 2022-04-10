package com.anujkap.thewedding.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anujkap.thewedding.R;
import com.anujkap.thewedding.adapters.InformationAdapter;
import com.anujkap.thewedding.classes.Event;
import com.anujkap.thewedding.classes.FAQ;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    Button logout;
    ArrayList<FAQ> faqs = new ArrayList<>();
    RecyclerView info;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        logout = root.findViewById(R.id.btn_logout);
        info = root.findViewById(R.id.recycler_information);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        info.setLayoutManager(new LinearLayoutManager(getActivity()));
        addDetails();
        info.setAdapter(new InformationAdapter(faqs, getActivity()));
    }

    void addDetails(){
        faqs.add(new FAQ("Having trouble with a taxi we provide?", "Please contact:\n\n"+"Surendra\nIn-charge off all the drivers at Station or Orchha\n"+
                            "+918957574650 \n\n"+ "Ramesh Rajput\nOverall in-charge and provider of the booked taxis\n"+"+919415187494 \n\n"+"Harish Jhansi Station Coordinator\n"+
                                    "+919935228824"));
        faqs.add(new FAQ("Want to explore around?","The hotel has a travel desk from which you can book a cab for places like Khajuraho or to explore Orchha. Please contact this number.\n" +
                "\nMobin Tour Aids\n"+" +919415030987 "));
        faqs.add(new FAQ("Hotel Chandela Khajuraho Booking In-Charge", "Ishaq Khan\n"+"+917415525146 \n"+"+919425304979"));
        faqs.add(new FAQ("Want some Makeup done?", "There is a local beautician that has been tried and tested. The following are her charges: \n\n"+
                    "Open hair style - 300/- \n" +
                "Hair bun - 500/- \n" +
                "Only makeup not hair - 1300/- \n" +
                "Saree draping - 250/-  \n" +
                "Proper makeup, hair and draping - 2000/- \n"+
                "At the Venue per head - 2000/- \n\n"+
                "Pinky Jha - Orchha Makeup \n"+ "+91 83497 48164"));
        faqs.add(new FAQ("Places to explore in Orchha", "Raja Ram Mandir\n"));

    }

}