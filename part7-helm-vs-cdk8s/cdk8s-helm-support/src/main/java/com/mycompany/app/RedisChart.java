package com.mycompany.app;

import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.Helm;
import org.cdk8s.HelmProps;
import software.constructs.Construct;

import java.util.Map;

public class RedisChart extends Chart {

    public RedisChart(final Construct scope, final String id) {
        super(scope, id, null);

        Map<String, Object> values = Map.of(
            "sentinel", Map.of(
                "enabled", true
            )
        );

        new Helm(this, "redis", new HelmProps.Builder()
            .chart("bitnami/redis")
            .values(values)
            .build()
        );
    }

    public static void main(String[] args) {
        final App app = new App();
        new RedisChart(app, "MyChart");
        app.synth();
    }
}
