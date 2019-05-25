package com.far.gpseed.Models;

/**
 * Created by mdsoft on 6/1/2017.
 */

public class CV {
    String Clave ="";
    String Valor="";
    String extraValue="";

    public  CV(String clave, String valor){
        setClave(clave);
        setValor(valor);
    }
    public  CV(String clave, String valor, String extra){
        setClave(clave);
        setValor(valor);
        setextraValue(extra);
    }
    public String getValor() {
        return Valor;
    }

    public void setValor(String valor) {
        Valor = valor;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String clave) {
        Clave = clave;
    }

    public String getextraValue() {
        return extraValue;
    }

    public void setextraValue(String extra) {
        extraValue = extra;
    }


    @Override
    public String toString(){
        return getValor();
    }

}
