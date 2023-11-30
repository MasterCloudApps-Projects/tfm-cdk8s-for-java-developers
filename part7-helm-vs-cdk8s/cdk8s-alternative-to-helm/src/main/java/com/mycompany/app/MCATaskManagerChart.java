package com.mycompany.app;

import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;

public class MCATaskManagerChart extends Chart
{

    public MCATaskManagerChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MCATaskManagerChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        // define resources here

        ChartOptions chartOptions = new ChartOptions();

        chartOptions.ingressEnabled = true;
        chartOptions.ingressHost = "cluster-ip";
        chartOptions.ingressServiceType = "NodePort";
        chartOptions.persistentVolumesCreate = true;
        chartOptions.persistentVolumesEnableStorageClass = true;
        chartOptions.persistentVolumesStorageClassesMysql = "mysql";
        chartOptions.persistentVolumesStorageClassesMongo = "mongodb";
        chartOptions.persistentVolumesStorageClassesRabbit = "rabbitmq";
        chartOptions.networkPoliciesEnabled = false;
        chartOptions.servicesMongodbImage = "mongo";
        chartOptions.servicesMongodbTag = "4.2.3";
        chartOptions.servicesMysqlImage = "mysql";
        chartOptions.servicesMysqlTag = "8";
        chartOptions.servicesMysqlUsername = "myuser";
        chartOptions.servicesMysqlPassword = "";
        chartOptions.servicesRabbitmqImage = "fjvela/urjc-fjvela-rabbitmq";
        chartOptions.servicesRabbitmqTag = "1.0.0";
        chartOptions.servicesServerImage = "fjvela/urjc-fjvela-server";
        chartOptions.servicesServerTag = "1.0.5";
        chartOptions.servicesWorkerImage = "torrespro/mca-worker";
        chartOptions.servicesWorkerTag = "2.0.0";
        chartOptions.servicesExternalImage = "fjvela/urjc-fjvela-external-service";
        chartOptions.servicesExternalTag = "1.0.1";

        new WebService(this, "rabbitmq", WebServiceProps.builder()
                .image("fjvela/urjc-fjvela-rabbitmq")
                .tag("1.0.0")
                .replicas(2)
                .build());

        new WebService(this, "mysql", WebServiceProps.builder()
                .image("mysql")
                .tag("8")
                .replicas(2)
                .build());

        new WebService(this, "mongodb", WebServiceProps.builder()
                .image("mongo")
                .tag("423")
                .replicas(2)
                .build());

        new WebService(this, "server", WebServiceProps.builder()
                .image("fjvela/urjc-fjvela-server")
                .tag("1.0.5")
                .containerPort(2368)
                .build());

        new WebService(this, "worker", WebServiceProps.builder()
                .image("torrespro/mca-worker")
                .tag("2.0.0")
                .replicas(2)
                .build());

        new WebService(this, "external", WebServiceProps.builder()
                .image("urjc-fjvela-external-service")
                .tag("1.0.1")
                .containerPort(2368)
                .build());

    }

    public static void main(String[] args) {
        final App app = new App();
        new MCATaskManagerChart(app, "cdk8s-alternative-to-helm");
        app.synth();
    }
}