package com.labs.jwvansale16.mdui;

import com.labs.jwvansale16.app.SAPWizardApplication;

import android.app.NotificationManager;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import com.sap.cloud.mobile.flowv2.core.DialogHelper;
import com.labs.jwvansale16.service.OfflineBaseWorker;
import com.labs.jwvansale16.service.OfflineProgressListener;
import com.labs.jwvansale16.service.OfflineWorkerUtil;
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.JvmClassMappingKt;
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer;
import com.sap.cloud.mobile.foundation.usage.UsageService;
import com.labs.jwvansale16.mdui.actioncancelvisitset.ActionCancelVisitSetActivity;
import com.labs.jwvansale16.mdui.actioncheckinmaterialset.ActionCheckInMaterialSetActivity;
import com.labs.jwvansale16.mdui.actionvisitoperationsset.ActionVisitOperationsSetActivity;
import com.labs.jwvansale16.mdui.cancelvisitset.CancelVisitSetActivity;
import com.labs.jwvansale16.mdui.checkinmaterialset.CheckInMaterialSetActivity;
import com.labs.jwvansale16.mdui.checkoutmaterialset.CheckoutMaterialSetActivity;
import com.labs.jwvansale16.mdui.collectionsset.CollectionsSetActivity;
import com.labs.jwvansale16.mdui.customervisitplanset.CustomerVisitPlanSetActivity;
import com.labs.jwvansale16.mdui.driverdetailsset.DriverDetailsSetActivity;
import com.labs.jwvansale16.mdui.freematerialset.FreeMaterialSetActivity;
import com.labs.jwvansale16.mdui.mastermaterialset.MasterMaterialSetActivity;
import com.labs.jwvansale16.mdui.openbillingset.OpenBillingSetActivity;
import com.labs.jwvansale16.mdui.pricesa005set.PricesA005SetActivity;
import com.labs.jwvansale16.mdui.pricesa904set.PricesA904SetActivity;
import com.labs.jwvansale16.mdui.pricesa905set.PricesA905SetActivity;
import com.labs.jwvansale16.mdui.pricesa906set.PricesA906SetActivity;
import com.labs.jwvansale16.mdui.pricesa908set.PricesA908SetActivity;
import com.labs.jwvansale16.mdui.priceskonpset.PricesKonpSetActivity;
import com.labs.jwvansale16.mdui.totalmaterialset.TotalMaterialSetActivity;
import com.labs.jwvansale16.mdui.visitdetailsset.VisitDetailsSetActivity;
import com.labs.jwvansale16.mdui.visitoperationsheaderset.VisitOperationsHeaderSetActivity;
import com.labs.jwvansale16.mdui.visitoperationsitemsset.VisitOperationsItemsSetActivity;
import com.sap.cloud.mobile.fiori.object.ObjectCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.labs.jwvansale16.R;

/*
 * An activity to display the list of all entity types from the OData service
 */
