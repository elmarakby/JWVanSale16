package com.labs.jwvansale16.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_Entities;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ZGW_VANSALE_SRV_EntitiesMetadata.EntitySets;

import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ActionCancelVisit;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ActionCheckInMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.ActionVisitOperations;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CancelVisit;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CheckInMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CheckoutMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.Collections;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.CustomerVisitPlan;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.DriverDetails;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.FreeMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.MasterMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.OpenBilling;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA005;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA904;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA905;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA906;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesA908;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.PricesKonp;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.TotalMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitDetails;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitOperationsHeader;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitOperationsItems;

import com.sap.cloud.mobile.odata.EntitySet;
import com.sap.cloud.mobile.odata.Property;
import com.labs.jwvansale16.service.OfflineWorkerUtil;

import java.util.WeakHashMap;

/*
 * Repository factory to construct repository for an entity set
 */
public class RepositoryFactory {

    /*
     * Cache all repositories created to avoid reconstruction and keeping the entities of entity set
     * maintained by each repository in memory. Use a weak hash map to allow recovery in low memory
     * conditions
     */
    private WeakHashMap<String, Repository> repositories;
    /**
     * Construct a RepositoryFactory instance. There should only be one repository factory and used
     * throughout the life of the application to avoid caching entities multiple times.
     */
    public RepositoryFactory() {
        repositories = new WeakHashMap<>();
    }

    /**
     * Construct or return an existing repository for the specified entity set
     * @param entitySet - entity set for which the repository is to be returned
     * @param orderByProperty - if specified, collection will be sorted ascending with this property
     * @return a repository for the entity set
     */
    public Repository getRepository(@NonNull EntitySet entitySet, @Nullable Property orderByProperty) {
        ZGW_VANSALE_SRV_Entities zGW_VANSALE_SRV_Entities = OfflineWorkerUtil.getZGW_VANSALE_SRV_Entities();
        String key = entitySet.getLocalName();
        Repository repository = repositories.get(key);
        if (repository == null) {
            if (key.equals(EntitySets.actionCancelVisitSet.getLocalName())) {
                repository = new Repository<ActionCancelVisit>(zGW_VANSALE_SRV_Entities, EntitySets.actionCancelVisitSet, orderByProperty);
            } else if (key.equals(EntitySets.actionCheckInMaterialSet.getLocalName())) {
                repository = new Repository<ActionCheckInMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.actionCheckInMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.actionVisitOperationsSet.getLocalName())) {
                repository = new Repository<ActionVisitOperations>(zGW_VANSALE_SRV_Entities, EntitySets.actionVisitOperationsSet, orderByProperty);
            } else if (key.equals(EntitySets.cancelVisitSet.getLocalName())) {
                repository = new Repository<CancelVisit>(zGW_VANSALE_SRV_Entities, EntitySets.cancelVisitSet, orderByProperty);
            } else if (key.equals(EntitySets.checkInMaterialSet.getLocalName())) {
                repository = new Repository<CheckInMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.checkInMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.checkoutMaterialSet.getLocalName())) {
                repository = new Repository<CheckoutMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.checkoutMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.collectionsSet.getLocalName())) {
                repository = new Repository<Collections>(zGW_VANSALE_SRV_Entities, EntitySets.collectionsSet, orderByProperty);
            } else if (key.equals(EntitySets.customerVisitPlanSet.getLocalName())) {
                repository = new Repository<CustomerVisitPlan>(zGW_VANSALE_SRV_Entities, EntitySets.customerVisitPlanSet, orderByProperty);
            } else if (key.equals(EntitySets.driverDetailsSet.getLocalName())) {
                repository = new Repository<DriverDetails>(zGW_VANSALE_SRV_Entities, EntitySets.driverDetailsSet, orderByProperty);
            } else if (key.equals(EntitySets.freeMaterialSet.getLocalName())) {
                repository = new Repository<FreeMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.freeMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.masterMaterialSet.getLocalName())) {
                repository = new Repository<MasterMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.masterMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.openBillingSet.getLocalName())) {
                repository = new Repository<OpenBilling>(zGW_VANSALE_SRV_Entities, EntitySets.openBillingSet, orderByProperty);
            } else if (key.equals(EntitySets.pricesA005Set.getLocalName())) {
                repository = new Repository<PricesA005>(zGW_VANSALE_SRV_Entities, EntitySets.pricesA005Set, orderByProperty);
            } else if (key.equals(EntitySets.pricesA904Set.getLocalName())) {
                repository = new Repository<PricesA904>(zGW_VANSALE_SRV_Entities, EntitySets.pricesA904Set, orderByProperty);
            } else if (key.equals(EntitySets.pricesA905Set.getLocalName())) {
                repository = new Repository<PricesA905>(zGW_VANSALE_SRV_Entities, EntitySets.pricesA905Set, orderByProperty);
            } else if (key.equals(EntitySets.pricesA906Set.getLocalName())) {
                repository = new Repository<PricesA906>(zGW_VANSALE_SRV_Entities, EntitySets.pricesA906Set, orderByProperty);
            } else if (key.equals(EntitySets.pricesA908Set.getLocalName())) {
                repository = new Repository<PricesA908>(zGW_VANSALE_SRV_Entities, EntitySets.pricesA908Set, orderByProperty);
            } else if (key.equals(EntitySets.pricesKonpSet.getLocalName())) {
                repository = new Repository<PricesKonp>(zGW_VANSALE_SRV_Entities, EntitySets.pricesKonpSet, orderByProperty);
            } else if (key.equals(EntitySets.totalMaterialSet.getLocalName())) {
                repository = new Repository<TotalMaterial>(zGW_VANSALE_SRV_Entities, EntitySets.totalMaterialSet, orderByProperty);
            } else if (key.equals(EntitySets.visitDetailsSet.getLocalName())) {
                repository = new Repository<VisitDetails>(zGW_VANSALE_SRV_Entities, EntitySets.visitDetailsSet, orderByProperty);
            } else if (key.equals(EntitySets.visitOperationsHeaderSet.getLocalName())) {
                repository = new Repository<VisitOperationsHeader>(zGW_VANSALE_SRV_Entities, EntitySets.visitOperationsHeaderSet, orderByProperty);
            } else if (key.equals(EntitySets.visitOperationsItemsSet.getLocalName())) {
                repository = new Repository<VisitOperationsItems>(zGW_VANSALE_SRV_Entities, EntitySets.visitOperationsItemsSet, orderByProperty);
            } else {
                throw new AssertionError("Fatal error, entity set[" + key + "] missing in generated code");
            }
            repositories.put(key, repository);
        }
        return repository;
    }

    /**
     * Get rid of all cached repositories
     */
    public void reset() {
        repositories.clear();
    }
 }
