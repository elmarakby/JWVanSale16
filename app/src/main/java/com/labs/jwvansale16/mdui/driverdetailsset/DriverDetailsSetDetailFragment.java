package com.labs.jwvansale16.mdui.driverdetailsset;

import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.labs.jwvansale16.R;
import com.labs.jwvansale16.databinding.FragmentDriverdetailssetDetailBinding;
import com.labs.jwvansale16.mdui.BundleKeys;
import com.labs.jwvansale16.mdui.InterfacedFragment;
import com.labs.jwvansale16.mdui.UIConstants;
import com.labs.jwvansale16.mdui.EntityKeyUtil;
import com.labs.jwvansale16.repository.OperationResult;
import com.labs.jwvansale16.viewmodel.driverdetails.DriverDetailsViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.DriverDetails;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.mobile.odata.DataValue;
import com.labs.jwvansale16.mdui.visitdetailsset.VisitDetailsSetActivity;
import com.labs.jwvansale16.mdui.checkoutmaterialset.CheckoutMaterialSetActivity;
import com.labs.jwvansale16.mdui.customervisitplanset.CustomerVisitPlanSetActivity;

/**
 * A fragment representing a single DriverDetails detail screen.
 * This fragment is contained in an DriverDetailsSetActivity.
 */
public class DriverDetailsSetDetailFragment extends InterfacedFragment<DriverDetails> {

    /** Generated data binding class based on layout file */
    private FragmentDriverdetailssetDetailBinding binding;

    /** DriverDetails entity to be displayed */
    private DriverDetails driverDetailsEntity = null;

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private ObjectHeader objectHeader;

    /** View model of the entity type that the displayed entity belongs to */
    private DriverDetailsViewModel viewModel;

    /** Arguments: DriverDetails for display */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = R.menu.itemlist_view_options;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return setupDataBinding(inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(currentActivity).get(DriverDetailsViewModel.class);
        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), this::onDeleteComplete);
        viewModel.getSelectedEntity().observe(getViewLifecycleOwner(), entity -> {
            driverDetailsEntity = entity;
            binding.setDriverDetails(entity);
            setupObjectHeader();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_item:
                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, driverDetailsEntity);
                return true;
            case R.id.delete_item:
                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNavigationClickedToVisitDetailsSet_VisitDetailsSet(View v) {
        Intent intent = new Intent(this.currentActivity, VisitDetailsSetActivity.class);
        intent.putExtra("parent", driverDetailsEntity);
        intent.putExtra("navigation", "VisitDetailsSet");
        startActivity(intent);
    }

    public void onNavigationClickedToCheckoutMaterialSet_CheckoutMaterialSet(View v) {
        Intent intent = new Intent(this.currentActivity, CheckoutMaterialSetActivity.class);
        intent.putExtra("parent", driverDetailsEntity);
        intent.putExtra("navigation", "CheckoutMaterialSet");
        startActivity(intent);
    }

    public void onNavigationClickedToCustomerVisitPlanSet_Customer_VisitPlanSet(View v) {
        Intent intent = new Intent(this.currentActivity, CustomerVisitPlanSetActivity.class);
        intent.putExtra("parent", driverDetailsEntity);
        intent.putExtra("navigation", "Customer_VisitPlanSet");
        startActivity(intent);
    }


    /** Completion callback for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<DriverDetails> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
        Exception ex = result.getError();
        if (ex != null) {
            showError(getString(R.string.delete_failed_detail));
            return;
        }
        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, driverDetailsEntity);
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull DriverDetails driverDetailsEntity) {
        if (driverDetailsEntity.getDataValue(DriverDetails.userid) != null && !driverDetailsEntity.getDataValue(DriverDetails.userid).toString().isEmpty()) {
            objectHeader.setDetailImageCharacter(driverDetailsEntity.getDataValue(DriverDetails.userid).toString().substring(0, 1));
        } else {
            objectHeader.setDetailImageCharacter("?");
        }
    }

    /**
     * Setup ObjectHeader with an instance of DriverDetails
     */
    private void setupObjectHeader() {
        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
        if (secondToolbar != null) {
            secondToolbar.setTitle(driverDetailsEntity.getEntityType().getLocalName());
        } else {
            currentActivity.setTitle(driverDetailsEntity.getEntityType().getLocalName());
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if (objectHeader != null) {
            // Use of getDataValue() avoids the knowledge of what data type the master property is.
            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
            // get<Property>() method and add code to convert to string
            DataValue dataValue = driverDetailsEntity.getDataValue(DriverDetails.userid);
            if (dataValue != null) {
                objectHeader.setHeadline(dataValue.toString());
            } else {
                objectHeader.setHeadline(null);
            }
            // EntityKey in string format: '{"key":value,"key2":value2}'
            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(driverDetailsEntity));
            objectHeader.setTag("#tag1", 0);
            objectHeader.setTag("#tag3", 2);
            objectHeader.setTag("#tag2", 1);

            objectHeader.setBody("You can set the header body text here.");
            objectHeader.setFootnote("You can set the header footnote here.");
            objectHeader.setDescription("You can add a detailed item description here.");

            setDetailImage(objectHeader, driverDetailsEntity);
            objectHeader.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set up databinding for this view
     *
     * @param inflater - layout inflater from onCreateView
     * @param container - view group from onCreateView
     * @return view - rootView from generated databinding code
     */
    private View setupDataBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDriverdetailssetDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.setHandler(this);
        return rootView;
    }
}
