package com.labs.jwvansale16.mdui.visitdetailsset;

import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.labs.jwvansale16.R;
import com.labs.jwvansale16.databinding.FragmentVisitdetailssetDetailBinding;
import com.labs.jwvansale16.mdui.BundleKeys;
import com.labs.jwvansale16.mdui.InterfacedFragment;
import com.labs.jwvansale16.mdui.JasmineCreateCreditOrderActivity;
import com.labs.jwvansale16.mdui.*;
import com.labs.jwvansale16.mdui.UIConstants;
import com.labs.jwvansale16.mdui.EntityKeyUtil;
import com.labs.jwvansale16.repository.OperationResult;
import com.labs.jwvansale16.viewmodel.visitdetails.VisitDetailsViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitDetails;
import com.sap.cloud.mobile.fiori.object.ObjectHeader;
import com.sap.cloud.mobile.odata.DataValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fragment representing a single VisitDetails detail screen.
 * This fragment is contained in an VisitDetailsSetActivity.
 */
public class VisitDetailsSetDetailFragment extends InterfacedFragment<VisitDetails> {

//    /** Generated data binding class based on layout file */
//    private FragmentVisitdetailssetDetailBinding binding;
//
//    /** VisitDetails entity to be displayed */
//    private VisitDetails visitDetailsEntity = null;
//
//    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
//    private ObjectHeader objectHeader;
//
//    /** View model of the entity type that the displayed entity belongs to */
//    private VisitDetailsViewModel viewModel;
//
//    /** Arguments: VisitDetails for display */
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        menu = R.menu.itemlist_view_options;
//        setHasOptionsMenu(true);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        return setupDataBinding(inflater, container);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        viewModel = new ViewModelProvider(currentActivity).get(VisitDetailsViewModel.class);
//        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), this::onDeleteComplete);
//        viewModel.getSelectedEntity().observe(getViewLifecycleOwner(), entity -> {
//            visitDetailsEntity = entity;
//            binding.setVisitDetails(entity);
//            setupObjectHeader();
//        });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.update_item:
//                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, visitDetailsEntity);
//                return true;
//            case R.id.delete_item:
//                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//
//    /** Completion callback for delete operation */
//    private void onDeleteComplete(@NonNull OperationResult<VisitDetails> result) {
//        if( progressBar != null ) {
//            progressBar.setVisibility(View.INVISIBLE);
//        }
//        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
//        Exception ex = result.getError();
//        if (ex != null) {
//            showError(getString(R.string.delete_failed_detail));
//            return;
//        }
//        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, visitDetailsEntity);
//    }
//
//    /**
//     * Set detail image of ObjectHeader.
//     * When the entity does not provides picture, set the first character of the masterProperty.
//     */
//    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull VisitDetails visitDetailsEntity) {
//        if (visitDetailsEntity.getDataValue(VisitDetails.userid) != null && !visitDetailsEntity.getDataValue(VisitDetails.userid).toString().isEmpty()) {
//            objectHeader.setDetailImageCharacter(visitDetailsEntity.getDataValue(VisitDetails.userid).toString().substring(0, 1));
//        } else {
//            objectHeader.setDetailImageCharacter("?");
//        }
//    }
//
//    /**
//     * Setup ObjectHeader with an instance of VisitDetails
//     */
//    private void setupObjectHeader() {
//        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
//        if (secondToolbar != null) {
//            secondToolbar.setTitle(visitDetailsEntity.getEntityType().getLocalName());
//        } else {
//            currentActivity.setTitle(visitDetailsEntity.getEntityType().getLocalName());
//        }
//
//        // Object Header is not available in tablet mode
//        objectHeader = currentActivity.findViewById(R.id.objectHeader);
//        if (objectHeader != null) {
//            // Use of getDataValue() avoids the knowledge of what data type the master property is.
//            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
//            // get<Property>() method and add code to convert to string
//            DataValue dataValue = visitDetailsEntity.getDataValue(VisitDetails.userid);
//            if (dataValue != null) {
//                objectHeader.setHeadline(dataValue.toString());
//            } else {
//                objectHeader.setHeadline(null);
//            }
//            // EntityKey in string format: '{"key":value,"key2":value2}'
//            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(visitDetailsEntity));
//            objectHeader.setTag("#tag1", 0);
//            objectHeader.setTag("#tag3", 2);
//            objectHeader.setTag("#tag2", 1);
//
//            objectHeader.setBody("You can set the header body text here.");
//            objectHeader.setFootnote("You can set the header footnote here.");
//            objectHeader.setDescription("You can add a detailed item description here.");
//
//            setDetailImage(objectHeader, visitDetailsEntity);
//            objectHeader.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * Set up databinding for this view
//     *
//     * @param inflater - layout inflater from onCreateView
//     * @param container - view group from onCreateView
//     * @return view - rootView from generated databinding code
//     */
//    private View setupDataBinding(LayoutInflater inflater, ViewGroup container) {
//        binding = FragmentVisitdetailssetDetailBinding.inflate(inflater, container, false);
//        View rootView = binding.getRoot();
//        binding.setHandler(this);
//        return rootView;
//    }