public class EntitySetListActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySetListActivity.class);
    private static final int BLUE_ANDROID_ICON = R.drawable.ic_android_blue;
    private static final int WHITE_ANDROID_ICON = R.drawable.ic_android_white;

    public enum EntitySetName {
        ActionCancelVisitSet("ActionCancelVisitSet", R.string.eset_actioncancelvisitset,BLUE_ANDROID_ICON),
        ActionCheckInMaterialSet("ActionCheckInMaterialSet", R.string.eset_actioncheckinmaterialset,WHITE_ANDROID_ICON),
        ActionVisitOperationsSet("ActionVisitOperationsSet", R.string.eset_actionvisitoperationsset,BLUE_ANDROID_ICON),
        CancelVisitSet("CancelVisitSet", R.string.eset_cancelvisitset,WHITE_ANDROID_ICON),
        CheckInMaterialSet("CheckInMaterialSet", R.string.eset_checkinmaterialset,BLUE_ANDROID_ICON),
        CheckoutMaterialSet("CheckoutMaterialSet", R.string.eset_checkoutmaterialset,WHITE_ANDROID_ICON),
        CollectionsSet("CollectionsSet", R.string.eset_collectionsset,BLUE_ANDROID_ICON),
        CustomerVisitPlanSet("CustomerVisitPlanSet", R.string.eset_customervisitplanset,WHITE_ANDROID_ICON),
        DriverDetailsSet("DriverDetailsSet", R.string.eset_driverdetailsset,BLUE_ANDROID_ICON),
        FreeMaterialSet("FreeMaterialSet", R.string.eset_freematerialset,WHITE_ANDROID_ICON),
        MasterMaterialSet("MasterMaterialSet", R.string.eset_mastermaterialset,BLUE_ANDROID_ICON),
        OpenBillingSet("OpenBillingSet", R.string.eset_openbillingset,WHITE_ANDROID_ICON),
        PricesA005Set("PricesA005Set", R.string.eset_pricesa005set,BLUE_ANDROID_ICON),
        PricesA904Set("PricesA904Set", R.string.eset_pricesa904set,WHITE_ANDROID_ICON),
        PricesA905Set("PricesA905Set", R.string.eset_pricesa905set,BLUE_ANDROID_ICON),
        PricesA906Set("PricesA906Set", R.string.eset_pricesa906set,WHITE_ANDROID_ICON),
        PricesA908Set("PricesA908Set", R.string.eset_pricesa908set,BLUE_ANDROID_ICON),
        PricesKonpSet("PricesKonpSet", R.string.eset_priceskonpset,WHITE_ANDROID_ICON),
        TotalMaterialSet("TotalMaterialSet", R.string.eset_totalmaterialset,BLUE_ANDROID_ICON),
        VisitDetailsSet("VisitDetailsSet", R.string.eset_visitdetailsset,WHITE_ANDROID_ICON),
        VisitOperationsHeaderSet("VisitOperationsHeaderSet", R.string.eset_visitoperationsheaderset,BLUE_ANDROID_ICON),
        VisitOperationsItemsSet("VisitOperationsItemsSet", R.string.eset_visitoperationsitemsset,WHITE_ANDROID_ICON);

        private int titleId;
        private int iconId;
        private String entitySetName;

        EntitySetName(String name, int titleId, int iconId) {
            this.entitySetName = name;
            this.titleId = titleId;
            this.iconId = iconId;
        }

        public int getTitleId() {
                return this.titleId;
        }

        public String getEntitySetName() {
                return this.entitySetName;
        }
    }

    private final List<String> entitySetNames = new ArrayList<>();
    private final Map<String, EntitySetName> entitySetNameMap = new HashMap<>();

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

        setContentView(R.layout.activity_entity_set_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.sync_determinate);
        UsageService usageService = SDKInitializer.INSTANCE.getService(JvmClassMappingKt.getKotlinClass(UsageService.class));
        if (usageService != null) {
            usageService.eventBehaviorViewDisplayed(EntitySetListActivity.class.getSimpleName(),
                    "elementId", "onCreate", "called");
        }

        entitySetNames.clear();
        entitySetNameMap.clear();
        for (EntitySetName entitySet : EntitySetName.values()) {
            String entitySetTitle = getResources().getString(entitySet.getTitleId());
            entitySetNames.add(entitySetTitle);
            entitySetNameMap.put(entitySetTitle, entitySet);
        }

        final ListView listView = findViewById(R.id.entity_list);
        final EntitySetListAdapter adapter = new EntitySetListAdapter(this, R.layout.element_entity_set_list, entitySetNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            EntitySetName entitySetName = entitySetNameMap.get(adapter.getItem(position));
            if (usageService != null) {
                usageService.eventBehaviorUserInteraction(EntitySetListActivity.class.getSimpleName(),
                    "position: " + position, "onClicked", entitySetName.getEntitySetName());
            }
            Context context = EntitySetListActivity.this;
            Intent intent;
            switch (entitySetName) {
                case ActionCancelVisitSet:
                    intent = new Intent(context, ActionCancelVisitSetActivity.class);
                    break;
                case ActionCheckInMaterialSet:
                    intent = new Intent(context, ActionCheckInMaterialSetActivity.class);
                    break;
                case ActionVisitOperationsSet:
                    intent = new Intent(context, ActionVisitOperationsSetActivity.class);
                    break;
                case CancelVisitSet:
                    intent = new Intent(context, CancelVisitSetActivity.class);
                    break;
                case CheckInMaterialSet:
                    intent = new Intent(context, CheckInMaterialSetActivity.class);
                    break;
                case CheckoutMaterialSet:
                    intent = new Intent(context, CheckoutMaterialSetActivity.class);
                    break;
                case CollectionsSet:
                    intent = new Intent(context, CollectionsSetActivity.class);
                    break;
                case CustomerVisitPlanSet:
                    intent = new Intent(context, CustomerVisitPlanSetActivity.class);
                    break;
                case DriverDetailsSet:
                    intent = new Intent(context, DriverDetailsSetActivity.class);
                    break;
                case FreeMaterialSet:
                    intent = new Intent(context, FreeMaterialSetActivity.class);
                    break;
                case MasterMaterialSet:
                    intent = new Intent(context, MasterMaterialSetActivity.class);
                    break;
                case OpenBillingSet:
                    intent = new Intent(context, OpenBillingSetActivity.class);
                    break;
                case PricesA005Set:
                    intent = new Intent(context, PricesA005SetActivity.class);
                    break;
                case PricesA904Set:
                    intent = new Intent(context, PricesA904SetActivity.class);
                    break;
                case PricesA905Set:
                    intent = new Intent(context, PricesA905SetActivity.class);
                    break;
                case PricesA906Set:
                    intent = new Intent(context, PricesA906SetActivity.class);
                    break;
                case PricesA908Set:
                    intent = new Intent(context, PricesA908SetActivity.class);
                    break;
                case PricesKonpSet:
                    intent = new Intent(context, PricesKonpSetActivity.class);
                    break;
                case TotalMaterialSet:
                    intent = new Intent(context, TotalMaterialSetActivity.class);
                    break;
                case VisitDetailsSet:
                    intent = new Intent(context, VisitDetailsSetActivity.class);
                    break;
                case VisitOperationsHeaderSet:
                    intent = new Intent(context, VisitOperationsHeaderSetActivity.class);
                    break;
                case VisitOperationsItemsSet:
                    intent = new Intent(context, VisitOperationsItemsSetActivity.class);
                    break;
                    default:
                        return;
            }
            context.startActivity(intent);
        });
    }

    public class EntitySetListAdapter extends ArrayAdapter<String> {

        EntitySetListAdapter(@NonNull Context context, int resource, List<String> entitySetNames) {
            super(context, resource, entitySetNames);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            EntitySetName entitySetName = entitySetNameMap.get(getItem(position));
            if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_entity_set_list, parent, false);
            }
            String headLineName = getResources().getString(entitySetName.titleId);
            ObjectCell entitySetCell = convertView.findViewById(R.id.entity_set_name);
            entitySetCell.setHeadline(headLineName);
            entitySetCell.setDetailImage(entitySetName.iconId);
            return convertView;
        }
    }
                
    @Override
    public void onBackPressed() {
            moveTaskToBack(true);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entity_set_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.debug("onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_settings:
                LOGGER.debug("settings screen menu item selected.");
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.menu_sync:
                synchronize();
                syncItem = item;
                syncItem.setEnabled(false);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (findViewById(R.id.sync_determinate).getVisibility() == View.VISIBLE) {
            getApplication().getSystemService(NotificationManager.class).cancel(OfflineBaseWorker.OFFLINE_NOTIFICATION_CHANNEL_INT_ID);
            WorkManager.getInstance(getApplication()).cancelUniqueWork(OfflineWorkerUtil.OFFLINE_SYNC_WORKER_UNIQUE_NAME);
        }
        if (syncItem != null) {
            syncItem.setEnabled(true);
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

}
