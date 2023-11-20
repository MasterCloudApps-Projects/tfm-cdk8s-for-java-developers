package com.mycompany.app;

import imports.aws.k8s.services.FieldExport;
import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class DeploymentChart extends Chart {


    private static final int APP_PORT = 8080;
    private static final int LB_PORT = 9090;
    private String serviceAccountName;
    private String image;
    private FieldExport fieldExportForTable;
    private FieldExport fieldExportForRegion;

    private ConfigMap configMap;

    public DeploymentChart(final Construct scope, final String id, final ChartProps options,
                           final FieldExport fieldExportForTable, final FieldExport fieldExportForRegion, ConfigMap configMap) {
        super(scope, id, options);

        this.serviceAccountName = System.getenv("SERVICE_ACCOUNT");
        if (this.serviceAccountName == null || this.serviceAccountName.isEmpty()) {
            throw new RuntimeException("Missing env variable SERVICE_ACCOUNT");
        }

        this.image = System.getenv("DOCKER_IMAGE");
        if (this.image == null || this.image.isEmpty()) {
            throw new RuntimeException("Missing env variable DOCKER_IMAGE");
        }

        this.fieldExportForRegion = fieldExportForRegion;
        if (this.fieldExportForRegion == null) {
            throw new RuntimeException("Missing FieldExport for Region");
        }
        this.fieldExportForTable = fieldExportForTable;
        if (this.fieldExportForTable == null) {
            throw new RuntimeException("Missing FieldExport for Table");
        }

        this.configMap = configMap;
        if (this.configMap == null) {
            throw new RuntimeException("Missing configMap");
        }

        Deployment dep = new Deployment(this, "dynamodb-app-deployment", new DeploymentProps.Builder()
            .metadata(new ApiObjectMetadata.Builder()
                .name("dynamodb-app")
                .build())
            .serviceAccount(ServiceAccount.fromServiceAccountName(this, "aws-irsa", serviceAccountName))
            .build());

        Container container = dep.addContainer(new ContainerProps.Builder()
            .name("dynamodb-app-container")
            .image(image)
            .port(APP_PORT)
            .build());

        container.getEnv().addVariable("TABLE_NAME", EnvValue.fromConfigMap(configMap, "default." + fieldExportForTable.getName()));
        container.getEnv().addVariable("AWS_REGION", EnvValue.fromConfigMap(configMap, "default." + fieldExportForRegion.getName()));

        dep.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
            .name("dynamodb-app-service")
            .serviceType(ServiceType.LOAD_BALANCER)
            .ports(List.of(new ServicePort.Builder()
                .protocol(Protocol.TCP)
                .port(LB_PORT)
                .targetPort(APP_PORT)
                .build()))
            .build());
    }
}