    private static final int CREATE_CREDIT_ORDER_ITEM = 200;
    private static final int CREATE_CASH_ORDER_ITEM = 300;
    private static final int CREATE_PRE_SOLD_ORDER_ITEM = 400;
    private static final int CREATE_RETURN_ORDER_ITEM = 500;
    private static final int CREATE_COLLECTION_ITEM = 600;

    /** Generated data binding class based on layout file */
    private FragmentVisitdetailssetDetailBinding binding;

    /** VisitDetails entity to be displayed */
    private VisitDetails visitDetailsEntity = null;

    /** Fiori ObjectHeader component used when entity is to be displayed on phone */
    private ObjectHeader objectHeader;

    /** View model of the entity type that the displayed entity belongs to */
    private VisitDetailsViewModel viewModel;

    /** Arguments: VisitDetails for display */

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitDetailsSetDetailFragment.class);
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = R.menu.itemlist_view_options_jasmine;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,CREATE_CREDIT_ORDER_ITEM,0,R.string.menu_item_create_credit_order);
        menu.add(0,CREATE_CASH_ORDER_ITEM,1,R.string.menu_item_create_cash_order);
        menu.add(0,CREATE_PRE_SOLD_ORDER_ITEM,2,R.string.menu_item_pre_sold_order);
        menu.add(0,CREATE_RETURN_ORDER_ITEM,3,R.string.menu_item_return_order);
        menu.add(0,CREATE_COLLECTION_ITEM,4,R.string.menu_item_collection);
