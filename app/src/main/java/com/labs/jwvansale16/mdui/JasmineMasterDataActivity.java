package com.labs.jwvansale16.mdui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.labs.jwvansale16.R;
import com.labs.jwvansale16.app.SAPWizardApplication;
import com.labs.jwvansale16.mdui.customervisitplanset.CustomerVisitPlanSetActivity;
import com.labs.jwvansale16.mdui.freematerialset.FreeMaterialSetActivity;
import com.labs.jwvansale16.mdui.mastermaterialset.MasterMaterialSetActivity;
import com.labs.jwvansale16.service.*;
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar;
import com.sap.cloud.mobile.flowv2.core.DialogHelper;
import com.sap.cloud.mobile.foundation.usage.UsageService;
import com.sap.cloud.mobile.odata.core.Action0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.labs.jwvansale16.mdui.customermaterialset.CustomerMaterialSetActivity;
//import com.labs.jwvansale16.mdui.customersset.CustomersSetActivity;
//import com.labs.jwvansale16.mdui.visitlistsset.VisitListsSetActivity;

public class JasmineMasterDataActivity extends AppCompatActivity {

    /*
     * Android Bound Service to handle offline synchronization operations. Service runs in foreground mode to maximize
     * resiliency.
     */
    private OfflineSyncWorker syncService;

    /*
     * Flag to indicate that current acvtity is bound to the Offline Sync Service
     */
    boolean isBound = false;

    /**
     * Flag to indicate whether requesting user confirmation before navigation is needed
     */
    protected boolean isNavigationDisabled = false;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(JasmineMasterDataActivity.class);

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
        setContentView(R.layout.jasmine_master_data);

        //sapServiceManager = ((SAPWizardApplication) getApplication()).getSAPServiceManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Master Data");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UsageService.getInstance().eventBehaviorViewDisplayed(JasmineMasterDataActivity.class.getSimpleName(),
                "elementId", "onCreate", "called");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, JasmineMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        finish();
////        super.onBackPressed();
////        moveTaskToBack(true);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SETTINGS_SCREEN_ITEM, 0, R.string.menu_item_settings);
        menu.add(0, SYNC_ACTION_ITEM, 1, R.string.synchronize_action);
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

    private void synchronizeConclusion() {
        unbindService(serviceConnection);
        isBound = false;
        syncService = null;
    }

    public void btnVisitListClick(View view) {
//        Intent intent = new Intent(this, VisitDetailsSetActivity.class);
//        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnMaterialsClick(View view) {
//        Intent intent = new Intent(this, TotalMaterialSetActivity.class);
//        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnReportsClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnMaterialMasterClick(View view) {
        //Context context = JasmineMasterDataActivity.this;
        Intent intent = new Intent(this, MasterMaterialSetActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void btnCustomerMasterClick(View view) {
        Intent intent = new Intent(this, CustomerVisitPlanSetActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void btnPromotionMasterDataClick(View view) {
        Intent intent = new Intent(this, FreeMaterialSetActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }
}
