package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.ChartProps;
import org.cdk8s.plus27.*;
import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;

import java.util.List;

public class NginxChart extends Chart {

    public NginxChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public NginxChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        Deployment deployment = new Deployment(this, "deployment", new DeploymentProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("nginx-deployment-cdk8s-plus")
                        .build())
                .build());


        deployment.addContainer(new ContainerProps.Builder()
                .name("nginx-container")
                .image("nginx:1.19.10")
                .ports(List.of(ContainerPort.builder().number(80).build()))
                .build());

        deployment.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
                .name("nginx-container-service")
                .serviceType(ServiceType.LOAD_BALANCER)
                .ports(List.of(new ServicePort.Builder()
                        .port(9090)
                        .targetPort(80)
                        .build()))
                .build());
    }

    public static void main(String[] args) {
        final App app = new App();
        new NginxChart(app, "nginx");
        app.synth();
    }
}
