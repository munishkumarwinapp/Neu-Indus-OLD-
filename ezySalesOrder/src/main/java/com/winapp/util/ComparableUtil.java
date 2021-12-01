package com.winapp.util;

import com.winapp.model.Product;

import java.util.Comparator;

/**
 * Created by Winapp on 25-Sep-17.
 */

public class ComparableUtil implements Comparator<Product> {
    @Override
    public int compare(Product bal1,Product bal2) {
        double balamt = bal1.getBalanceAmount();
        if (balamt == 0) {
            return -1;
        } else if (balamt > 0) {
            return 1;
        }else{
            return 0;
        }
    }

}