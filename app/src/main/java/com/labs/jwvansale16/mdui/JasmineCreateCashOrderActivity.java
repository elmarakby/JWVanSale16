package com.labs.jwvansale16.mdui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.labs.jwvansale16.R;
import com.labs.jwvansale16.app.SAPWizardApplication;
import com.labs.jwvansale16.customized.Globals;
import com.labs.jwvansale16.customized.models.JasmineOrderItemsInnerModel;
import com.labs.jwvansale16.service.*;
import com.labs.jwvansale16.viewmodel.totalmaterial.TotalMaterialViewModel;
import com.labs.jwvansale16.viewmodel.visitoperationsheader.VisitOperationsHeaderViewModel;
import com.labs.jwvansale16.viewmodel.visitoperationsitems.VisitOperationsItemsViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.TotalMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitOperationsHeader;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitOperationsItems;
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar;
import com.sap.cloud.mobile.flowv2.core.DialogHelper;
import com.sap.cloud.mobile.foundation.usage.UsageService;
import com.sap.cloud.mobile.odata.core.Action0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JasmineCreateCashOrderActivity extends AppCompatActivity {

    /*
     * Android Bound Service to handle offline synchronization operations. Service runs in foreground mode to maximize
     * resiliency.
     */
    private OfflineSyncWorker syncService;

    /*
     * Flag to indicate that current acvtity is bound to the Offline Sync Service
     */
    boolean isBound = false;

    /*
     * Fiori progress bar for busy indication if either update or delete action is clicked upon
     */
    //private FioriProgressBar progressBar;

    /*
     * Service connection object callbacks when service is bound or lost
     */
    private ServiceConnection serviceConnection;

    private OfflineWorkerUtil sapServiceManager;

    private static final int SETTINGS_SCREEN_ITEM = 200;
    private static final int SYNC_ACTION_ITEM = 300;
    private static final Logger LOGGER = LoggerFactory.getLogger(JasmineCreateCashOrderActivity.class);

    /**
     * View model of the entity type
     */
    private TotalMaterialViewModel viewModel; //CustomerMaterialViewModel
    private List<TotalMaterial> totalMaterialSet;

    private OrderItemsAdapter order_adapter;
    private ListView mList_OrderItems;
    private ArrayList<JasmineOrderItemsInnerModel> m_orderItemsList;
    private ArrayList<JasmineOrderItemsInnerModel> m_orderItemsList_out;
    private JasmineOrderItemsInnerModel m_orderItemModel;
    /**
     * List adapter to be used with RecyclerView containing all instances of customerMaterialSet
     */
    private VisitOperationsHeaderViewModel viewModel_OrderHeader; //TourHeaderViewModel
    private VisitOperationsItemsViewModel viewModel_OrderItem;

    private VisitOperationsHeader tourHeader;
    private VisitOperationsItems tourItem;

    private String Exdocument;

    private String currentDate;
    private String currentTime;
    private VisitOperationsItems tourItem1;

    /* Fiori progress bar for busy indication if either update or delete action is clicked upon */
    private FioriProgressBar progressBar = null;
    private final int totalNumberOfSteps = 40;
    private int startPointForSync = 0;
    private int previousStep = 0;
    private int currentStepNumber = 0;
    private MenuItem syncItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jasmine_create_cash_order);

        //sapServiceManager = ((SAPWizardApplication) getApplication()).getSAPServiceManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Cash Order");
        setSupportActionBar(toolbar);
        UsageService.getInstance().eventBehaviorViewDisplayed(JasmineCreateCashOrderActivity.class.getSimpleName(),
                "elementId", "onCreate", "called");
        mList_OrderItems = findViewById(R.id.list_OrderItems);
        m_orderItemsList = new ArrayList<JasmineOrderItemsInnerModel>();
        m_orderItemsList_out = new ArrayList<JasmineOrderItemsInnerModel>();
        order_adapter = new OrderItemsAdapter(m_orderItemsList);
        mList_OrderItems.setAdapter(order_adapter);
        m_orderItemModel = new JasmineOrderItemsInnerModel();

        tourHeader = new VisitOperationsHeader();
        tourItem = new VisitOperationsItems();


        prepareCustomerMaterialViewModel();

        prepareOrderViewModel();
    }

    private void prepareOrderViewModel() {
        viewModel_OrderHeader = new ViewModelProvider(this).get(VisitOperationsHeaderViewModel.class);
        viewModel_OrderItem = new ViewModelProvider(this).get(VisitOperationsItemsViewModel.class);

        viewModel_OrderHeader.initialRead(this::showError);
        viewModel_OrderItem.initialRead(this::showError);

        viewModel_OrderHeader.getObservableItems().observe(this, tourHeaders -> {
            if (tourHeaders != null) {
            }
        });
        viewModel_OrderItem.getObservableItems().observe(this, tourItems -> {
            if (tourItems != null) {
            }
        });
    }

    /**
     * Initializes the view model and add observers on it
     */
    private void prepareCustomerMaterialViewModel() {
        viewModel = new ViewModelProvider(this).get(TotalMaterialViewModel.class);
        viewModel.initialRead(this::showError);
        //LiveData<List<CustomerMaterial>> liveData = viewModel.getObservableItems();
        viewModel.getObservableItems().observe(this, customerMaterialSet -> {
            if (customerMaterialSet != null) {
                this.totalMaterialSet = customerMaterialSet; //Allaho Akbar. Marakby 07.02.2021
                Globals.customerMaterialEntity = customerMaterialSet;
                int i = 1;
                for (TotalMaterial customerMaterial : this.totalMaterialSet) {
                    m_orderItemModel = new JasmineOrderItemsInnerModel();
                    m_orderItemModel.setItemQty(customerMaterial.getQty());
                    m_orderItemModel.setM_index(i);
                    i++;
                    m_orderItemModel.setMaterial_Desc(customerMaterial.getMaterialtext());
                    m_orderItemModel.setMaterial_No(customerMaterial.getMaterial());
                    m_orderItemModel.setVanStockQty(customerMaterial.getQty());
                    m_orderItemModel.setUOM(customerMaterial.getUom());
                    m_orderItemModel.setItemQty("0");
                    //m_orderItemModel.setUnitPrice(customerMaterial.);

                    m_orderItemsList.add(m_orderItemModel);

                    order_adapter.notifyDataSetChanged();
                }
                m_orderItemsList_out = (ArrayList<JasmineOrderItemsInnerModel>) m_orderItemsList.clone();
            }
        });
    }

    protected void showError(String errorMessage) {

        new DialogHelper(this,
                R.style.OnboardingDefaultTheme_Dialog_Alert)
                .showOKOnlyDialog(
                        this.getSupportFragmentManager(),
                        errorMessage, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SYNC_ACTION_ITEM, 0, R.string.synchronize_action);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.debug("onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case SETTINGS_SCREEN_ITEM:
                LOGGER.debug("settings screen menu item selected.");
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(intent, SETTINGS_SCREEN_ITEM);
                return true;

            case SYNC_ACTION_ITEM:
                synchronize();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.debug("EntitySetListActivity::onActivityResult, request code: " + requestCode + " result code: " + resultCode);
        if (requestCode == SETTINGS_SCREEN_ITEM) {
            LOGGER.debug("Calling AppState to retrieve settings after settings screen is closed.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            syncService = null;
        }
    }

    private void synchronize() {
        OfflineWorkerUtil.sync(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        OfflineWorkerUtil.addProgressListener(progressListener);
        WorkManager.getInstance(getApplicationContext())
                .getWorkInfosByTagLiveData(OfflineWorkerUtil.OFFLINE_WORKER_SYNC_TAG)
                .observe(this, workInfos -> {
                    for(WorkInfo workInfo : workInfos) {
                        if(workInfo.getState().isFinished()) {
                            if (syncItem != null) {
                                syncItem.setEnabled(true);
                            }
                            OfflineWorkerUtil.removeProgressListener(progressListener);
                            progressBar.setVisibility(View.INVISIBLE);
                            switch (workInfo.getState()) {
                                case SUCCEEDED:
                                    LOGGER.info("Offline sync done.");
                                    break;
                                case FAILED:
                                    new DialogHelper(getApplication(), R.style.OnboardingDefaultTheme_Dialog_Alert).showOKOnlyDialog(
                                            getSupportFragmentManager(),
                                            (workInfo.getOutputData().getString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL) == null)? getString(R.string.synchronize_failure_detail) : workInfo.getOutputData().getString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL),
                                            null, null, null
                                    );
                                    break;
                            }
                        }
                    }
                });

    }

    private final OfflineProgressListener progressListener = (provider, progress) -> {
        if (progress.getCurrentStepNumber() > previousStep) {
            currentStepNumber = totalNumberOfSteps / 2 * progress.getCurrentStepNumber() / progress.getTotalNumberOfSteps() + startPointForSync;
            previousStep = progress.getCurrentStepNumber();
            progressBar.setMax(totalNumberOfSteps);
            progressBar.setProgress(currentStepNumber);
        }
        if (progress.getCurrentStepNumber() == progress.getTotalNumberOfSteps()) {
            previousStep = 0;
            currentStepNumber = 0;
            if (startPointForSync == 0) {
                startPointForSync = totalNumberOfSteps / 2;
            } else {
                startPointForSync = 0;
            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    public void btnSaveClick(View view) {
        //m_orderItemsList = (ArrayList<JasmineOrderItemsInnerModel>) m_orderItemsList_out.clone();
        order_adapter.notifyDataSetChanged();
        try {
            constructExDoc();
            //region$ Header
            tourHeader.setVisitid(Globals.customersEntity.getVisitid());
            tourHeader.setExdocument(Exdocument);
            tourHeader.setCustomerno(Globals.customersEntity.getCustomerno());
            tourHeader.setAction("Create Sales Order");
            tourHeader.setCurrency("SAR");
            tourHeader.setDistchannel(Globals.customersEntity.getDistchannel());
            tourHeader.setDivision(Globals.customersEntity.getDivision());
            tourHeader.setDocumenttype("ZVCI");
            tourHeader.setDriverid(Globals.customersEntity.getDriverid());
            tourHeader.setFvar("var1");
            tourHeader.setFvar2("var2");
            tourHeader.setFvar3("var3");
            tourHeader.setInsertiondate(currentDate);
            tourHeader.setInsertiontime(currentTime);
            tourHeader.setSalesorg(Globals.customersEntity.getSalesorg());
            tourHeader.setTotalprice(caculateTotalPrice(m_orderItemsList));/////TODO
            tourHeader.setVisitOperationsItemsSet(new List<VisitOperationsItems>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(@Nullable Object o) {
                    return false;
                }

                @NonNull
                @Override
                public Iterator<VisitOperationsItems> iterator() {
                    return new Iterator<VisitOperationsItems>() {
                        @Override
                        public boolean hasNext() {
                            return false;
                        }

                        @Override
                        public VisitOperationsItems next() {
                            return new VisitOperationsItems();
                        }
                    };
                }

                @Nullable
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public <T> T[] toArray(@Nullable T[] a) {
                    return null;
                }

                @Override
                public boolean add(VisitOperationsItems visitOperationsItems) {
                    return false;
                }

                @Override
                public boolean remove(@Nullable Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(@NonNull Collection<? extends VisitOperationsItems> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, @NonNull Collection<? extends VisitOperationsItems> c) {
                    return false;
                }

                @Override
                public boolean removeAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(@NonNull Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public VisitOperationsItems get(int index) {
                    return new VisitOperationsItems();
                }

                @Override
                public VisitOperationsItems set(int index, VisitOperationsItems element) {
                    return new VisitOperationsItems();
                }

                @Override
                public void add(int index, VisitOperationsItems element) {

                }

                @Override
                public VisitOperationsItems remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(@Nullable Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(@Nullable Object o) {
                    return 0;
                }

                @NonNull
                @Override
                public ListIterator<VisitOperationsItems> listIterator() {
                    return null;
                }

                @NonNull
                @Override
                public ListIterator<VisitOperationsItems> listIterator(int index) {
                    return null;
                }

                @NonNull
                @Override
                public List<VisitOperationsItems> subList(int fromIndex, int toIndex) {
                    return null;
                }

            });
            viewModel_OrderHeader.create(tourHeader);


            //endregion


            //$region Items
            if (m_orderItemsList != null) {
                int i = 10;
                for (JasmineOrderItemsInnerModel model : m_orderItemsList) {
                    tourItem = new VisitOperationsItems();
                    tourItem.setBatch("");
                    tourItem.setCurrency("SAR");
                    tourItem.setItemnumber(String.valueOf(i));
                    i = i + 10;
                    tourItem.setDiscount("");
                    tourItem.setExdocument(Exdocument);
                    tourItem.setFvar("var1");
                    tourItem.setFvar2("var2");
                    tourItem.setFvar3("var3");
                    tourItem.setMaterial(model.getMaterial_No());
                    tourItem.setPrice("10"); ///TODO
                    tourItem.setCurrency("SAR");
                    tourItem.setVisitid(Globals.customersEntity.getVisitid());
                    tourItem.setFvar2("var2");
                    tourItem.setMainqty(model.getItemQty());
                    tourItem.setSalesoffice(Globals.customersEntity.getSalesoffice());
                    tourItem.setTotalprice(caculateTotalPrice(m_orderItemsList));///TODO
                    tourItem.setMainuom(model.getUOM());
                    viewModel_OrderItem.create(tourItem);


                }

                Toast.makeText(this, "Order Created. Sync to upload", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Failed To Save", Toast.LENGTH_SHORT).show();
        }
        //endregion


    }

    private String caculateTotalPrice(ArrayList<JasmineOrderItemsInnerModel> m_orderItemsList) {
        String totalPrice = "100";///TODO
        try {

        } catch (Exception ex) {
        }
        return totalPrice;
    }

    private void constructExDoc() {
        Exdocument = "";
        try {
            String customer = Globals.customersEntity.getCustomerno();
            Calendar c = Calendar.getInstance();

            currentDate = String.valueOf(c.get(Calendar.YEAR)) +
                    String.valueOf(c.get(Calendar.MONTH))
                    + String.valueOf(c.get(Calendar.DAY_OF_MONTH));

            currentTime = String.valueOf(c.get(Calendar.HOUR_OF_DAY))
                    + String.valueOf(c.get(Calendar.MINUTE))
                    + String.valueOf(c.get(Calendar.SECOND));
            Exdocument = customer + currentDate + currentTime;

        } catch (Exception ex) {
        }
    }

    public void btnPrintClick(View view) {
        Toast.makeText(JasmineCreateCashOrderActivity.this, "Not Implmented", Toast.LENGTH_SHORT).show();
    }

    class OrderItemsAdapter extends BaseAdapter {
        ArrayList<JasmineOrderItemsInnerModel> Items = new ArrayList<>();

        public OrderItemsAdapter(ArrayList<JasmineOrderItemsInnerModel> items) {
            Items = items;
        }


        @Override
        public int getCount() {
            return Items.size();
        }

        @Override
        public Object getItem(int position) {
            return Items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final RecyclerView.ViewHolder listViewHolder;
            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.jasmine_order_row, null);

            TextView Material_NO = view1.findViewById(R.id.material_no);
            TextView Material_Desc = view1.findViewById(R.id.material_desc);
            TextView Van_Stock_qty = view1.findViewById(R.id.van_stock);
            TextView UOM = view1.findViewById(R.id.uom);
            EditText qty = (EditText) view1.findViewById(R.id.qty);


            Material_NO.setText("Material Code: " + Items.get(position).getMaterial_No());
            Material_Desc.setText(Items.get(position).getMaterial_Desc());
            Van_Stock_qty.setText("Van Stock: " + Items.get(position).getVanStockQty());
            qty.setText(Items.get(position).getItemQty());
            Items.get(position).setItemQty(qty.getText().toString());
            UOM.setText(Items.get(position).getUOM());

            qty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (qty.getText() != null && !qty.getText().equals("")) {
                            if (Float.valueOf(qty.getText().toString()) > Float.valueOf(Items.get(position).getVanStockQty())) {
                                qty.setText("");
                            }
                            Items.get(position).setItemQty(qty.getText().toString());
                            m_orderItemsList_out.get(position).setItemQty(qty.getText().toString());

                        }
                    } catch (Exception ex
                    ) {
                    }

                }
            });


            return view1;

        }
    }


}
