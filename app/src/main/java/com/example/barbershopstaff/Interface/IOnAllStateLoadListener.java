package com.example.barbershopstaff.Interface;

import com.example.barbershopstaff.Model.City;

import java.util.List;

public interface IOnAllStateLoadListener {
    void onAllstateLoadSuccess(List<City> cityList);
    void onAllstateLoadFailed(String message);
}
