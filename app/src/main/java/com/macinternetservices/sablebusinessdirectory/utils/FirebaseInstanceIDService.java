package com.macinternetservices.sablebusinessdirectory.utils;

import android.content.Intent;

/**
 * Created by MAC Internet Services on 8/11/16.
 * Contact Email : admin@thesablebusinessdirectory.com
 */
public class FirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {

        super.onNewToken(token);
        Utils.psLog("token : " + token);

        Intent in = new Intent();
        in.putExtra("message",token);
        in.setAction("NOW");

    }

}
