package com.mycompany.app;

import org.cdk8s.ChartProps;
import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;

public class MyChart extends Chart {

    public MyChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        new WordpressStack(this, "wordpress-stack", new WordpressProps.Builder()
                .mySQLImage("mysql")
                .mySQLPassword("Password123")
                .mySQLStorage(3.0)
                .wordpressImage("wordpress:4.8-apache")
                .wordpressStorage(2.0)
                .build());
    }

    public static void main(String[] args) {
        final App app = new App();
        new MyChart(app, "wordpress-custom-stack");
        app.synth();
    }
}
