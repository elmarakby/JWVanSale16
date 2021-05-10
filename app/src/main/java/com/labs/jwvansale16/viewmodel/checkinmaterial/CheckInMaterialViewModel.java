package com.labs.jwvansale16.viewmodel.checkinmaterial;

import android.app.Application;
import android.os.Parcelable;

import com.labs.jwvansale16.viewmodel.EntityViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CheckInMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;

/*
 * Represents View model for CheckInMaterial
 * Having an entity view model for each <T> allows the ViewModelProvider to cache and
 * return the view model of that type. This is because the ViewModelStore of
 * ViewModelProvider cannot not be able to tell the difference between EntityViewModel<type1>
 * and EntityViewModel<type2>.
 */
public class CheckInMaterialViewModel extends EntityViewModel<CheckInMaterial> {

    /**
    * Default constructor for a specific view model.
    * @param application - parent application
    */
    public CheckInMaterialViewModel(Application application) {
        super(application, EntitySets.checkInMaterialSet, CheckInMaterial.driverid);
    }

    /**
    * Constructor for a specific view model with navigation data.
    * @param application - parent application
    * @param navigationPropertyName - name of the navigation property
    * @param entityData - parent entity (starting point of the navigation)
    */
	 public CheckInMaterialViewModel(Application application, String navigationPropertyName, Parcelable entityData) {
        super(application, EntitySets.checkInMaterialSet, CheckInMaterial.driverid, navigationPropertyName, entityData);
    }
}
