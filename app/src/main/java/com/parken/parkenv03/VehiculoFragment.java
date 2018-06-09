package com.parken.parkenv03;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class VehiculoFragment extends Fragment{

    //Objects
    ListView mVehiculoList;
    VehiculoAdapter mVehiculoAdapter;
    ShPref session;


    public VehiculoFragment() {
        // Required empty public constructor
    }

    public static VehiculoFragment newInstance(/*parámetros*/) {
        VehiculoFragment fragment = new VehiculoFragment();
        // Setup parámetros
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Gets parámetros
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_vehiculo, container, false);

        mVehiculoList = root.findViewById(R.id.vehiculo_list);
        session = new ShPref(getActivity());
        // Instancia del ListView.
                    // Gets parámetros
            ArrayList<Vehiculo> vehiculos = ObtenerJson(session.getVehiculos());
            mVehiculoAdapter = new VehiculoAdapter(getActivity(), vehiculos);

            //Relacionando la lista con el adaptador
            mVehiculoList.setAdapter(mVehiculoAdapter);

        mVehiculoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Vehiculo currentVehiculo = mVehiculoAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),VehiculoInfoActivity.class);
                intent.putExtra("marca",currentVehiculo.getMarca());
                intent.putExtra("modelo",currentVehiculo.getModelo());
                intent.putExtra("placa",currentVehiculo.getPlaca());
                intent.putExtra("id", currentVehiculo.getId());
                startActivity(intent);
            }
        });

        return root;


    }



    public ArrayList<Vehiculo> ObtenerJson(String result){
        try{
            ArrayList<Vehiculo> listaVehiculo = new ArrayList<Vehiculo>();
            JSONArray jsonArray = new JSONArray(result);
            Vehiculo vehiculo;

            for(int i = 0; i < jsonArray.length(); i++){
                vehiculo = new Vehiculo(jsonArray.getJSONObject(i).getString("id"), jsonArray.getJSONObject(i).getString("marca"),jsonArray.getJSONObject(i).getString("modelo"),jsonArray.getJSONObject(i).getString("placa"));

                /*vehiculo.setCodigoproducto(jsonArray.getJSONObject(i).getInt("idproducto"));
                vehiculo.setIdtipo(jsonArray.getJSONObject(i).getInt("idtipo"));
                Vehiculo.setDescproducto(jsonArray.getJSONObject(i).getString("descproducto"));
                vehiculo.setImagen(jsonArray.getJSONObject(i).getString("imagen"));
*/
                //Agregas a tu lista el nuevo objeto
                listaVehiculo.add(vehiculo);

            }
            //Devuelves el listado
            return listaVehiculo;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }



}
