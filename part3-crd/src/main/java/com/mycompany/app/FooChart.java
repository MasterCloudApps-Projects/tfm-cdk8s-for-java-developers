package com.mycompany.app;

import imports.io.k8s.samplecontroller.Foo;
import imports.io.k8s.samplecontroller.FooProps;
import imports.io.k8s.samplecontroller.FooSpec;
import software.constructs.Construct;
import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;

public class FooChart extends Chart {

    public FooChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public FooChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        new Foo(this, "foo1", new FooProps.Builder()
            .spec(new FooSpec.Builder()
                .deploymentName("foo1-dep")
                .replicas(2)
                .build())
            .build());
    }

    public static void main(String[] args) {
        final App app = new App();
        new FooChart(app, "foo");
        app.synth();
    }
}