//        inflater.inflate(this.menu,menu);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu) {
////        menu.add(0,CREATE_CREDIT_ORDER_ITEM,0,R.string.menu_item_create_credit_order);
////        menu.add(0,CREATE_CASH_ORDER_ITEM,1,R.string.menu_item_create_cash_order);
////        menu.add(0,CREATE_PRE_SOLD_ORDER_ITEM,2,R.string.menu_item_pre_sold_order);
////        menu.add(0,CREATE_RETURN_ORDER_ITEM,3,R.string.menu_item_return_order);
////        menu.add(0,CREATE_COLLECTION_ITEM,4,R.string.menu_item_collection);
//
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return setupDataBinding(inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(currentActivity).get(VisitDetailsViewModel.class);
        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), this::onDeleteComplete);
        viewModel.getSelectedEntity().observe(getViewLifecycleOwner(), entity -> {
            visitDetailsEntity = entity;
            binding.setVisitDetails(entity);
            setupObjectHeader();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.debug("onOptionsIteSelected"+item.getTitle());
        Intent intent;
        switch (item.getItemId()) {
            case CREATE_CREDIT_ORDER_ITEM:
                LOGGER.debug("create credit order screen menu item selected");

                intent = new Intent(getActivity(), JasmineCreateCreditOrderActivity.class);
                this.startActivityForResult(intent,CREATE_CREDIT_ORDER_ITEM);
                return true;

            case CREATE_CASH_ORDER_ITEM:
                LOGGER.debug("create cash order screen menu item selected");

                intent = new Intent(getActivity(), JasmineCreateCashOrderActivity.class);
                this.startActivityForResult(intent,CREATE_CREDIT_ORDER_ITEM);
                return true;

            case CREATE_PRE_SOLD_ORDER_ITEM:
                LOGGER.debug("create pre-sold order screen menu item selected");

                intent = new Intent(getActivity(), JasmineCreateCashOrderActivity.class);
                this.startActivityForResult(intent,CREATE_CREDIT_ORDER_ITEM);
                return true;

            case CREATE_RETURN_ORDER_ITEM:
                LOGGER.debug("create return order screen menu item selected");

                intent = new Intent(getActivity(), JasmineCreateCashOrderActivity.class);
                this.startActivityForResult(intent,CREATE_CREDIT_ORDER_ITEM);
                return true;

            case CREATE_COLLECTION_ITEM:
                LOGGER.debug("create collection screen menu item selected");

                intent = new Intent(getActivity(), JasmineCreateCashOrderActivity.class);
                this.startActivityForResult(intent,CREATE_CREDIT_ORDER_ITEM);
                return true;

//            case R.id.update_item:
//                listener.onFragmentStateChange(UIConstants.EVENT_EDIT_ITEM, visitDetailsEntity);
//                return true;
//            case R.id.delete_item:
//                listener.onFragmentStateChange(UIConstants.EVENT_ASK_DELETE_CONFIRMATION,null);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Completion callback for delete operation */
    private void onDeleteComplete(@NonNull OperationResult<VisitDetails> result) {
        if( progressBar != null ) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        viewModel.removeAllSelected(); //to make sure the 'action mode' not activated in the list
        Exception ex = result.getError();
        if (ex != null) {
            showError(getString(R.string.delete_failed_detail));
            return;
        }
        listener.onFragmentStateChange(UIConstants.EVENT_DELETION_COMPLETED, visitDetailsEntity);
    }

    /**
     * Set detail image of ObjectHeader.
     * When the entity does not provides picture, set the first character of the masterProperty.
     */
    private void setDetailImage(@NonNull ObjectHeader objectHeader, @NonNull VisitDetails visitDetailsEntity) {
        if (visitDetailsEntity.getDataValue(VisitDetails.userid) != null && !visitDetailsEntity.getDataValue(VisitDetails.userid).toString().isEmpty()) {
            objectHeader.setDetailImageCharacter(visitDetailsEntity.getDataValue(VisitDetails.userid).toString().substring(0, 1));
        } else {
            objectHeader.setDetailImageCharacter("?");
        }
    }

    /**
     * Setup ObjectHeader with an instance of VisitDetails
     */
    private void setupObjectHeader() {
        Toolbar secondToolbar = currentActivity.findViewById(R.id.secondaryToolbar);
        if (secondToolbar != null) {
            secondToolbar.setTitle(visitDetailsEntity.getEntityType().getLocalName());
        } else {
            currentActivity.setTitle(visitDetailsEntity.getEntityType().getLocalName());
        }

        // Object Header is not available in tablet mode
        objectHeader = currentActivity.findViewById(R.id.objectHeader);
        if (objectHeader != null) {
            // Use of getDataValue() avoids the knowledge of what data type the master property is.
            // This is a convenience for wizard generated code. Normally, developer will use the proxy class
            // get<Property>() method and add code to convert to string
            DataValue dataValue = visitDetailsEntity.getDataValue(VisitDetails.userid);
            if (dataValue != null) {
                objectHeader.setHeadline(dataValue.toString());
            } else {
                objectHeader.setHeadline(null);
            }
            // EntityKey in string format: '{"key":value,"key2":value2}'
            objectHeader.setSubheadline(EntityKeyUtil.getOptionalEntityKey(visitDetailsEntity));
            objectHeader.setTag("#tag1", 0);
            objectHeader.setTag("#tag3", 2);
            objectHeader.setTag("#tag2", 1);

            objectHeader.setBody("You can set the header body text here.");
            objectHeader.setFootnote("You can set the header footnote here.");
            objectHeader.setDescription("You can add a detailed item description here.");

            setDetailImage(objectHeader, visitDetailsEntity);
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
        binding = FragmentVisitdetailssetDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.setHandler(this);
        return rootView;
    }

}
