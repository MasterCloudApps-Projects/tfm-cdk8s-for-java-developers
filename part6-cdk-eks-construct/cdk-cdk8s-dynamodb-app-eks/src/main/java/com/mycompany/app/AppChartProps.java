package com.mycompany.app;

import org.cdk8s.ChartProps;
import software.constructs.Construct;

public class AppChartProps implements ChartProps {
    private String serviceAccountName;
    private String image;
    private int appPort;
    private int lbPort;
    private String tableName;
    private String region;

    public AppChartProps(String imageUri, String tableName) {

        this.serviceAccountName = System.getenv("SERVICE_ACCOUNT_NAME");
        if (this.serviceAccountName == null || this.serviceAccountName.isEmpty()) {
            throw new RuntimeException("Missing env variable SERVICE_ACCOUNT_NAME");
        }

        String appPortStr = System.getenv("APP_PORT");
        if (appPortStr == null || appPortStr.isEmpty()) {
            throw new RuntimeException("Missing env variable APP_PORT");
        }
        this.appPort = Integer.parseInt(appPortStr);

        String lbPortStr = System.getenv("LB_PORT");
        if (lbPortStr == null || lbPortStr.isEmpty()) {
            System.out.println("Missing env variable LB_PORT. Setting it to APP_PORT " + appPortStr);
            lbPortStr = appPortStr;
        }
        this.lbPort = Integer.parseInt(lbPortStr);

        this.region = System.getenv("AWS_REGION");
        if (this.region == null || this.region.isEmpty()) {
            throw new RuntimeException("Missing env variable AWS_REGION");
        }

        this.image = imageUri;
        this.tableName = tableName;
    }

    public String getServiceAccountName() {
        return serviceAccountName;
    }

    public void setServiceAccountName(String serviceAccountName) {
        this.serviceAccountName = serviceAccountName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    public int getLbPort() {
        return lbPort;
    }

    public void setLbPort(int lbPort) {
        this.lbPort = lbPort;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

