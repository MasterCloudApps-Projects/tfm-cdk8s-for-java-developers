package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.plus25.*;
import org.cdk8s.plus25.ContainerPort;
import org.cdk8s.plus25.ServicePort;
import org.jetbrains.annotations.NotNull;
import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebService extends Construct {

    private Deployment deployment;

    public WebService(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public WebService(final Construct scope, final String id, final WebServiceProps props) {
        super(scope, id);

        Deployment deployment = new Deployment(this, "deployment", DeploymentProps.builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("deployment")
                        .build())
                .build());

        deployment.addContainer(getContainer(props));

        if (props.getPort() != null) {
            deployment.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
                    .name("service")
                    .serviceType(ServiceType.CLUSTER_IP)
                    .ports(transformToServicePorts(props.getPort()))
                    .build());
        }

        if (props.isIngressEnabled()) {
            deployment.exposeViaIngress("/(.*)", ExposeDeploymentViaIngressOptions.builder()
                    .ingress(Ingress.Builder.create(this, "ingress").metadata(new ApiObjectMetadata.Builder()
                                    .name("multi-ingress")
                                    .annotations(Map.of("nginx.ingress.kubernetes.io/rewrite-target", "/$1"))
                                    .build())
                            .build())
                    .pathType(HttpIngressPathType.IMPLEMENTATION_SPECIFIC)
                    .build());
        }

        this.deployment = deployment;
    }

    public static List<ServicePort> transformToServicePorts(List<Integer> ports) {
        List<ServicePort> servicePorts = new ArrayList<>();

        if (ports != null) {
            for (Integer port : ports) {
                ServicePort servicePort = new ServicePort.Builder()
                        .name("port-" + port)
                        .port(port)
                        .targetPort(port)
                        .build();
                servicePorts.add(servicePort);
            }
        }

        return servicePorts;
    }

    public static List<ContainerPort> transformToContainerPorts(List<Integer> ports) {
        List<ContainerPort> containerPorts = new ArrayList<>();

        if (ports != null) {
            for (Integer port : ports) {
                ContainerPort containerPort = new ContainerPort.Builder()
                        .number(port)
                        .build();
                containerPorts.add(containerPort);
            }
        }

        return containerPorts;
    }

    @NotNull
    private static ContainerProps getContainer(WebServiceProps props) {
        return new ContainerProps.Builder()
                .name("container")
                .image(props.getImage() + props.getTag())
                .ports(transformToContainerPorts(props.getPort()))
                .build();
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }
}