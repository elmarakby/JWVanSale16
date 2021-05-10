package com.labs.jwvansale16.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.os.Parcelable;

import com.labs.jwvansale16.viewmodel.actioncancelvisit.ActionCancelVisitViewModel;
import com.labs.jwvansale16.viewmodel.actioncheckinmaterial.ActionCheckInMaterialViewModel;
import com.labs.jwvansale16.viewmodel.actionvisitoperations.ActionVisitOperationsViewModel;
import com.labs.jwvansale16.viewmodel.cancelvisit.CancelVisitViewModel;
import com.labs.jwvansale16.viewmodel.checkinmaterial.CheckInMaterialViewModel;
import com.labs.jwvansale16.viewmodel.checkoutmaterial.CheckoutMaterialViewModel;
import com.labs.jwvansale16.viewmodel.collections.CollectionsViewModel;
import com.labs.jwvansale16.viewmodel.customervisitplan.CustomerVisitPlanViewModel;
import com.labs.jwvansale16.viewmodel.driverdetails.DriverDetailsViewModel;
import com.labs.jwvansale16.viewmodel.freematerial.FreeMaterialViewModel;
import com.labs.jwvansale16.viewmodel.mastermaterial.MasterMaterialViewModel;
import com.labs.jwvansale16.viewmodel.openbilling.OpenBillingViewModel;
import com.labs.jwvansale16.viewmodel.pricesa005.PricesA005ViewModel;
import com.labs.jwvansale16.viewmodel.pricesa904.PricesA904ViewModel;
import com.labs.jwvansale16.viewmodel.pricesa905.PricesA905ViewModel;
import com.labs.jwvansale16.viewmodel.pricesa906.PricesA906ViewModel;
import com.labs.jwvansale16.viewmodel.pricesa908.PricesA908ViewModel;
import com.labs.jwvansale16.viewmodel.priceskonp.PricesKonpViewModel;
import com.labs.jwvansale16.viewmodel.totalmaterial.TotalMaterialViewModel;
import com.labs.jwvansale16.viewmodel.visitdetails.VisitDetailsViewModel;
import com.labs.jwvansale16.viewmodel.visitoperationsheader.VisitOperationsHeaderViewModel;
import com.labs.jwvansale16.viewmodel.visitoperationsitems.VisitOperationsItemsViewModel;


/**
 * Custom factory class, which can create view models for entity subsets, which are
 * reached from a parent entity through a navigation property.
 */
public class EntityViewModelFactory implements ViewModelProvider.Factory {

	// application class
    private Application application;
	// name of the navigation property
    private String navigationPropertyName;
	// parent entity
    private Parcelable entityData;

	/**
	 * Creates a factory class for entity view models created following a navigation link.
	 *
	 * @param application parent application
	 * @param navigationPropertyName name of the navigation link
	 * @param entityData parent entity
	 */
    public EntityViewModelFactory(Application application, String navigationPropertyName, Parcelable entityData) {
        this.application = application;
        this.navigationPropertyName = navigationPropertyName;
        this.entityData = entityData;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T retValue = null;
		switch(modelClass.getSimpleName()) {



			case "ActionCancelVisitViewModel":
				retValue = (T) new ActionCancelVisitViewModel(application, navigationPropertyName, entityData);
				break;
			case "ActionCheckInMaterialViewModel":
				retValue = (T) new ActionCheckInMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "ActionVisitOperationsViewModel":
				retValue = (T) new ActionVisitOperationsViewModel(application, navigationPropertyName, entityData);
				break;
			case "CancelVisitViewModel":
				retValue = (T) new CancelVisitViewModel(application, navigationPropertyName, entityData);
				break;
			case "CheckInMaterialViewModel":
				retValue = (T) new CheckInMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "CheckoutMaterialViewModel":
				retValue = (T) new CheckoutMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "CollectionsViewModel":
				retValue = (T) new CollectionsViewModel(application, navigationPropertyName, entityData);
				break;
			case "CustomerVisitPlanViewModel":
				retValue = (T) new CustomerVisitPlanViewModel(application, navigationPropertyName, entityData);
				break;
			case "DriverDetailsViewModel":
				retValue = (T) new DriverDetailsViewModel(application, navigationPropertyName, entityData);
				break;
			case "FreeMaterialViewModel":
				retValue = (T) new FreeMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "MasterMaterialViewModel":
				retValue = (T) new MasterMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "OpenBillingViewModel":
				retValue = (T) new OpenBillingViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesA005ViewModel":
				retValue = (T) new PricesA005ViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesA904ViewModel":
				retValue = (T) new PricesA904ViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesA905ViewModel":
				retValue = (T) new PricesA905ViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesA906ViewModel":
				retValue = (T) new PricesA906ViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesA908ViewModel":
				retValue = (T) new PricesA908ViewModel(application, navigationPropertyName, entityData);
				break;
			case "PricesKonpViewModel":
				retValue = (T) new PricesKonpViewModel(application, navigationPropertyName, entityData);
				break;
			case "TotalMaterialViewModel":
				retValue = (T) new TotalMaterialViewModel(application, navigationPropertyName, entityData);
				break;
			case "VisitDetailsViewModel":
				retValue = (T) new VisitDetailsViewModel(application, navigationPropertyName, entityData);
				break;
			case "VisitOperationsHeaderViewModel":
				retValue = (T) new VisitOperationsHeaderViewModel(application, navigationPropertyName, entityData);
				break;
			case "VisitOperationsItemsViewModel":
				retValue = (T) new VisitOperationsItemsViewModel(application, navigationPropertyName, entityData);
				break;
		}
		return retValue;
	}
}