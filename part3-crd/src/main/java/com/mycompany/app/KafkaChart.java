package com.mycompany.app;

import imports.io.strimzi.kafka.*;
import software.constructs.Construct;
import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaChart extends Chart {

    public KafkaChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public KafkaChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        Map<String, Object> config = new HashMap<>();
        config.put("offsets.topic.replication.factor", 1);
        config.put("transaction.state.log.replication.factor", 1);
        config.put("transaction.state.log.min.isr", 1);
        config.put("default.replication.factor", 1);
        config.put("min.insync.replicas", 1);
        config.put("inter.broker.protocol.version", "3.2");

        new Kafka(this, "kafka", new KafkaProps.Builder()
            .spec(new KafkaSpec.Builder()
                .kafka(new KafkaSpecKafka.Builder()
                    .version("3.2.0")
                    .replicas(1)
                    .listeners(List.of(new KafkaSpecKafkaListeners.Builder()
                        .name("plain")
                        .port(9092)
                        .type(KafkaSpecKafkaListenersType.INTERNAL)
                        .tls(false)
                        .build()))
                    .config(config)
                    .storage(new KafkaSpecKafkaStorage.Builder()
                        .type(KafkaSpecKafkaStorageType.EPHEMERAL)
                        .build())
                    .build())
                .zookeeper(new KafkaSpecZookeeper.Builder()
                    .replicas(1)
                    .storage(new KafkaSpecZookeeperStorage.Builder()
                        .type(KafkaSpecZookeeperStorageType.EPHEMERAL)
                        .build())
                    .build())
                .entityOperator(new KafkaSpecEntityOperator.Builder()
                    .topicOperator(new KafkaSpecEntityOperatorTopicOperator.Builder().build())
                    .build())
                .build())
            .build());
    }

    public static void main(String[] args) {
        final App app = new App();
        new KafkaChart(app, "KafkaApp");
        app.synth();
    }
}
