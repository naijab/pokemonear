package com.naijab.pokemonear.maps;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class PokemonCatchableModel {

    private String message;
    private List<PokemonDataModel> data;

    public PokemonCatchableModel(String message, List<PokemonDataModel> data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PokemonDataModel> getData() {
        return data;
    }

    public void setData(List<PokemonDataModel> data) {
        this.data = data;
    }

}
