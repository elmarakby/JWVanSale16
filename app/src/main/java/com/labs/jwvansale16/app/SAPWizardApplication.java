package com.labs.jwvansale16.app;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler;
import com.sap.cloud.mobile.foundation.model.AppConfig;
import com.labs.jwvansale16.service.OfflineWorkerUtil;
import com.sap.cloud.mobile.foundation.settings.SharedDeviceService;
import com.labs.jwvansale16.repository.RepositoryFactory;
import com.sap.cloud.mobile.foundation.mobileservices.MobileService;
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer;
import java.util.ArrayList;
import java.util.List;
import com.sap.cloud.mobile.foundation.logging.Logging;
import com.sap.cloud.mobile.foundation.logging.LogService;
import ch.qos.logback.classic.Level;
import com.sap.cloud.mobile.foundation.usage.UsageService;

public class SAPWizardApplication extends Application {

    /**
     * The application configuration information.
     */
    private AppConfig appConfig;
    public boolean isApplicationUnlocked = false;
    public static SharedPreferences sp;

    public static final String KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference";
    private static final String OFFLINE_APP_ENCRYPTION_CONSTANT = "34dab53fc060450280faeed44a36571b";

    /**
     * Application-wide RepositoryFactory
     */
    private RepositoryFactory repositoryFactory;
    /**
     * Returns the application-wide repository factory
     *
     * @return the repository factory
     */
    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    /**
     * Clears all user-specific data from the application, essentially resetting
     * it to its initial state.
     *
     * If client code wants to handle the reset logic of a service, here is an example:
     * 
     *     SDKInitializer.INSTANCE.resetServices( service -> {
     *             if(service instanceof PushService) {
     *                 PushService.unregisterPushSync(new RemoteNotificationClient.CallbackListener() {
     *                     @Override
     *                     public void onSuccess() {
     *
     *                     }
     *
     *                     @Override
     *                     public void onError(@NonNull Throwable throwable) {
     *
     *                     }
     *                 });
     *                 return true;
     *             } else {
     *                 return false;
     *             }
     *         });
     */
    public void resetApp() {
        sp.edit().clear().apply();
        appConfig = null;
        isApplicationUnlocked = false;
        repositoryFactory.reset();
        SDKInitializer.INSTANCE.resetServices(null);
        OfflineWorkerUtil.resetOffline(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        registerActivityLifecycleCallbacks(AppLifecycleCallbackHandler.getInstance());
        repositoryFactory = new RepositoryFactory();
        initService();
        clearConsentStatus();

    }

    private void clearConsentStatus() {
        sp.edit().putBoolean(WizardFlowStateListener.USAGE_SERVICE_PRE, false).apply();

    }
    private void initService() {
        List<MobileService> services = new ArrayList<>();
        services.add(new UsageService());
        Logging.setConfigurationBuilder(new Logging.ConfigurationBuilder().initialLevel(Level.WARN).logToConsole(true).build());
        services.add(new LogService());

        services.add(new SharedDeviceService(OFFLINE_APP_ENCRYPTION_CONSTANT));

        SDKInitializer.INSTANCE.start(this, services.toArray(new MobileService[0]), null);
    }


    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
}
