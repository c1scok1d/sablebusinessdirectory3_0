package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

/**
 * Created by MAC Internet Services on 1/10/18.
 * Contact Email : admin@thesablebusinessdirectory.com
 */


public class PSContext {

    public Context context;

    @Inject
    PSContext(Application App) {
        this.context = App.getApplicationContext();
    }
}
