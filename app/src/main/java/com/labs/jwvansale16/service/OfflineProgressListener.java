package com.labs.jwvansale16.service;

import com.sap.cloud.mobile.odata.offline.OfflineODataProvider;
import com.sap.cloud.mobile.odata.offline.OfflineODataProviderOperationProgress;

import androidx.annotation.NonNull;

public interface OfflineProgressListener {
    void onOfflineProgress(@NonNull OfflineODataProvider var1, @NonNull OfflineODataProviderOperationProgress var2);
}
