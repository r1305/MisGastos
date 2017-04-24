package com.example.lenovo.misgastos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.misgastos.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 05/04/2017.
 */

public class GastosRecycler  extends RecyclerView.Adapter<GastosRecycler.ViewHolder>{


    List<JSONObject> list=new ArrayList<>();


    public GastosRecycler(List<JSONObject> list) {
        this.list = list;
    }
    @Override
    public GastosRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gastos,parent,false));
    }

    @Override
    public void onBindViewHolder(GastosRecycler.ViewHolder holder, int position) {
        JSONObject o=list.get(position);

        try {
            holder.descripcion.setText(o.get("desc").toString().toUpperCase());
            holder.monto.setText("S/."+o.get("monto").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }else {
            return list.size();

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView descripcion,monto;

        public ViewHolder(View itemView) {
            super(itemView);
            descripcion=(TextView)itemView.findViewById(R.id.item_descripcion);
            monto=(TextView)itemView.findViewById(R.id.item_monto);

        }
    }
}
