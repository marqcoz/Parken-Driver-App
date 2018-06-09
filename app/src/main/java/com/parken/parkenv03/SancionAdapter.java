package com.parken.parkenv03;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class SancionAdapter extends RecyclerView.Adapter<SancionAdapter.ViewHolder> {

        private Context context;
        private List<Sancion> sancion;
        private Cursor items;
        private final OnItemClickListener listener;



        public SancionAdapter(Context context, List<Sancion> sancion, OnItemClickListener listener) {
            this.context = context;
            this.sancion = sancion;
            this.listener = listener;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_sanciones,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.bind(sancion.get(position), listener);

            Glide.with(context).load(sancion.get(position).getImgMapLink()).into(holder.imageMap);

            holder.zona.setText(sancion.get(position).getNombreZonaParken());
            String idep = "Espacio Parken : " + String.valueOf(sancion.get(position).getIdEspacioParken());
            holder.espacio.setText(idep);
            //holder.direccion.setText(sancion.get(position).getDireccionEspacioParken());
            String dinero = "$ "+String.valueOf(sancion.get(position).getMonto())+" MXN";
            holder.monto.setText(dinero);
            String time = sancion.get(position).getTiempo() + " hrs";
            holder.tiempo.setText(time);
            String carro = sancion.get(position).getModeloVehiculo() + " - " + sancion.get(position).getPlacaAutomovilista();
            holder.vehiculo.setText(carro);
            String est = sancion.get(position).getEstatus();
            holder.estatus.setText(est);
            if(est.equals("PAGADA")){
                holder.estatus.setTextColor(Color.argb(255,46,204,113));
                holder.statusIcon.setImageResource(R.drawable.ic_check);
                holder.pago.setVisibility(View.GONE);
            }else {
                holder.statusIcon.setImageResource(R.drawable.ic_alert);
                holder.estatus.setTextColor(Color.argb(255,52,73,94));
                holder.pago.setVisibility(View.VISIBLE);
            }



        }

        @Override
        public int getItemCount() {
            return sancion.size();
        }


        interface OnItemClickListener {
            void onItemClick(Sancion item);
        }

        public  class ViewHolder extends  RecyclerView.ViewHolder{
        //implements View.OnClickListener{

            public ImageView imageMap;
            public  ImageView statusIcon;
            public TextView zona;
            public TextView espacio;
            public TextView direccion;
            public ConstraintLayout pago;
            public TextView vehiculo;
            public TextView monto;
            public TextView estatus;
            public TextView tiempo;
            public Button action;



            public ViewHolder(View itemView) {
                super(itemView);

                imageMap = (ImageView) itemView.findViewById(R.id.map);
                statusIcon = (ImageView) itemView.findViewById(R.id.imageViewStatus);
                zona = (TextView) itemView.findViewById(R.id.zonaParken);
                espacio = (TextView) itemView.findViewById(R.id.idEP);
                tiempo = (TextView) itemView.findViewById(R.id.tiempoSancion);
                //direccion = (TextView) itemView.findViewById(R.id.direccionEP);
                vehiculo = (TextView) itemView.findViewById(R.id.vehiculoSancion);
                monto = (TextView) itemView.findViewById(R.id.precio);
                estatus = (TextView) itemView.findViewById(R.id.estatusSancion);

                pago = itemView.findViewById(R.id.linearLayoutPagar);
                action = (Button)itemView.findViewById(R.id.buttonAction);

                //action.setOnClickListener(this);
                //itemView.setOnClickListener(this);
            }

            public void bind(final Sancion item, final OnItemClickListener listener) {

                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }

                /*
            @Override
            public void onClick(View view) {
                if(view.getTag().toString().equals("action")){
                  //Click
                    Log.d("PressButton", "True");

                    //getAdapterPosition();
                }
                //escucha.onClick(this, obtenerIdAlquiler(getAdapterPosition()));
            }*/
        }

}
