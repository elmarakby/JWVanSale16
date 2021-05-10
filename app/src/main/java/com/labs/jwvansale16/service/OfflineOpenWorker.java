package com.labs.jwvansale16.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.*;
import com.sap.cloud.mobile.flowv2.securestore.UserSecureStoreDelegate;
import com.sap.cloud.mobile.foundation.model.AppConfig;
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import com.sap.cloud.mobile.odata.offline.OfflineODataProviderOperationProgress;
import com.labs.jwvansale16.app.MainBusinessActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CountDownLatch;

/**
 * Represents the worker to open the offline database.
 */
public class OfflineOpenWorker extends OfflineBaseWorker {

    public OfflineOpenWorker(
        @NonNull Context context,
        @NonNull WorkerParameters params) {
        super(context, params);
        localContext = context;
    }
    private int previousStep = 0;
    private final int totalNumberOfSteps = 40;
    private int startPointForOpen = 0;
    private static final Logger logger = LoggerFactory.getLogger(OfflineOpenWorker.class);
    private final Context localContext;
    private int result = 0;
    private String errorMessage = null;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar progressBar = null;

    public static void setProgressBar(ProgressBar pBar) {
        progressBar = pBar;
    }

    private final OfflineProgressListener progressListener = new OfflineProgressListener() {
        @Override
        public void onOfflineProgress(@NonNull OfflineODataProvider provider,
                @NonNull OfflineODataProviderOperationProgress progress) {
            Intent intent = new Intent(localContext, MainBusinessActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(localContext, 0, intent, 0);

            if (progress.getCurrentStepNumber() > previousStep) {
                previousStep = progress.getCurrentStepNumber();
                if (getInputData().getBoolean(OfflineWorkerUtil.IS_USER_SWITCH, false)) {
                    int currentStepNumber = totalNumberOfSteps / 2 * progress.getCurrentStepNumber() / progress.getTotalNumberOfSteps() + startPointForOpen;
                    notificationManager.notify(
                            OFFLINE_NOTIFICATION_CHANNEL_INT_ID,
                            createNotification(totalNumberOfSteps, currentStepNumber, pendingIntent)
                    );
                    if (progressBar != null) {
                        progressBar.setMax(totalNumberOfSteps);
                        progressBar.setProgress(currentStepNumber);
                    }
                } else {
                    notificationManager.notify(
                            OFFLINE_NOTIFICATION_CHANNEL_INT_ID,
                            createNotification(progress.getTotalNumberOfSteps(), progress.getCurrentStepNumber(), pendingIntent)
                    );
                    if (progressBar != null) {
                        progressBar.setMax(progress.getTotalNumberOfSteps());
                        progressBar.setProgress(progress.getCurrentStepNumber());
                    }
                }
            }
        }
    };

    @NonNull
    @Override
    public Result doWork() {
        AppConfig appConfig;
        if (getInputData().getString(OfflineWorkerUtil.INPUT_DATA_APP_CONFIG) != null) {
            appConfig = AppConfig.createAppConfigFromJsonString(getInputData().getString(OfflineWorkerUtil.INPUT_DATA_APP_CONFIG));
        } else {
            return Result.failure(new Data.Builder().putString(OfflineWorkerUtil.OUTPUT_ERROR_KEY, "No app config provided.").build());
        }

        setForegroundAsync(new ForegroundInfo(OFFLINE_NOTIFICATION_CHANNEL_INT_ID, createNotification(100, 0, null)));
        OfflineWorkerUtil.addProgressListener(progressListener);
        OfflineWorkerUtil.initializeOffline(localContext, appConfig, UserSecureStoreDelegate.getInstance().getRuntimeMultipleUserModeAsync());

        CountDownLatch countDownLatch = new CountDownLatch(1);

        if (OfflineWorkerUtil.getOfflineODataProvider() != null) {
            previousStep = 0;
            startPointForOpen = 0;
            OfflineWorkerUtil.getOfflineODataProvider().open(
                    () -> {
                        logger.info("Offline provider open succeeded.");
                        if (getInputData().getBoolean(OfflineWorkerUtil.IS_USER_SWITCH, false)) {
                            previousStep = 0;
                            startPointForOpen = totalNumberOfSteps / 2;
                            OfflineWorkerUtil.getOfflineODataProvider().download(
                                    () -> {
                                        PreferenceManager.getDefaultSharedPreferences(localContext)
                                                .edit()
                                                .putBoolean(OfflineWorkerUtil.PREF_OFFLINE_INITIALIZED, true)
                                                .apply();
                                        logger.info("Offline provider download succeeded.");
                                        countDownLatch.countDown();
                                    },
                                    exception -> {
                                        countDownLatch.countDown();
                                        errorMessage = (exception.getMessage() == null)? "Unknown offline sync error when downloading data." : exception.getMessage();
                                        logger.error(errorMessage);
                                    }
                            );
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(localContext)
                                    .edit()
                                    .putBoolean(OfflineWorkerUtil.PREF_OFFLINE_INITIALIZED, true)
                                    .apply();
                            countDownLatch.countDown();
                        }
                    },
                    exception -> {
                        errorMessage = (exception.getMessage() == null)? "Unknown offline sync error when init opening data." : exception.getMessage();
                        logger.error(errorMessage);
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) (localContext.getSystemService(
                                        Context.CONNECTIVITY_SERVICE));
                        Network activeNetwork = connectivityManager.getActiveNetwork();
                        NetworkCapabilities capabilities =
                                connectivityManager.getNetworkCapabilities(activeNetwork);
                        if (capabilities == null) {
                            result = -1;
                        } else {
                            result = exception.getErrorCode();
                        }
                        countDownLatch.countDown();
                    }
            );
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        OfflineWorkerUtil.removeProgressListener(progressListener);
        return (result == 0)? Result.success() : Result.failure(new Data.Builder()
                                                                        .putInt(OfflineWorkerUtil.OUTPUT_ERROR_KEY, result)
                                                                        .putString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL, errorMessage)
                                                                        .build());
    }
}
