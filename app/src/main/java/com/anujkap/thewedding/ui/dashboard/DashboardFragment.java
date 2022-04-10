package com.anujkap.thewedding.ui.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anujkap.thewedding.Helpers.BaseActivity;
import com.anujkap.thewedding.Helpers.SharedPrefs;
import com.anujkap.thewedding.R;
import com.anujkap.thewedding.classes.TaxiBooking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Date;

public class DashboardFragment extends Fragment {

    Spinner fromSelection, toSelection;
    Button button;
    DatabaseReference mDatabase, users;
    FirebaseAuth mAuth;
    FirebaseUser user;
    BaseActivity activity;
    SharedPrefs prefs;
    String[] places = {"Choose a place", "Jhansi Station", "Orchha Palace", "Orchha Resort", "Janki Villas", "Jhansi Home", "Yatrik Hotel"};
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        fromSelection = root.findViewById(R.id.from_spinner);
        toSelection = root.findViewById(R.id.to_spinner);
        button = root.findViewById(R.id.book_taxi_btn);
        mDatabase = FirebaseDatabase.getInstance(getString(R.string.SERVER_ADDRESS)).getReference();
        users = mDatabase.child("users");
        activity = (BaseActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        prefs = SharedPrefs.getInstance(getActivity());
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter ad = new ArrayAdapter(getActivity(),
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                places);
        ad.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        fromSelection.setAdapter(ad);
        toSelection.setAdapter(ad);

        button.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                activity.showProgressDialog();
                if (validateChoices()) {
                    Date d = new Date();
                    long uniqueid = d.getTime();
                    sendSMS(new TaxiBooking(fromSelection.getSelectedItem().toString(),
                            toSelection.getSelectedItem().toString(),
                            (new Timestamp(uniqueid).toString()),
                            prefs.getName(),
                            prefs.getNumber()));
                }
                activity.hideProgressDialog();
            }
            else{
                requireActivity().requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1101);
            }
        });
    }

    boolean validateChoices(){
        String from = fromSelection.getSelectedItem().toString();
        String to =toSelection.getSelectedItem().toString();
        if(from.equals(to)) {
            Toast.makeText(getActivity(), "To and from cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (from.equals("Choose a place") || to.equals("Choose a place")){
            Toast.makeText(getActivity(), "Please select an option", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected void sendSMS(TaxiBooking booking)
    {
        String toPhoneNumber = getString(R.string.phone_no);
        String smsMessage = booking.toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
//            Toast.makeText(getContext(), "A Taxi has been booked and will contact you shortly",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(getActivity())
                    .setTitle("Success!")
                    .setMessage("A Taxi has been booked and will contact you shortly")
                    .setPositiveButton("Ok", null)
            .show();
        }
        catch (Exception e)
        {
//            Toast.makeText(getContext(),"Booking Failed please try again later",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("Booking Failed please try again later")
                    .setPositiveButton("Ok", null)
                    .show();
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activity.showProgressDialog();
                    if (validateChoices()) {
                        Date d = new Date();
                        long uniqueid = d.getTime();
                        sendSMS(new TaxiBooking(fromSelection.getSelectedItem().toString(),
                                toSelection.getSelectedItem().toString(),
                                (new Timestamp(uniqueid).toString()),
                                prefs.getName(),
                                prefs.getNumber()));
                    }
                    activity.hideProgressDialog();
                }  else {
                    Toast.makeText(getActivity(), "Permission needed for booking, please change this in app settings", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Permission Required")
                            .setMessage("This permission is required to do a taxi booking, please approve it")
                            .setPositiveButton("Ok", (dialogInterface, i) -> requireActivity().requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1101))
                            .setNegativeButton("Cancel", null)
                            .show();
                        }
        }
    }
}