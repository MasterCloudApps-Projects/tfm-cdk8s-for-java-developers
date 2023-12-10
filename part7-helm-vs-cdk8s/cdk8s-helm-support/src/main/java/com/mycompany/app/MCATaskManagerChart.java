package com.mycompany.app;

import org.cdk8s.*;
import software.constructs.Construct;

import java.util.Map;

public class MCATaskManagerChart extends Chart
{

    public MCATaskManagerChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MCATaskManagerChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        Map<String, Object> values = Map.of(
                "ingress", Map.of(
                        "enabled", true,
                        "host", "cluster-ip",
                        "serviceType", "NodePort"
                ),
                "persistentVolumes", Map.of(
                        "create", true,
                        "enableStorageClass", true,
                        "storageClasses", Map.of(
                                "mysql", "mysql",
                                "mongo", "mongodb",
                                "rabbit", "rabbitmq"
                        )
                ),
                "networkPolicies", Map.of(
                        "enabled", false
                ),
                "services", Map.of(
                        "mongodb", Map.of(
                                "image", "mongo",
                                "tag", "4.2.3"
                        ),
                        "mysql", Map.of(
                                "image", "mysql",
                                "tag", "8",
                                "username", "myuser",
                                "password", "mypassword"
                        ),
                        "rabbitmq", Map.of(
                                "image", "fjvela/urjc-fjvela-rabbitmq",
                                "tag", "1.0.0"
                        ),
                        "server", Map.of(
                                "image", "fjvela/urjc-fjvela-server",
                                "tag", "1.0.5"
                        ),
                        "worker", Map.of(
                                "image", "torrespro/mca-worker",
                                "tag", "2.0.0"
                        ),
                        "external", Map.of(
                                "image", "fjvela/urjc-fjvela-external-service",
                                "tag", "1.0.1"
                        )
                )
        );

        new Helm(this, "Release", new HelmProps.Builder()
                .chart("mca-03-02-practica4-recuperacion/Practica_4_Recuperacion_helm")
                .values(values)
                .build()
        );
    }

    public static void main(String[] args) {
        final App app = new App();
        new MCATaskManagerChart(app, "MyChart");
        app.synth();
    }
}
