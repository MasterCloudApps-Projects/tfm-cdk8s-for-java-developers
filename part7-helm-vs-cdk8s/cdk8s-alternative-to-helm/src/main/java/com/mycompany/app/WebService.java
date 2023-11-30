package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.plus27.*;
import org.cdk8s.plus27.ContainerPort;
import org.cdk8s.plus27.ServicePort;
import software.constructs.Construct;

import java.util.List;

public class WebService extends Construct {

    public WebService(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public WebService(final Construct scope, final String id, final WebServiceProps props) {
        super(scope, id);


//        Map<String, String> label = Collections.singletonMap("app", Names.toLabelValue(this));

//        new KubeService(this, "service", KubeServiceProps.builder()
//                .spec(ServiceSpec.builder()
//                        .type("LoadBalancer")
//                        .ports(List.of(ServicePort.builder()
//                                .port(props.getPort())
//                                .targetPort(IntOrString.fromNumber(props.getContainerPort()))
//                                .build()))
//                        .selector(label)
//                        .build())
//                .build());
//
//        new KubeDeployment(this, "deployment", KubeDeploymentProps.builder()
//                .spec(DeploymentSpec.builder()
//                        .replicas(props.getReplicas())
//                        .selector(LabelSelector.builder()
//                                .matchLabels(label)
//                                .build())
//                        .template(PodTemplateSpec.builder()
//                                .metadata(ObjectMeta.builder()
//                                        .labels(label)
//                                        .build())
//                                .spec(PodSpec.builder()
//                                        .containers(List.of(Container.builder()
//                                                        .name("web")
//                                                        .image(props.getImage())
//                                                        .ports(List.of(ContainerPort.builder()
//                                                                        .containerPort(props.getContainerPort())
//                                                                .build()))
//                                                .build()))
//                                        .build())
//                                .build())
//                        .build())
//                .build());

        Deployment deployment = new Deployment(this, "deployment", new DeploymentProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("deployment")
                        .build())
                .build());


        deployment.addContainer(new ContainerProps.Builder()
                .name("container")
                .image(props.getImage()+props.getTag())
                .ports(List.of(ContainerPort.builder().number(props.getContainerPort()).build()))
                .build());

        deployment.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
                .name("service")
                .serviceType(ServiceType.LOAD_BALANCER)
                .ports(List.of(new ServicePort.Builder()
                        .port(props.getPort())
                        .targetPort(props.getContainerPort())
                        .build()))
                .build());
    }

}