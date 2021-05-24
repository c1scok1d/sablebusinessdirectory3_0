package com.macinternetservices.sablebusinessdirectory.ui.contactus;

import com.macinternetservices.sablebusinessdirectory.repository.contactus.ContactUsRepository;
import com.macinternetservices.sablebusinessdirectory.ui.common.BackgroundTaskHandler;

/**
 * Created by MAC Internet Services on 05/21/2021.
 * Contact Email : admin@thesablebusinessdirectory.com
 * Website : https://www.sablebusinessdirectory.com
 */

public class ContactUsBackgroundTaskHandler extends BackgroundTaskHandler{

    private final ContactUsRepository repository;

    public ContactUsBackgroundTaskHandler(ContactUsRepository repository) {
        super();
        this.repository = repository;
    }


    public void postContactUs(String apiKey, String contactName, String contactEmail, String contactDesc, String contactPhone) {

        unregister();

        holdLiveData = repository.postContactUs(apiKey, contactName, contactEmail, contactDesc, contactPhone);
        loadingState.setValue(new LoadingState(true, null));

        //noinspection ConstantConditions
        holdLiveData.observeForever(this);
    }
}
