package com.parken.parkenv03;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {

    ListView mAppsList;
    AppAdapter mAppAdapter;
    ShPref session;

    public AppsFragment() {

    }


    public static AppsFragment newInstance() {
        AppsFragment fragment = new AppsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        mAppsList = root.findViewById(R.id.apps_list);
        session = new ShPref(getActivity());

        ArrayList<ResolveInfo> app = obtenerApps();
        mAppAdapter = new AppAdapter(getActivity(), app);

        //Relacionando la lista con el adaptador
        mAppsList.setAdapter(mAppAdapter);
        mAppsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        int currentPosition = mAppsList.getSelectedItemPosition();

            currentPosition = mAppsList.getFirstVisiblePosition();

            Log.d("Position", String.valueOf(currentPosition));

            mAppsList.setSelected(false);

            mAppsList.getAdapter();
//                    getView().setSelected(true);

            mAppsList.setSelection(0);



        final PackageManager packageManager = getContext().getPackageManager();
        mAppsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

                ResolveInfo currentVehiculo = mAppAdapter.getItem(position);
                Log.d("App", currentVehiculo.loadLabel(packageManager).toString());
                //mAppsList.setItemChecked(position, true);
                //mAppAdapter.set currentVehiculo
            }
        });
        return root;
    }

    public ArrayList<ResolveInfo> obtenerApps(){

        ResolveInfo app;
        ArrayList<ResolveInfo> listaApps = new ArrayList<ResolveInfo>();

        try {
            Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
            PackageManager packageManager = getContext().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, PackageManager.GET_META_DATA);

            for(ResolveInfo info: activities){
                if(!info.loadLabel(packageManager).toString().equals("Uber"))
                listaApps.add(info);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return listaApps;
    }
}
