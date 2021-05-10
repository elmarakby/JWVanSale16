package com.labs.jwvansale16.viewmodel.pricesa904;

import android.app.Application;
import android.os.Parcelable;

import com.labs.jwvansale16.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA904;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;

/*
 * Represents View model for PricesA904
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class PricesA904ViewModel extends EntityViewModel<PricesA904> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public PricesA904ViewModel(Application application) {
        super(application, EntitySets.pricesA904Set, PricesA904.kappl);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public PricesA904ViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.pricesA904Set, PricesA904.kappl, navigationPropertyName, entityData);
    }
}
