package com.parken.parkenv03;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.List;

public class VehiculoAdapter extends ArrayAdapter<Vehiculo> {


    public VehiculoAdapter(Context context, List<Vehiculo> objects) {
        super(context, 0, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item_vehiculos, parent, false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView company = (TextView) convertView.findViewById(R.id.tv_company);

        // Lead actual.
        Vehiculo vehiculo = getItem(position);

        // Setup.
        name.setText(vehiculo.getMarca());
        title.setText(vehiculo.getModelo());
        company.setText(vehiculo.getPlaca());

        return convertView;
    }

}
