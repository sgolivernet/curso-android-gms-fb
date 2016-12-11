package net.sgoliver.android.firebasertdb;

public class Prediccion {
    private String cielo;
    private long temperatura;
    private double humedad;
    private String fecha;

    public Prediccion() {
        //Es obligatorio incluir constructor por defecto
    }

    public Prediccion(String fecha, String cielo, long temperatura, double humedad)
    {
        this.fecha = fecha;
        this.cielo = cielo;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCielo() {
        return cielo;
    }

    public void setCielo(String cielo) {
        this.cielo = cielo;
    }

    public long getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(long temperatura) {
        this.temperatura = temperatura;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    @Override
    public String toString() {
        return "Prediccion{" +
                "fecha='" + fecha + '\'' +
                ", cielo='" + cielo + '\'' +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }
}
