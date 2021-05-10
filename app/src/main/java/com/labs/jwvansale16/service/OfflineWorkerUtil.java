package com.labs.jwvansale16.service;

import com.labs.jwvansale16.customized.Globals;
import com.sap.cloud.mobile.odata.offline.OfflineODataDefiningQuery;
import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import com.sap.cloud.mobile.odata.offline.OfflineODataProviderOperationProgress;
import android.content.Context;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.work.*;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_Entities;
import com.sap.cloud.mobile.flowv2.core.FlowContextRegistry;
import com.sap.cloud.mobile.flowv2.securestore.UserSecureStoreDelegate;
import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.foundation.common.EncryptionUtil;
import com.sap.cloud.mobile.foundation.model.AppConfig;
import com.sap.cloud.mobile.odata.core.AndroidSystem;
import com.sap.cloud.mobile.odata.core.Logger;
import com.sap.cloud.mobile.odata.core.LoggerFactory;
import com.sap.cloud.mobile.odata.offline.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OfflineWorkerUtil {
    private static final OfflineWorkerUtil instance = new OfflineWorkerUtil();
    private OfflineWorkerUtil() {}
    public static OfflineWorkerUtil getInstance() {
        return instance;
    }

    /*
     * Offline OData Provider
     */
    private static OfflineODataProvider offlineODataProvider = null;

    private static boolean isUserSwitch = false;

    public static OfflineODataProvider getOfflineODataProvider() {
        return offlineODataProvider;
    }

    public static void resetOfflineODataProvider() {
        offlineODataProvider = null;
    }
    private static final Logger logger = LoggerFactory.getLogger(OfflineWorkerUtil.class.toString());
    private static final HashSet<OfflineProgressListener> progressListeners = new HashSet<>();
    public static final String OFFLINE_OPEN_WORKER_UNIQUE_NAME = "offline_init_sync_worker";

    public static final String OFFLINE_SYNC_WORKER_UNIQUE_NAME = "offline_sync_worker";

    public static final String OFFLINE_WORKER_OPEN_TAG = "offline_init_sync_tag";

    public static final String OFFLINE_WORKER_SYNC_TAG = "offline_sync_tag";

    public static final String INPUT_DATA_APP_CONFIG = "input.app.config";

    public static final String IS_USER_SWITCH = "offline_open_check_if_user_switched";

    public static final String OUTPUT_ERROR_KEY = "output.error";

    public static final String OUTPUT_ERROR_DETAIL = "output.error.detail";

    /** Name of the offline data file on the application file space */
    private static final String OFFLINE_DATASTORE = "OfflineDataStore";

    private static final String OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS = "Offline_DataStore_EncryptionKey_Alias";

    /** Header name for application version */
    private static final String APP_VERSION_HEADER = "X-APP-VERSION";

    /** The preference to say whether offline is initialized. */
    public static final String PREF_OFFLINE_INITIALIZED = "pref.offline.db.initialized";

    /*
     * OData service for interacting with local OData Provider
     */
    private static ZGW_VANSALE_SRV_Entities zGW_VANSALE_SRV_Entities;

    @NonNull
    public static ZGW_VANSALE_SRV_Entities getZGW_VANSALE_SRV_Entities() {
        if (zGW_VANSALE_SRV_Entities == null) {
            throw new NullPointerException("zGW_VANSALE_SRV_Entities was not initialized.");
        }
        return zGW_VANSALE_SRV_Entities;
    }
    /*
     * Connection ID of Mobile Application
     */
    public static final String CONNECTION_ID_ZGW_VANSALE_SRV_ENTITIES = "JasmineDevHttpNew";

    public static void addProgressListener(@NonNull OfflineProgressListener listener) {
        progressListeners.add(listener);
    }

    public static void removeProgressListener(@NonNull OfflineProgressListener listener) {
        progressListeners.remove(listener);
    }

    private static final OfflineODataProviderDelegate delegate = new OfflineODataProviderDelegate() {
        @Override
        public void updateOpenProgress(@NonNull OfflineODataProvider offlineODataProvider,
                @NonNull OfflineODataProviderOperationProgress offlineODataProviderOperationProgress) {
            notifyListeners(offlineODataProvider, offlineODataProviderOperationProgress);
        }

        @Override
        public void updateDownloadProgress(@NonNull OfflineODataProvider offlineODataProvider,
                @NonNull OfflineODataProviderDownloadProgress offlineODataProviderDownloadProgress) {
            notifyListeners(offlineODataProvider, offlineODataProviderDownloadProgress);
        }

        @Override
        public void updateUploadProgress(@NonNull OfflineODataProvider offlineODataProvider,
                @NonNull OfflineODataProviderOperationProgress offlineODataProviderOperationProgress) {
            notifyListeners(offlineODataProvider, offlineODataProviderOperationProgress);
        }

        @Override
        public void updateFailedRequest(@NonNull OfflineODataProvider offlineODataProvider,
                @NonNull OfflineODataFailedRequest offlineODataFailedRequest) {

        }

        @Override
        public void updateSendStoreProgress(@NonNull OfflineODataProvider offlineODataProvider,
                @NonNull OfflineODataProviderOperationProgress offlineODataProviderOperationProgress) {
            notifyListeners(offlineODataProvider, offlineODataProviderOperationProgress);
        }

        private void notifyListeners(
                OfflineODataProvider offlineODataProvider,
            OfflineODataProviderOperationProgress offlineODataProviderOperationProgress
        ) {
            logger.debug("Progress " + offlineODataProviderOperationProgress.getCurrentStepNumber()
                    + " out of " + offlineODataProviderOperationProgress.getTotalNumberOfSteps());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                for (OfflineProgressListener progressListener : progressListeners) {
                    progressListener.onOfflineProgress(offlineODataProvider,
                            offlineODataProviderOperationProgress);
                }
            });
            executor.shutdown();
        }
    };

    /*
     * Create OfflineODataProvider
     * This is a blocking call, no data will be transferred until open, download, upload
     */
    public static void initializeOffline(
        Context context,
        AppConfig appConfig,
        boolean runtimeMultipleUserMode
    ) {
        if (offlineODataProvider != null) return;
        if (FlowContextRegistry.getFlowContext().getCurrentUserId() == null ||
                FlowContextRegistry.getFlowContext().getCurrentUserId().isEmpty())
            throw new IllegalStateException("Current user not ready yet.");

        Globals.user_name = FlowContextRegistry.getFlowContext().getCurrentUserId();
        AndroidSystem.setContext(context);
        String serviceUrl = appConfig.getServiceUrl();
        try {
            URL url = new URL(serviceUrl + CONNECTION_ID_ZGW_VANSALE_SRV_ENTITIES);
            OfflineODataParameters offlineODataParameters = new OfflineODataParameters();
            offlineODataParameters.setEnableRepeatableRequests(true);
            offlineODataParameters.setStoreName(OFFLINE_DATASTORE);
            offlineODataParameters.setCurrentUser(FlowContextRegistry.getFlowContext().getCurrentUserId());
            offlineODataParameters.setForceUploadOnUserSwitch(runtimeMultipleUserMode);
            offlineODataParameters.setTimeout(5);
            String encryptionKey = UserSecureStoreDelegate.getInstance().getOfflineEncryptionKey();
            String key;
            if (encryptionKey == null) {
                byte[] encryptionKeyBytes = EncryptionUtil.getEncryptionKey(OFFLINE_DATASTORE_ENCRYPTION_KEY_ALIAS);
                String defaultKey = Base64.encodeToString(encryptionKeyBytes, Base64.NO_WRAP);
                Arrays.fill(encryptionKeyBytes, (byte)0);
                key = defaultKey;
            } else {
                key = encryptionKey;
            }
            offlineODataParameters.setStoreEncryptionKey(key);

            // Set the default application version
            Map customHeaders = offlineODataParameters.getCustomHeaders();
            customHeaders.put(APP_VERSION_HEADER, appConfig.getApplicationVersion());
            // In case of offlineODataParameters.customHeaders returning a new object if customHeaders from offlineODataParameters is null, set again as below
            offlineODataParameters.setCustomHeaders(customHeaders);

            offlineODataProvider = new OfflineODataProvider(url, offlineODataParameters, ClientProvider.get(), delegate);
//            OfflineODataDefiningQuery actionCancelVisitSetQuery = new OfflineODataDefiningQuery("Action_CancelVisitSet", "Action_CancelVisitSet", false);
//            offlineODataProvider.addDefiningQuery(actionCancelVisitSetQuery);
//            OfflineODataDefiningQuery actionCheckInMaterialSetQuery = new OfflineODataDefiningQuery("Action_CheckInMaterialSet", "Action_CheckInMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(actionCheckInMaterialSetQuery);
//            OfflineODataDefiningQuery actionVisitOperationsSetQuery = new OfflineODataDefiningQuery("Action_VisitOperationsSet", "Action_VisitOperationsSet", false);
//            offlineODataProvider.addDefiningQuery(actionVisitOperationsSetQuery);
//            OfflineODataDefiningQuery cancelVisitSetQuery = new OfflineODataDefiningQuery("CancelVisitSet", "CancelVisitSet", false);
//            offlineODataProvider.addDefiningQuery(cancelVisitSetQuery);
//            OfflineODataDefiningQuery checkInMaterialSetQuery = new OfflineODataDefiningQuery("CheckInMaterialSet", "CheckInMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(checkInMaterialSetQuery);
//            OfflineODataDefiningQuery checkoutMaterialSetQuery = new OfflineODataDefiningQuery("CheckoutMaterialSet", "CheckoutMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(checkoutMaterialSetQuery);
//            OfflineODataDefiningQuery collectionsSetQuery = new OfflineODataDefiningQuery("CollectionsSet", "CollectionsSet", false);
//            offlineODataProvider.addDefiningQuery(collectionsSetQuery);
//            OfflineODataDefiningQuery customerVisitPlanSetQuery = new OfflineODataDefiningQuery("Customer_VisitPlanSet", "Customer_VisitPlanSet", false);
//            offlineODataProvider.addDefiningQuery(customerVisitPlanSetQuery);
//            OfflineODataDefiningQuery driverDetailsSetQuery = new OfflineODataDefiningQuery("DriverDetailsSet", "DriverDetailsSet", false);
//            offlineODataProvider.addDefiningQuery(driverDetailsSetQuery);
//            OfflineODataDefiningQuery freeMaterialSetQuery = new OfflineODataDefiningQuery("FreeMaterialSet", "FreeMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(freeMaterialSetQuery);
//            OfflineODataDefiningQuery masterMaterialSetQuery = new OfflineODataDefiningQuery("MasterMaterialSet", "MasterMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(masterMaterialSetQuery);
//            OfflineODataDefiningQuery openBillingSetQuery = new OfflineODataDefiningQuery("OpenBillingSet", "OpenBillingSet", false);
//            offlineODataProvider.addDefiningQuery(openBillingSetQuery);
//            OfflineODataDefiningQuery pricesA005SetQuery = new OfflineODataDefiningQuery("Prices_A005Set", "Prices_A005Set", false);
//            offlineODataProvider.addDefiningQuery(pricesA005SetQuery);
//            OfflineODataDefiningQuery pricesA904SetQuery = new OfflineODataDefiningQuery("Prices_A904Set", "Prices_A904Set", false);
//            offlineODataProvider.addDefiningQuery(pricesA904SetQuery);
//            OfflineODataDefiningQuery pricesA905SetQuery = new OfflineODataDefiningQuery("Prices_A905Set", "Prices_A905Set", false);
//            offlineODataProvider.addDefiningQuery(pricesA905SetQuery);
//            OfflineODataDefiningQuery pricesA906SetQuery = new OfflineODataDefiningQuery("Prices_A906Set", "Prices_A906Set", false);
//            offlineODataProvider.addDefiningQuery(pricesA906SetQuery);
//            OfflineODataDefiningQuery pricesA908SetQuery = new OfflineODataDefiningQuery("Prices_A908Set", "Prices_A908Set", false);
//            offlineODataProvider.addDefiningQuery(pricesA908SetQuery);
//            OfflineODataDefiningQuery pricesKonpSetQuery = new OfflineODataDefiningQuery("Prices_KonpSet", "Prices_KonpSet", false);
//            offlineODataProvider.addDefiningQuery(pricesKonpSetQuery);
//            OfflineODataDefiningQuery totalMaterialSetQuery = new OfflineODataDefiningQuery("TotalMaterialSet", "TotalMaterialSet", false);
//            offlineODataProvider.addDefiningQuery(totalMaterialSetQuery);
//            OfflineODataDefiningQuery visitDetailsSetQuery = new OfflineODataDefiningQuery("VisitDetailsSet", "VisitDetailsSet", false);
//            offlineODataProvider.addDefiningQuery(visitDetailsSetQuery);
//            OfflineODataDefiningQuery visitOperationsHeaderSetQuery = new OfflineODataDefiningQuery("VisitOperationsHeaderSet", "VisitOperationsHeaderSet", false);
//            offlineODataProvider.addDefiningQuery(visitOperationsHeaderSetQuery);
//            OfflineODataDefiningQuery visitOperationsItemsSetQuery = new OfflineODataDefiningQuery("VisitOperationsItemsSet", "VisitOperationsItemsSet", false);
//            offlineODataProvider.addDefiningQuery(visitOperationsItemsSetQuery);
            OfflineODataDefiningQuery actionCancelVisitSetQuery = new OfflineODataDefiningQuery("Action_CancelVisitSet", "Action_CancelVisitSet", false);
            offlineODataProvider.addDefiningQuery(actionCancelVisitSetQuery);
            OfflineODataDefiningQuery actionCheckInMaterialSetQuery = new OfflineODataDefiningQuery("Action_CheckInMaterialSet", "Action_CheckInMaterialSet", false);
            offlineODataProvider.addDefiningQuery(actionCheckInMaterialSetQuery);
            OfflineODataDefiningQuery actionVisitOperationsSetQuery = new OfflineODataDefiningQuery("Action_VisitOperationsSet", "Action_VisitOperationsSet", false);
            offlineODataProvider.addDefiningQuery(actionVisitOperationsSetQuery);
            OfflineODataDefiningQuery cancelVisitSetQuery = new OfflineODataDefiningQuery("CancelVisitSet", "CancelVisitSet", false);
            offlineODataProvider.addDefiningQuery(cancelVisitSetQuery);
            OfflineODataDefiningQuery checkInMaterialSetQuery = new OfflineODataDefiningQuery("CheckInMaterialSet", "CheckInMaterialSet", false);
            offlineODataProvider.addDefiningQuery(checkInMaterialSetQuery);
            OfflineODataDefiningQuery checkoutMaterialSetQuery = new OfflineODataDefiningQuery("CheckoutMaterialSet", "CheckoutMaterialSet", false);
            OfflineODataDefiningQuery checkoutMaterialSetQuery2 = new OfflineODataDefiningQuery("CheckoutMaterialSet", "CheckoutMaterialSet?$filter=Userid eq '" + Globals.user_name + "'", false);
            offlineODataProvider.addDefiningQuery(checkoutMaterialSetQuery2);
            OfflineODataDefiningQuery collectionsSetQuery = new OfflineODataDefiningQuery("CollectionsSet", "CollectionsSet", false);
            offlineODataProvider.addDefiningQuery(collectionsSetQuery);
            OfflineODataDefiningQuery customerVisitPlanSetQuery = new OfflineODataDefiningQuery("Customer_VisitPlanSet", "Customer_VisitPlanSet", false);
            OfflineODataDefiningQuery customerVisitPlanSetQuery2 = new OfflineODataDefiningQuery("Customer_VisitPlanSet", "Customer_VisitPlanSet?$filter=Userid eq '" + Globals.user_name + "'", false);
            offlineODataProvider.addDefiningQuery(customerVisitPlanSetQuery2);
            OfflineODataDefiningQuery driverDetailsSetQuery = new OfflineODataDefiningQuery("DriverDetailsSet", "DriverDetailsSet", false);
            OfflineODataDefiningQuery driverDetailsSetQuery2 = new OfflineODataDefiningQuery("DriverDetailsSet", "DriverDetailsSet?$filter=Userid eq '" + Globals.user_name + "'", false);
            offlineODataProvider.addDefiningQuery(driverDetailsSetQuery2);
            OfflineODataDefiningQuery freeMaterialSetQuery = new OfflineODataDefiningQuery("FreeMaterialSet", "FreeMaterialSet", false);
            offlineODataProvider.addDefiningQuery(freeMaterialSetQuery);
            OfflineODataDefiningQuery masterMaterialSetQuery = new OfflineODataDefiningQuery("MasterMaterialSet", "MasterMaterialSet", false);
            offlineODataProvider.addDefiningQuery(masterMaterialSetQuery);
            OfflineODataDefiningQuery openBillingSetQuery = new OfflineODataDefiningQuery("OpenBillingSet", "OpenBillingSet", false);
            offlineODataProvider.addDefiningQuery(openBillingSetQuery);
            OfflineODataDefiningQuery pricesA005SetQuery = new OfflineODataDefiningQuery("Prices_A005Set", "Prices_A005Set", false);
            offlineODataProvider.addDefiningQuery(pricesA005SetQuery);
            OfflineODataDefiningQuery pricesA904SetQuery = new OfflineODataDefiningQuery("Prices_A904Set", "Prices_A904Set", false);
            offlineODataProvider.addDefiningQuery(pricesA904SetQuery);
            OfflineODataDefiningQuery pricesA905SetQuery = new OfflineODataDefiningQuery("Prices_A905Set", "Prices_A905Set", false);
            offlineODataProvider.addDefiningQuery(pricesA905SetQuery);
            OfflineODataDefiningQuery pricesA906SetQuery = new OfflineODataDefiningQuery("Prices_A906Set", "Prices_A906Set", false);
            offlineODataProvider.addDefiningQuery(pricesA906SetQuery);
            OfflineODataDefiningQuery pricesA908SetQuery = new OfflineODataDefiningQuery("Prices_A908Set", "Prices_A908Set", false);
            offlineODataProvider.addDefiningQuery(pricesA908SetQuery);
            OfflineODataDefiningQuery pricesKonpSetQuery = new OfflineODataDefiningQuery("Prices_KonpSet", "Prices_KonpSet", false);
            offlineODataProvider.addDefiningQuery(pricesKonpSetQuery);
            OfflineODataDefiningQuery totalMaterialSetQuery = new OfflineODataDefiningQuery("TotalMaterialSet", "TotalMaterialSet", false);
            offlineODataProvider.addDefiningQuery(totalMaterialSetQuery);
            OfflineODataDefiningQuery visitDetailsSetQuery = new OfflineODataDefiningQuery("VisitDetailsSet", "VisitDetailsSet", false);
            OfflineODataDefiningQuery visitDetailsSetQuery2 = new OfflineODataDefiningQuery("VisitDetailsSet", "VisitDetailsSet?$filter=Userid eq '" + Globals.user_name + "'", false);
            offlineODataProvider.addDefiningQuery(visitDetailsSetQuery2);
            OfflineODataDefiningQuery visitOperationsHeaderSetQuery = new OfflineODataDefiningQuery("VisitOperationsHeaderSet", "VisitOperationsHeaderSet", false);
            offlineODataProvider.addDefiningQuery(visitOperationsHeaderSetQuery);
            OfflineODataDefiningQuery visitOperationsItemsSetQuery = new OfflineODataDefiningQuery("VisitOperationsItemsSet", "VisitOperationsItemsSet", false);
            offlineODataProvider.addDefiningQuery(visitOperationsItemsSetQuery);

            zGW_VANSALE_SRV_Entities = new ZGW_VANSALE_SRV_Entities(offlineODataProvider);
        } catch (Exception e) {
            logger.error("Exception encountered setting up offline store: " + e.getMessage());
        }
    }

    /*
     * Close and remove offline data store
     */
    public static void resetOffline(Context context) {
        try {
            AndroidSystem.setContext(context);
            if (offlineODataProvider != null) {
                offlineODataProvider.close();
            }
            OfflineODataProvider.clear(OFFLINE_DATASTORE);
        } catch (OfflineODataException e) {
            logger.error("Unable to reset Offline Data Store. Encountered exception: " + e.getMessage());
        } finally {
            offlineODataProvider = null;
        }
        progressListeners.clear();
    }

    public static void reOpen(Context context,
            AppConfig appConfig) {
        open(context, appConfig, isUserSwitch);
    }

    public static void open(
        Context context,
        AppConfig appConfig,
        boolean userSwitchFlag
    ) {
        isUserSwitch = userSwitchFlag;
        if (FlowContextRegistry.getFlowContext().getCurrentUserId() == null)
            throw new IllegalStateException("Current user not ready yet.");

        Constraints constraints = new Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build();

        Map<String, Object> map = new HashMap();
        map.put(INPUT_DATA_APP_CONFIG, appConfig.toString());
        map.put(IS_USER_SWITCH, isUserSwitch);

        OneTimeWorkRequest openRequest = new OneTimeWorkRequest.Builder(OfflineOpenWorker.class)
            .setConstraints(constraints)
            .addTag(OFFLINE_WORKER_OPEN_TAG)
            .setInputData(new Data.Builder().putAll(map).build())
            .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
            OFFLINE_OPEN_WORKER_UNIQUE_NAME,
            ExistingWorkPolicy.KEEP,
            openRequest
        );
    }

    /**
     * Synchronize local offline data store with Server
     * Upload - local changes
     * Download - server changes
     */
    public static void sync(Context context) {
        Constraints constraints = new Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build();

        OneTimeWorkRequest openRequest = new OneTimeWorkRequest.Builder(OfflineSyncWorker.class)
            .setConstraints(constraints)
            .addTag(OFFLINE_WORKER_SYNC_TAG)
            .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
            OFFLINE_SYNC_WORKER_UNIQUE_NAME,
            ExistingWorkPolicy.KEEP,
            openRequest
        );
    }
}
