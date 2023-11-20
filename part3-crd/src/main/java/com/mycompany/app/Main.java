package com.mycompany.app;

import imports.io.strimzi.kafka.Kafka;
import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;

public class Main extends Chart 
{

    public Main(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Main(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        // define resources here
    }

    public static void main(String[] args) {
        final App app = new App();
//        new FooChart(app, "FooApp", null);
        new KafkaChart(app, "KafkaApp", null);
        app.synth();
    }
}