package com.labs.jwvansale16.viewmodel.cancelvisit;

import android.app.Application;
import android.os.Parcelable;

import com.labs.jwvansale16.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CancelVisit;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;

/*
 * Represents View model for CancelVisit
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class CancelVisitViewModel extends EntityViewModel<CancelVisit> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public CancelVisitViewModel(Application application) {
        super(application, EntitySets.cancelVisitSet, CancelVisit.driverid);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public CancelVisitViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.cancelVisitSet, CancelVisit.driverid, navigationPropertyName, entityData);
    }
}
