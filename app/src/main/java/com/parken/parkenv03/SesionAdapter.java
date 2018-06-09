package com.parken.parkenv03;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.ViewHolder> {

    private Context context;
    private List<Sesion> sesion;
    private Cursor items;
    private final OnItemClickListener listener;
    //private final View.OnClickListener clickListener;



    public SesionAdapter(Context context, List<Sesion> sesion, OnItemClickListener listener) {
        this.context = context;
        this.sesion = sesion;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_sesiones,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(sesion.get(position), listener);

        Glide.with(context).load(sesion.get(position).getImgMapLink()).into(holder.imageMap);
        String carro = sesion.get(position).getModeloVehiculo() + " - " + sesion.get(position).getPlacaVehiculo();
        holder.vehiculo.setText(carro);
        holder.zona.setText(sesion.get(position).getNombreZonaParken());
        String fi = sesion.get(position).getFechaInicio();
        String ff = sesion.get(position).getFechaFinal();
        String inifin;
        if (fi.equals(ff)){
         inifin = sesion.get(position).getHoraInicio() + " - " +  sesion.get(position).getHoraFinal() + " hrs " + fi;
        }else{
            inifin = fi +" " + sesion.get(position).getHoraInicio() + " hrs - " + ff + " " + sesion.get(position).getHoraFinal() + " hrs";
        }
        holder.fechas.setText(inifin);
        String time = sesion.get(position).getTiempo() + " minutos";
        holder.tiempo.setText(time);
        String dinero = "$ "+String.valueOf(sesion.get(position).getMonto())+"0 MXN";
        holder.monto.setText(dinero);
        String idep = "Espacio Parken : " + String.valueOf(sesion.get(position).getIdEspacioParken());
        holder.espacio.setText(idep);
        holder.direccion.setText(sesion.get(position).getDireccionEspacioParken());
        String est = sesion.get(position).getEstatus();
        holder.estatus.setText(est);
        if(est.equals("FINALIZADA")){
            holder.estatus.setTextColor(Color.argb(255,46,204,113));
            holder.statusIcon.setImageResource(R.drawable.ic_check);
            holder.actions.setVisibility(View.GONE);
        }else {
            if(est.equals("SANCIONADA")){
                holder.statusIcon.setImageResource(R.drawable.ic_alert_red);
                holder.estatus.setTextColor(Color.argb(255,244,67,54));
                holder.actions.setVisibility(View.VISIBLE);
                holder.pagar.setVisibility(View.VISIBLE);
                holder.finalizar.setVisibility(View.GONE);
                holder.renovar.setVisibility(View.GONE);

            }else{
                holder.estatus.setTextColor(Color.argb(255,52,73,94));
                holder.statusIcon.setVisibility(View.INVISIBLE);
                holder.actions.setVisibility(View.VISIBLE);
                holder.pagar.setVisibility(View.GONE);
                holder.finalizar.setVisibility(View.VISIBLE);
                holder.renovar.setVisibility(View.VISIBLE);
                holder.tiempo.setVisibility(View.GONE);
            }
        }



    }

    @Override
    public int getItemCount() {
        return sesion.size();
    }


    interface OnItemClickListener {
        void onItemClick(Sesion item, View view);
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        //implements View.OnClickListener{

        public ImageView imageMap;

        public TextView vehiculo;
        public TextView zona;
        public TextView fechas;
        public TextView tiempo;
        public TextView monto;

        public  ImageView statusIcon;
        public TextView estatus;

        public TextView espacio;
        public TextView direccion;


        public LinearLayout minutos;
        public ConstraintLayout actions;

        public Button pagar;
        public Button renovar;
        public Button finalizar;

        public ViewHolder(View itemView) {
            super(itemView);

            imageMap = (ImageView) itemView.findViewById(R.id.map);
            vehiculo = (TextView) itemView.findViewById(R.id.vehiculoSesion);
            zona = (TextView) itemView.findViewById(R.id.zonaParken);
            fechas = (TextView) itemView.findViewById(R.id.tiempoSancion);
            tiempo = (TextView) itemView.findViewById(R.id.tiempoTotal);
            monto = (TextView) itemView.findViewById(R.id.precio);
            statusIcon = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            estatus = (TextView) itemView.findViewById(R.id.estatusSesion);

            espacio = (TextView) itemView.findViewById(R.id.idEP);
            direccion = (TextView) itemView.findViewById(R.id.direccion);

            minutos = itemView.findViewById(R.id.linearTiempoTotal);
            actions = itemView.findViewById(R.id.linearLayoutPagar);
            pagar = (Button)itemView.findViewById(R.id.buttonActionPagar);
            renovar = (Button)itemView.findViewById(R.id.buttonActionRenovar);
            finalizar = (Button)itemView.findViewById(R.id.buttonActionFinalizar);


            //action.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

        public void bind(final Sesion item, final OnItemClickListener listener) {

            pagar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });


            renovar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
            finalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
        }

    }

}

