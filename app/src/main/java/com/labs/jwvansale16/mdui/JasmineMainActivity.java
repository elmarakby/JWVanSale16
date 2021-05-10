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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.labs.jwvansale16.R;
import com.labs.jwvansale16.app.SAPWizardApplication;
import com.labs.jwvansale16.customized.Globals;
import com.labs.jwvansale16.mdui.checkoutmaterialset.CheckoutMaterialSetActivity;
import com.labs.jwvansale16.mdui.visitdetailsset.VisitDetailsSetActivity;
import com.labs.jwvansale16.service.OfflineOpenWorker;
import com.labs.jwvansale16.service.OfflineProgressListener;
import com.labs.jwvansale16.service.OfflineWorkerUtil;
import com.labs.jwvansale16.service.*;
import com.labs.jwvansale16.viewmodel.driverdetails.DriverDetailsViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.DriverDetails;
import com.sap.cloud.mobile.fiori.indicator.FioriProgressBar;
import com.sap.cloud.mobile.flowv2.core.DialogHelper;
import com.sap.cloud.mobile.foundation.usage.UsageService;
import com.sap.cloud.mobile.odata.core.Action0;
import com.sap.cloud.mobile.odata.offline.OfflineODataException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class JasmineMainActivity extends AppCompatActivity {

    /*
     * Android Bound Service to handle offline synchronization operations. Service runs in foreground mode to maximize
     * resiliency.
     */
//    private OfflineODataSyncService syncService;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(JasmineMainActivity.class);

    //region UI
    private TextView txtDriverID;
    private TextView txtDriverName;
    private TextView txtDate;
    private TextView txtSupervisor;
    private TextView txtSalesLimit;
    private TextView txtVatRegNo;
    private TextView txtTourID;
    private TextView txtRoute;
    private TextView txtVechile;
    //

    /* Fiori progress bar for busy indication if either update or delete action is clicked upon */
    private FioriProgressBar progressBar = null;
    private final int totalNumberOfSteps = 40;
    private int startPointForSync = 0;
    private int previousStep = 0;
    private int currentStepNumber = 0;
    private MenuItem syncItem = null;

    private DriverDetailsViewModel driverDetailsViewModel;
    private DriverDetails driverDetails;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jasmine_main_page);

        //sapServiceManager = ((SAPWizardApplication) getApplication()).ge.get.getSAPServiceManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UsageService.getInstance().eventBehaviorViewDisplayed(JasmineMainActivity.class.getSimpleName(),
                "elementId", "onCreate", "called");

        prepareUIComponents();

        driverDetails = Globals.driverDetails;

        if (driverDetails == null)
        {
            prepareDriverDetailViewModel();
        }
        else {
            txtDate.setText("Date: " + Calendar.getInstance().getTime());
            txtDriverID.setText("Driver ID: " + driverDetails.getDriverid());
            txtDriverName.setText("Driver Name: " + driverDetails.getDrivername());
            txtRoute.setText("Route: " + driverDetails.getRoute());
            txtSalesLimit.setText("Sales Limit: " + driverDetails.getSaleslimit());
            txtSupervisor.setText("Supervisor: " + driverDetails.getSupervisorname());
            txtTourID.setText("Tour ID: " + driverDetails.getVisitid());
            txtVatRegNo.setText("VAT Reg. No: " + driverDetails.getTaxregno());
            txtVechile.setText("Vechile: " + driverDetails.getVehicle());
        }
    }

    private void prepareUIComponents() {
        txtDriverID = findViewById(R.id.txtDriverID);

        txtDriverName = findViewById(R.id.txtDriverName);

        txtDate = findViewById(R.id.txtDate);

        txtSupervisor = findViewById(R.id.txtSupervisor);

        txtSalesLimit = findViewById(R.id.txtSalesLimit);

        txtVatRegNo = findViewById(R.id.txtVatRegNo);

        txtTourID = findViewById(R.id.txtTourID);

        txtRoute = findViewById(R.id.txtRoute);

        txtVechile = findViewById(R.id.txtVechile);

        progressBar = findViewById(R.id.sync_indeterminate_js);

    }

    private void prepareDriverDetailViewModel() {
        driverDetailsViewModel =  new ViewModelProvider(this).get(DriverDetailsViewModel.class);
        driverDetailsViewModel.initialRead(this::showError);

        driverDetailsViewModel.getObservableItems().observe(this, driverDetails -> {
            if (driverDetails != null) {
                try{
                this.driverDetails = driverDetails.get(0);}
                catch(Exception ex)
                {
                    Toast.makeText(this,"Driver Data Not retrieved",Toast.LENGTH_LONG);
                    LOGGER.error(ex.getMessage());
                }
                Globals.driverDetails = this.driverDetails;
                if (this.driverDetails != null) {
                    txtDate.setText("Date: " + Calendar.getInstance().getTime());
                    txtDriverID.setText("Driver ID: " + this.driverDetails.getDriverid());
                    txtDriverName.setText("Driver Name: " + this.driverDetails.getDrivername());
                    txtRoute.setText("Route: " + this.driverDetails.getRoute());
                    txtSalesLimit.setText("Sales Limit: " + this.driverDetails.getSaleslimit());
                    txtSupervisor.setText("Supervisor: " + this.driverDetails.getSupervisorname());
                    txtTourID.setText("Tour ID: " + this.driverDetails.getVisitid());
                    txtVatRegNo.setText("VAT Reg. No: " + this.driverDetails.getTaxregno());
                    txtVechile.setText("Vechile: " + this.driverDetails.getVehicle());

                }
            }
        });
    }

    private void showError(String errormessage) {
        new DialogHelper(this,
                R.style.Flows_Dialog).showOKOnlyDialog(this.getSupportFragmentManager(), errormessage, null, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareDriverDetailViewModel();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

//    private void synchronize(Action0 syncCompleteHandler) {
//        if (progressBar == null) {
//            progressBar = getWindow().getDecorView().findViewById(R.id.sync_indeterminate_js);
//        }
//        progressBar.setVisibility(View.VISIBLE);
//        serviceConnection = new ServiceConnection() {
//            public void onServiceConnected(ComponentName className, IBinder service) {
//                syncService = ((OfflineODataSyncService.LocalBinder) service).getService();
//                isBound = true;
//                sapServiceManager.synchronize(syncService,
//                        () -> JasmineMainActivity.this.runOnUiThread(() -> {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            syncCompleteHandler.call();
//                        }),
//                        error -> JasmineMainActivity.this.runOnUiThread(() -> {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            new DialogHelper(getApplication(), R.style.OnboardingDefaultTheme_Dialog_Alert).showOKOnlyDialog(
//                                    getSupportFragmentManager(),
//                                    getString(R.string.synchronize_failure_detail),
//                                    null, null, null
//                            );
//                        }));
//            }
//
//            public void onServiceDisconnected(ComponentName className) {
//                syncService = null;
//                isBound = false;
//            }
//        };
//
//        if (bindService(new Intent(this, OfflineODataSyncService.class),
//                serviceConnection, Context.BIND_AUTO_CREATE)) {
//        } else {
//            unbindService(serviceConnection);
//            LOGGER.error("Bind service failure");
//        }
//    }

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

    private void synchronizeConclusion() {
        unbindService(serviceConnection);
        isBound = false;
        syncService = null;
    }

    public void btnVisitListClick(View view) {
        Intent intent = new Intent(this, VisitDetailsSetActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnMaterialsClick(View view) {
        Intent intent = new Intent(this, CheckoutMaterialSetActivity.class);// TotalMaterialSetActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnReportsClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnMasterDataClick(View view) {
        //Context context = JasmineMainActivity.this;
        Intent intent = new Intent(this, JasmineMasterDataActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );//| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void btnEndUploadClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnSyncClick(View view) {
        //Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
        synchronize();




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

    public void btnResetClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnLoadRequestClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnPreSalesDeliveryClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }

    public void btnReloadStatusClick(View view) {
        Toast.makeText(this, "Not Implmented", Toast.LENGTH_LONG).show();
    }
}
