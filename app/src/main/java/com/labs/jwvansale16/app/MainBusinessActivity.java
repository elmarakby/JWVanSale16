package com.labs.jwvansale16.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.labs.jwvansale16.R;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.app.NotificationManager;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.labs.jwvansale16.customized.Globals;
import com.labs.jwvansale16.mdui.JasmineMainActivity;
import com.labs.jwvansale16.mdui.JasmineSplashActivity;
import com.sap.cloud.mobile.flowv2.core.DialogHelper;
import com.sap.cloud.mobile.flowv2.securestore.UserSecureStoreDelegate;
import com.sap.cloud.mobile.foundation.mobileservices.ServiceResult;
import com.sap.cloud.mobile.foundation.user.User;
import com.sap.cloud.mobile.foundation.user.UserService;
import com.sap.cloud.mobile.odata.offline.OfflineODataException;
import com.sap.cloud.mobile.foundation.user.DeviceUser;
import kotlin.Unit;
import com.labs.jwvansale16.service.*;
import com.labs.jwvansale16.service.ext.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.labs.jwvansale16.mdui.EntitySetListActivity;

public class MainBusinessActivity extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainBusinessActivity.class);
    private ProgressBar progressBar = null;
    private boolean isOfflineStoreInitialized = false;

    private void navigateToEntityList() {
//        Intent intent = new Intent(this, EntitySetListActivity.class);
//        Intent intent = new Intent(this, JasmineMainActivity.class);
        Intent intent = new Intent(this, JasmineSplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business);

    }

    private void startEntitySetListActivity() {
        if (isOfflineStoreInitialized) {
            navigateToEntityList();
        } else {
            LOGGER.info("Waiting for the sync finish.");
            WorkManager.getInstance(getApplicationContext())
                .getWorkInfosByTagLiveData(OfflineWorkerUtil.OFFLINE_WORKER_OPEN_TAG)
                .observe(this, workInfos -> {
                    for (WorkInfo workInfo : workInfos) {
                        if (workInfo.getState().isFinished()) {
                            switch (workInfo.getState()) {
                                case SUCCEEDED:
                                    navigateToEntityList();
                                    break;
                                case FAILED:
                                    switch (workInfo.getOutputData().getInt(OfflineWorkerUtil.OUTPUT_ERROR_KEY, 0)) {
                                        case -1:
                                            offlineNetworkErrorAction();
                                            break;
                                        case -10425:
                                            offlineTransactionIssueAction();
                                            break;
                                        default:
                                            new DialogHelper(getApplication(), R.style.OnboardingDefaultTheme_Dialog_Alert)
                                                    .showOKOnlyDialog(
                                                            getSupportFragmentManager(),
                                                            (workInfo.getOutputData().getString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL) == null)? "Offline sync failed" : workInfo.getOutputData().getString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL),
                                                            getResources().getString(R.string.offline_initial_open_error), null,
                                                            (() -> {
                                                                Intent intent = new Intent(this, WelcomeActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                                return Unit.INSTANCE;
                                                            })
                                                    );
                                            break;
                                    }
                                    break;
                            }
                        }
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar = findViewById(R.id.main_bus_progress_bar);
        OfflineOpenWorker.setProgressBar(progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        findViewById(R.id.offlineNetworkErrorScreen).setVisibility(View.INVISIBLE);
        findViewById(R.id.offlineTransactionIssueScreen).setVisibility(View.INVISIBLE);
        findViewById(R.id.app_bar).setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isOfflineStoreInitialized = sharedPreferences.getBoolean(OfflineWorkerUtil.PREF_OFFLINE_INITIALIZED, false);

        if (isOfflineStoreInitialized) {
            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
            findViewById(R.id.init_offline_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.init_offline_description).setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.main_bus_resume_progress_bar).setVisibility(View.VISIBLE);
        } else {
            toolbar.setNavigationOnClickListener(v -> {
                getApplication().getSystemService(NotificationManager.class).cancel(OfflineBaseWorker.OFFLINE_NOTIFICATION_CHANNEL_INT_ID);
                WorkManager.getInstance(getApplication()).cancelUniqueWork(OfflineWorkerUtil.OFFLINE_OPEN_WORKER_UNIQUE_NAME);
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
            findViewById(R.id.init_offline_label).setVisibility(View.VISIBLE);
            findViewById(R.id.init_offline_description).setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            findViewById(R.id.main_bus_resume_progress_bar).setVisibility(View.INVISIBLE);

        }
        startEntitySetListActivity();
    }

    @Override
    public void onBackPressed() {
        if (!isOfflineStoreInitialized) {
            getApplication().getSystemService(NotificationManager.class).cancel(OfflineBaseWorker.OFFLINE_NOTIFICATION_CHANNEL_INT_ID);
            WorkManager.getInstance(getApplication()).cancelUniqueWork(OfflineWorkerUtil.OFFLINE_OPEN_WORKER_UNIQUE_NAME);
        }
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void offlineNetworkErrorAction() {
        runOnUiThread(() -> {
            OfflineNetworkErrorScreen offlineNetworkErrorScreen = findViewById(R.id.offlineNetworkErrorScreen);
            offlineNetworkErrorScreen.setVisibility(View.VISIBLE);
            findViewById(R.id.app_bar).setVisibility(View.INVISIBLE);
            findViewById(R.id.init_offline_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.init_offline_description).setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.main_bus_resume_progress_bar).setVisibility(View.INVISIBLE);

            OfflineNetworkErrorScreenSettings offlineNetworkErrorScreenSettings = new OfflineNetworkErrorScreenSettings.Builder().build();

            offlineNetworkErrorScreen.initialize(offlineNetworkErrorScreenSettings);
            setSupportActionBar(offlineNetworkErrorScreen.getToolBar());
            offlineNetworkErrorScreen.setButtonClickListener(
                    v -> {
                        OfflineWorkerUtil.reOpen(getApplication(), ((SAPWizardApplication) getApplication()).getAppConfig());
                        offlineNetworkErrorScreen.setVisibility(View.INVISIBLE);
                        findViewById(R.id.app_bar).setVisibility(View.VISIBLE);
                        findViewById(R.id.init_offline_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.init_offline_description).setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        findViewById(R.id.main_bus_resume_progress_bar).setVisibility(View.INVISIBLE);
                    }
            );
            offlineNetworkErrorScreen.setToolBarBackButtonClickListener(
                    v -> {
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
            );
        });
    }

    private void offlineTransactionIssueAction() {
        runOnUiThread(() -> {
            OfflineTransactionIssueScreen offlineTransactionIssueScreen = findViewById(R.id.offlineTransactionIssueScreen);
            offlineTransactionIssueScreen.setVisibility(View.VISIBLE);
            findViewById(R.id.app_bar).setVisibility(View.INVISIBLE);
            findViewById(R.id.init_offline_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.init_offline_description).setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.main_bus_resume_progress_bar).setVisibility(View.INVISIBLE);
            String previousUser = null;
            try {
                previousUser = OfflineWorkerUtil.getOfflineODataProvider().getPreviousUser();
            } catch (OfflineODataException exception) {
                LOGGER.error("Cannot get info of previous user. Exception happens: " + exception.getMessage());
            }

            OfflineTransactionIssueScreenSettings offlineTransactionIssueScreenSettings = new OfflineTransactionIssueScreenSettings.Builder().build();

            offlineTransactionIssueScreen.initialize(offlineTransactionIssueScreenSettings);
            if (previousUser != null) {
                DeviceUser user = UserSecureStoreDelegate.getInstance().getUserInfoByIdAsync(previousUser);
                if (user != null) {
                    offlineTransactionIssueScreen.setPrevUserName(user.getName());
                    offlineTransactionIssueScreen.setPrevUserMail(user.getEmail());
                } else {
                    offlineTransactionIssueScreen.setPrevUserName(previousUser);
                }
            }
            setSupportActionBar(offlineTransactionIssueScreen.getToolBar());
            offlineTransactionIssueScreen.setButtonClickListener(
                    v -> {
                        Intent transactionIssueHandleIntent = new Intent(this, WelcomeActivity.class);
                        transactionIssueHandleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(transactionIssueHandleIntent);
                    }
            );
            offlineTransactionIssueScreen.setToolBarBackButtonClickListener(
                    v -> {
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
            );
        });
    }
}
