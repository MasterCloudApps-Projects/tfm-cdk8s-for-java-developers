package com.mycompany.app;

public class ChartOptions {
    public boolean ingressEnabled;
    public String ingressHost;
    public String ingressServiceType;
    public boolean persistentVolumesCreate;
    public boolean persistentVolumesEnableStorageClass;
    public String persistentVolumesStorageClassesMysql;
    public String persistentVolumesStorageClassesMongo;
    public String persistentVolumesStorageClassesRabbit;
    public boolean networkPoliciesEnabled;
    public String servicesMongodbImage;
    public String servicesMongodbTag;
    public String servicesMysqlImage;
    public String servicesMysqlTag;
    public String servicesMysqlUsername;
    public String servicesMysqlPassword;
    public String servicesRabbitmqImage;
    public String servicesRabbitmqTag;
    public String servicesServerImage;
    public String servicesServerTag;
    public String servicesWorkerImage;
    public String servicesWorkerTag;
    public String servicesExternalImage;
    public String servicesExternalTag;
}
