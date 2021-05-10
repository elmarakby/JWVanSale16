package com.labs.jwvansale16.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import com.sap.cloud.mobile.odata.offline.OfflineODataProviderOperationProgress;
import com.labs.jwvansale16.mdui.EntitySetListActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CountDownLatch;

public class OfflineSyncWorker extends OfflineBaseWorker {

    public OfflineSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        localContext = context;
    }

    private final int totalNumberOfSteps = 40;
    private int startPointForSync = 0;
    private int previousStep = 0;
    private final Context localContext;
    private static final Logger logger = LoggerFactory.getLogger(OfflineSyncWorker.class);

    private final OfflineProgressListener progressListener = new OfflineProgressListener() {
        @Override
        public void onOfflineProgress(@NonNull OfflineODataProvider provider,
                @NonNull OfflineODataProviderOperationProgress progress) {
            Intent intent = new Intent(localContext, EntitySetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(localContext, 0, intent, 0);
            if (progress.getCurrentStepNumber() > previousStep) {
                int currentStepNumber = totalNumberOfSteps / 2 * progress.getCurrentStepNumber() / progress.getTotalNumberOfSteps() + startPointForSync;
                previousStep = progress.getCurrentStepNumber();
                notificationManager.notify(
                        OFFLINE_NOTIFICATION_CHANNEL_INT_ID,
                        createNotification(totalNumberOfSteps, currentStepNumber, pendingIntent)
                );
            }
            if (progress.getCurrentStepNumber() == progress.getTotalNumberOfSteps()) {
                previousStep = 0;
                if (startPointForSync == 0) {
                    startPointForSync = totalNumberOfSteps / 2;
                } else {
                    notificationManager.cancel(OFFLINE_NOTIFICATION_CHANNEL_INT_ID);
                    startPointForSync = 0;
                }
            }
        }
    };

    private String errorMessage = null;
    @NonNull
    @Override
    public Result doWork() {
        OfflineWorkerUtil.addProgressListener(progressListener);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        if (OfflineWorkerUtil.getOfflineODataProvider() != null) {
            logger.info("Uploading data...");
            OfflineODataProvider provider = OfflineWorkerUtil.getOfflineODataProvider();
            provider.upload(
                () -> {
                    logger.info("Downloading data...");
                    provider.download(
                        countDownLatch::countDown,
                        exception -> {
                            countDownLatch.countDown();
                            errorMessage = (exception.getMessage() == null)? "Unknown offline sync error when downloading data." : exception.getMessage();
                            logger.error(errorMessage);
                        }
                    );
                },
                exception -> {
                    countDownLatch.countDown();
                    errorMessage = (exception.getMessage() == null)? "Unknown offline sync error when uploading data." : exception.getMessage();
                    logger.error(errorMessage);
                }
            );
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        OfflineWorkerUtil.removeProgressListener(progressListener);
        if (errorMessage != null) {
            logger.error("Offline sync error: " + errorMessage);
            notificationManager.cancel(OFFLINE_NOTIFICATION_CHANNEL_INT_ID);
            return Result.failure(new Data.Builder().putString(OfflineWorkerUtil.OUTPUT_ERROR_DETAIL, errorMessage).build());
        } else {
            return Result.success();
        }
    }
}