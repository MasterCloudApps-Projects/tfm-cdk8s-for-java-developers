package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class DynamoDBAppChart extends Chart {

    public DynamoDBAppChart(final Construct scope, final String id, final AppChartProps props) {
        super(scope, id, props);

        Deployment dep = new Deployment(this, "dynamodb-app-deployment", new DeploymentProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("dynamodb-app")
                        .build())
                .serviceAccount(ServiceAccount.fromServiceAccountName(
                        this,
                        "aws-irsa",
                        props.getServiceAccountName()))
                .build());

        Container container = dep.addContainer(
                new ContainerProps.Builder()
                        .name("dynamodb-app-container")
                        .image(props.getImage())
                        .port(props.getAppPort())
                        .build());

        container.getEnv().addVariable("TABLE_NAME", EnvValue.fromValue(props.getTableName()));

        container.getEnv().addVariable("AWS_REGION", EnvValue.fromValue(props.getRegion()));

        dep.exposeViaService(
                new DeploymentExposeViaServiceOptions.Builder()
                        .name("dynamodb-app-service")
                        .serviceType(ServiceType.LOAD_BALANCER)
                        .ports(List.of(new ServicePort.Builder()
                                .protocol(Protocol.TCP)
                                .port(props.getLbPort())
                                .targetPort(props.getAppPort())
                                .build()))
                        .build());
    }
}