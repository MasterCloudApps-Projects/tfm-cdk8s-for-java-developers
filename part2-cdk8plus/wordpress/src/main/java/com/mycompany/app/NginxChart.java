package com.mycompany.app;

import org.cdk8s.ChartProps;
import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;

import imports.k8s.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NginxChart extends Chart {

    public NginxChart(final Construct scope, final String id, final String appLabel) {
        this(scope, id, null, appLabel);
    }

    public NginxChart(final Construct scope, final String id, final ChartProps options, final String appLabel) {
        super(scope, id, options);

        Map<String, String> label = Collections.singletonMap("app", appLabel);

        LabelSelector selector = new LabelSelector.Builder()
                .matchLabels(label)
                .build();

        ObjectMeta labels = new ObjectMeta.Builder()
                .labels(label)
                .build();

        Container nginxContainer = new Container.Builder()
                .name("nginx-container")
                .image("nginx:1.19.10")
                .ports(List.of(new ContainerPort.Builder().containerPort(80).build()))
                .build();

        new KubeDeployment(this, "deployment", new KubeDeploymentProps.Builder()
                .spec(new DeploymentSpec.Builder()
                        .replicas(1)
                        .selector(selector)
                        .template(new PodTemplateSpec.Builder()
                                .metadata(labels)
                                .spec(new PodSpec.Builder()
                                        .containers(List.of(nginxContainer))
                                        .build())
                                .build())
                        .build())
                .build());
    }

    public static void main(String[] args) {
        final App app = new App();
        MySQLChart mySQLChart = new MySQLChart(app, "mysql");
        WordpressChart wordpressChart = new WordpressChart(app, "wordpress");
        wordpressChart.addDependency(mySQLChart);
        app.synth();
    }

}
