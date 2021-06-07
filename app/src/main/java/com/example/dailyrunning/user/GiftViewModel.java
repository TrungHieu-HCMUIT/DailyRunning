package com.example.dailyrunning.user;


import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.utils.CustomDialog;

public class GiftViewModel extends ViewModel {
    public void exchangeClick(ExchangeClick exchangeClick)
    {
        exchangeClick.onExchangeClick();
    }
    public interface ExchangeClick
    {
        void onExchangeClick();
    }
}
