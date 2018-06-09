package com.parken.parkenv03;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.PackageManager;

import java.util.List;

public class AppAdapter extends ArrayAdapter<ResolveInfo> {

    private List<ResolveInfo> objects = null;
    private Context context;
    private PackageManager packageManager;

    public AppAdapter (Context context, List<ResolveInfo> objects) {

        super(context, 0, objects);
        this.context = context;
        this.objects = objects;
        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item_apps, parent, false);
        }

        // Referencias UI.
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.imageViewAppIcon);
        TextView appName = (TextView) convertView.findViewById(R.id.textViewAppName);


        // Lead actual.
        ResolveInfo app = getItem(position);


        // Setup.
        appName.setText(app.loadLabel(packageManager));
        appIcon.setImageDrawable(app.loadIcon(packageManager));

        return convertView;
    }
}
