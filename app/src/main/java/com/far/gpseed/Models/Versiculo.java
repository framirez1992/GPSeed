package com.far.gpseed.Models;

import java.util.UUID;

/**
 * Created by mdsoft on 7/24/2017.
 */

public class Versiculo {
    String rowguid="";
    String texto="";
    String referencia="";
    public Versiculo(String txt, String ref){
        this.texto = txt;
        this.referencia = ref;
        this.rowguid  = UUID.randomUUID().toString();
    }

    public String getrowguid(){
        return rowguid;
    }
    public String getTexto(){
        return texto;
    }
    public String getReferencia(){
        return referencia;
    }
}
