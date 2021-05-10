package com.labs.jwvansale16.mdui.checkoutmaterialset;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.labs.jwvansale16.R;
import com.labs.jwvansale16.databinding.FragmentCheckoutmaterialsetCreateBinding;
import com.labs.jwvansale16.mdui.BundleKeys;
import com.labs.jwvansale16.mdui.InterfacedFragment;
import com.labs.jwvansale16.mdui.UIConstants;
import com.labs.jwvansale16.repository.OperationResult;
import com.labs.jwvansale16.viewmodel.checkoutmaterial.CheckoutMaterialViewModel;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CheckoutMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntityTypes;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.mobile.fiori.formcell.SimplePropertyFormCell;
import com.sap.cloud.mobile.odata.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fragment that presents a screen to either create or update an existing CheckoutMaterial entity.
 * This fragment is contained in the {@link CheckoutMaterialSetActivity}.
 */
public class CheckoutMaterialSetCreateFragment extends InterfacedFragment<CheckoutMaterial> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutMaterialSetCreateFragment.class);
    //The key for the saved instance of the working entity for device configuration change
    private static final String KEY_WORKING_COPY = "WORKING_COPY";

    /** CheckoutMaterial object and it's copy: the modifications are done on the copied object. */
    private CheckoutMaterial checkoutMaterialEntity;
    private CheckoutMaterial checkoutMaterialEntityCopy;

    /** DataBinding generated class */
    private FragmentCheckoutmaterialsetCreateBinding binding;

    /** Indicate what operation to be performed */
    private String operation;

    /** CheckoutMaterial ViewModel */
    private CheckoutMaterialViewModel viewModel;

    /** The update menu item */
    private MenuItem updateMenuItem;

    /**
     * This fragment is used for both update and create for CheckoutMaterialSet to enter values for the properties.
     * When used for update, an instance of the entity is required. In the case of create, a new instance
     * of the entity with defaults will be created. The default values may not be acceptable for the
     * OData service.
     * Arguments: Operation: [OP_CREATE | OP_UPDATE]
     *            CheckoutMaterial if Operation is update
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = R.menu.itemlist_edit_options;
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        operation = bundle.getString(BundleKeys.OPERATION);
        if (UIConstants.OP_CREATE.equals(operation)) {
            activityTitle = currentActivity.getResources().getString(R.string.title_create_fragment, EntityTypes.checkoutMaterial.getLocalName());
        } else {
            activityTitle = currentActivity.getResources().getString(R.string.title_update_fragment) + " " + EntityTypes.checkoutMaterial.getLocalName();
        }

        ((CheckoutMaterialSetActivity)currentActivity).isNavigationDisabled = true;
        viewModel = new ViewModelProvider(currentActivity).get(CheckoutMaterialViewModel.class);
        viewModel.getCreateResult().observe(this, result -> onComplete(result));
        viewModel.getUpdateResult().observe(this, result -> onComplete(result));

        if(UIConstants.OP_CREATE.equals(operation)) {
            checkoutMaterialEntity = createCheckoutMaterial();
        } else {
            checkoutMaterialEntity = viewModel.getSelectedEntity().getValue();
        }

        CheckoutMaterial workingCopy = null;
        if( savedInstanceState != null ) {
            workingCopy =  (CheckoutMaterial)savedInstanceState.getParcelable(KEY_WORKING_COPY);
        }
        if( workingCopy == null ) {
            checkoutMaterialEntityCopy = (CheckoutMaterial) checkoutMaterialEntity.copy();
            checkoutMaterialEntityCopy.setEntityTag(checkoutMaterialEntity.getEntityTag());
            checkoutMaterialEntityCopy.setOldEntity(checkoutMaterialEntity);
            checkoutMaterialEntityCopy.setEditLink((checkoutMaterialEntity.getEditLink()));
        } else {
            //in this case, the old entity and entity tag should already been set.
            checkoutMaterialEntityCopy = workingCopy;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ObjectHeader objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if( objectHeader != null ) objectHeader.setVisibility(View.GONE);
        return setupDataBinding(inflater, container);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_WORKING_COPY, checkoutMaterialEntityCopy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(secondaryToolbar != null) {
            secondaryToolbar.setTitle(activityTitle);
        } else {
            getActivity().setTitle(activityTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                updateMenuItem = item;
                enableUpdateMenuItem(false);
                return onSaveItem();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** 
     * Enables or disables the update menu item base on the given 'enable'
     * @param enable true to enable the menu item, false otherwise
     */
    private void enableUpdateMenuItem(boolean enable) {
        updateMenuItem.setEnabled(enable);
        updateMenuItem.getIcon().setAlpha( enable ? 255 : 130);
    }

    /**
     * Saves the entity
     */
    private boolean onSaveItem() {
        if (!isCheckoutMaterialValid()) {
            return false;
        }
        //set 'isNavigationDisabled' false here to make sure the logic in list is ok, and set it to true if update fails.
        ((CheckoutMaterialSetActivity)currentActivity).isNavigationDisabled = false;
        if( progressBar != null ) progressBar.setVisibility(View.VISIBLE);
        if (operation.equals(UIConstants.OP_CREATE)) {
            viewModel.create(checkoutMaterialEntityCopy);
        } else {
            viewModel.update(checkoutMaterialEntityCopy);
        }
        return true;
    }

    /**
     * Create a new CheckoutMaterial instance and initialize properties to its default values
     * Nullable property will remain null
     * For offline, keys will be unset to avoid collision should more than one is created locally
     * @return new CheckoutMaterial instance
     */
    private CheckoutMaterial createCheckoutMaterial() {
        CheckoutMaterial checkoutMaterialEntity = new CheckoutMaterial(true);
        checkoutMaterialEntity.unsetDataValue(CheckoutMaterial.userid);
        checkoutMaterialEntity.unsetDataValue(CheckoutMaterial.driver);
        checkoutMaterialEntity.unsetDataValue(CheckoutMaterial.customerno);
        checkoutMaterialEntity.unsetDataValue(CheckoutMaterial.type_);
        checkoutMaterialEntity.unsetDataValue(CheckoutMaterial.material);
        return checkoutMaterialEntity;
    }

    /** Callback function to complete processing when updateResult or createResult events fired */
    private void onComplete(@NonNull OperationResult<CheckoutMaterial> result) {
        if( progressBar != null ) progressBar.setVisibility(View.INVISIBLE);
        enableUpdateMenuItem(true);
        if (result.getError() != null) {
            ((CheckoutMaterialSetActivity)currentActivity).isNavigationDisabled = true;
            handleError(result);
        } else {
            boolean isMasterDetail = currentActivity.getResources().getBoolean(R.bool.two_pane);
            if( UIConstants.OP_UPDATE.equals(operation) && !isMasterDetail) {
                viewModel.setSelectedEntity(checkoutMaterialEntityCopy);
            }
            if( isMasterDetail ) {
                CheckoutMaterialSetListFragment listFragment = (CheckoutMaterialSetListFragment) currentActivity.getSupportFragmentManager().findFragmentByTag(UIConstants.LIST_FRAGMENT_TAG);
                listFragment.refreshListData();
            }
            currentActivity.onBackPressed();
        }
    }

    /** Simple validation: checks the presence of mandatory fields. */
    private boolean isValidProperty(@NonNull Property property, @NonNull String value) {
        boolean isValid = true;
        if (!property.isNullable() && value.isEmpty()) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Set up data binding for this view
     * @param inflater - layout inflater from onCreateView
     * @param container - view group from onCreateView
     * @return view - rootView from generated data binding code
     */
    private View setupDataBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        binding = FragmentCheckoutmaterialsetCreateBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.setCheckoutMaterial(checkoutMaterialEntityCopy);
        return rootView;
    }

    /** Validate the edited inputs */
    private boolean isCheckoutMaterialValid() {
        LinearLayout linearLayout = getView().findViewById(R.id.create_update_checkoutmaterial);
        boolean isValid = true;
        // validate properties i.e. check non-nullable properties are truly non-null
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View viewItem = linearLayout.getChildAt(i);
            SimplePropertyFormCell simplePropertyFormCell = (SimplePropertyFormCell)viewItem;
            String propertyName = (String) simplePropertyFormCell.getTag();
            Property property = EntityTypes.checkoutMaterial.getProperty(propertyName);
            String value = simplePropertyFormCell.getValue().toString();
            if (!isValidProperty(property, value)) {
                simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, true);
                String errorMessage = getResources().getString(R.string.mandatory_warning);
                simplePropertyFormCell.setErrorEnabled(true);
                simplePropertyFormCell.setError(errorMessage);
                isValid = false;
            }
            else {
                if (simplePropertyFormCell.isErrorEnabled()){
                    boolean hasMandatoryError = (Boolean)simplePropertyFormCell.getTag(R.id.TAG_HAS_MANDATORY_ERROR);
                    if (!hasMandatoryError) {
                        isValid = false;
                    } else {
                        simplePropertyFormCell.setErrorEnabled(false);
                    }
                }
                simplePropertyFormCell.setTag(R.id.TAG_HAS_MANDATORY_ERROR, false);
            }
        }
        return isValid;
    }

    /**
     * Notify user of error encountered while execution the operation
     * @param result - operation result with error
     */
    private void handleError(@NonNull OperationResult<CheckoutMaterial> result) {
        String errorMessage;
        switch (result.getOperation()) {
            case UPDATE:
                errorMessage = getResources().getString(R.string.update_failed_detail);
                break;
            case CREATE:
                errorMessage = getResources().getString(R.string.create_failed_detail);
                break;
            default:
                throw new AssertionError();
        }
        showError(errorMessage);
    }
}
