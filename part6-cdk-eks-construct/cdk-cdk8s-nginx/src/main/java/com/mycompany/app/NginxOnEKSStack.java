package com.mycompany.app;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.DefaultCapacityType;
import software.amazon.awscdk.services.eks.EndpointAccess;
import software.amazon.awscdk.services.eks.KubernetesVersion;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static software.amazon.awscdk.services.eks.EndpointAccess.*;

public class NginxOnEKSStack extends Stack {

    public NginxOnEKSStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, "demo-vpc").build();

        SecurityGroup eksSecurityGroup = SecurityGroup.Builder.create(this, "eks-demo-sg")
                .vpc(vpc)
                .securityGroupName("eks-demo-sg")
                .allowAllOutbound(true)
                .build();

        Cluster eksCluster = Cluster.Builder.create(this, "demo-eks")
                .clusterName("demo-eks-cluster")
                .version(KubernetesVersion.V1_25)
                .vpc(vpc)
                .securityGroup(eksSecurityGroup)
                .vpcSubnets(List.of(SubnetSelection.builder().subnets(vpc.getPrivateSubnets()).build()))
                .defaultCapacity(2)
                .defaultCapacityInstance(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.SMALL))
                .defaultCapacityType(DefaultCapacityType.NODEGROUP)
                .outputConfigCommand(true)
                .endpointAccess(PUBLIC)
                .build();

//        deployNginxUsingCDK(eksCluster);
        deployNginxUsingCDK8s(eksCluster);
    }

    private void deployNginxUsingCDK(Cluster eksCluster) {
        Map<String, String> appLabel = new HashMap<>();
        appLabel.put("app", "nginx-eks-cdk");

        eksCluster.addManifest("app-deployment",
                Map.of(
                        "apiVersion", "apps/v1",
                        "kind", "Deployment",
                        "metadata", Map.of("name", "nginx-deployment-cdk"),
                        "spec", Map.of(
                                "replicas", 1,
                                "selector", Map.of("matchLabels", appLabel),
                                "template", Map.of(
                                        "metadata", Map.of("labels", appLabel),
                                        "spec", Map.of(
                                                "containers", List.of(
                                                        Map.of(
                                                                "name", "nginx",
                                                                "image", "nginx",
                                                                "ports", List.of(Map.of("containerPort", 80))
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                Map.of(
                        "apiVersion", "v1",
                        "kind", "Service",
                        "metadata", Map.of("name", "nginx-service-cdk"),
                        "spec", Map.of(
                                "type", "LoadBalancer",
                                "ports", List.of(Map.of("port", 9090, "targetPort", 80)),
                                "selector", appLabel
                        )
                )
        );
    }

    private void deployNginxUsingCDK8s(Cluster eksCluster) {
        App app = new App();
        NginxChart nginxChart = new NginxChart(app, "nginx-cdk8s", null);
        eksCluster.addCdk8sChart("nginx-eks-chart", nginxChart , null);
    }


    public static void main(String[] args) {
        final App app = new App();
        new NginxOnEKSStack(app, "NginxOnEKSStack", new StackProps.Builder().build());
        app.synth();
    }

}
