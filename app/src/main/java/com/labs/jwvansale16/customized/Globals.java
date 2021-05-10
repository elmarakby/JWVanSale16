package com.labs.jwvansale16.customized;

import com.labs.jwvansale16.viewmodel.driverdetails.DriverDetailsViewModel;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.DriverDetails;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.TotalMaterial;
import com.sap.cloud.android.odata.zgw_vansale_srv_entities.VisitDetails;

import java.util.List;

public class Globals {
    public static String user_name = "";
    public static String driver_no = "";
    public static  String active_customer = "";

    public static VisitDetails customersEntity = null;
    public static List<TotalMaterial> customerMaterialEntity = null;

    public static DriverDetailsViewModel driverDetailsViewModel;
    public static DriverDetails driverDetails;
    public static Boolean is_confirmed = false;
}
