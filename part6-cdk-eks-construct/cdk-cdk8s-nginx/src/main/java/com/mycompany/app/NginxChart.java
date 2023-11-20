package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Chart;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class NginxChart extends Chart {

    public NginxChart(final Construct scope, final String id, final MyChartProps props) {
        super(scope, id, null);

        Deployment dep = new Deployment(this, "nginx-deployment", new DeploymentProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("nginx-deployment-cdk8s")
                        .build())
                .build());

        dep.addContainer(
                new ContainerProps.Builder()
                        .name("nginx-container")
                        .image("nginx")
                        .port(80)
                        .build());

        dep.exposeViaService(
                new DeploymentExposeViaServiceOptions.Builder()
                        .name("nginx-service-cdk8s")
                        .serviceType(ServiceType.LOAD_BALANCER)
                        .ports(List.of(new ServicePort.Builder()
                                .port(9090)
                                .targetPort(80)
                                .build()))
                        .build());
    }
}
