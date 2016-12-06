package net.sgoliver.android.firebasertdb;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PrediccionHolder extends RecyclerView.ViewHolder {
    private View mView;

    public PrediccionHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setFecha(String fecha) {
        TextView field = (TextView) mView.findViewById(R.id.lblFecha);
        field.setText(fecha);
    }

    public void setCielo(String cielo) {
        TextView field = (TextView) mView.findViewById(R.id.lblCielo);
        field.setText(cielo);
    }

    public void setTemperatura(String temp) {
        TextView field = (TextView) mView.findViewById(R.id.lblTemperatura);
        field.setText(temp);
    }

    public void setHumedad(String hum) {
        TextView field = (TextView) mView.findViewById(R.id.lblHumedad);
        field.setText(hum);
    }
}
