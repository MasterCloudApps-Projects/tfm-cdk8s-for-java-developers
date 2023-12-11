package com.mycompany.app;

import org.cdk8s.plus25.*;
import software.constructs.Construct;

import org.cdk8s.App;
import org.cdk8s.Chart;

import java.util.List;

public class MCATaskManagerChart extends Chart
{

    public MCATaskManagerChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MCATaskManagerChart(final Construct scope, final String id, final MCATaskManagerChartOptions MCATaskManagerChartOptions) {
        super(scope, id, MCATaskManagerChartOptions);

        // define resources here

        WebService rabbitmq = new WebService(this, "rabbitmq", WebServiceProps.builder()
                .image("fjvela/urjc-fjvela-rabbitmq")
                .tag("1.0.0")
                .port(List.of(5672))
                .replicas(2)
                .build());

        Database mysql = new Database(this, "mysql", DatabaseProps.builder()
                .image("mysql")
                .tag("8")
                .port(List.of(3306))
                .replicas(2)
                .password("password")
                .build());

        mysql.getDeployment().getContainers().getFirst().getEnv().addVariable("MYSQL_DATABASE", EnvValue.fromValue("database"));
        mysql.getDeployment().getContainers().getFirst().getEnv().addVariable("MYSQL_USER", EnvValue.fromValue("username"));
        mysql.getDeployment().getContainers().getFirst().getEnv().addVariable("MYSQL_ROOT_PASSWORD", EnvValue.fromValue("mypassword"));
        mysql.getDeployment().getContainers().getFirst().getEnv().addVariable("MYSQL_PASSWORD", EnvValue.fromSecretValue(SecretValue.builder()
                .secret(mysql.getSecret())
                .key("password")
                .build()));

        Database mongodb = new Database(this, "mongodb", DatabaseProps.builder()
                .image("mongo")
                .tag("423")
                .port(List.of(27017, 27018, 27019))
                .replicas(2)
                .password("passwordMongo")
                .build());

        WebService server = new WebService(this, "server", WebServiceProps.builder()
                .image("fjvela/urjc-fjvela-server")
                .tag("1.0.5")
                .port(List.of(8080))
                .ingressEnabled(true)
                .build());

        WebService worker = new WebService(this, "worker", WebServiceProps.builder()
                .image("torrespro/mca-worker")
                .tag("2.0.0")
                .replicas(2)
                .build());

        WebService external = new WebService(this, "external", WebServiceProps.builder()
                .image("urjc-fjvela-external-service")
                .tag("1.0.1")
                .port(List.of(9090))
                .build());

        if (MCATaskManagerChartOptions.isNetworkPoliciesEnabled()) {

            NetworkPolicy.Builder.create(this, "policyDenyAll")
                    .ingress(NetworkPolicyTraffic.builder().defaultValue(NetworkPolicyTrafficDefault.DENY).build())
                    .egress(NetworkPolicyTraffic.builder().defaultValue(NetworkPolicyTrafficDefault.DENY).build())
                    .build();

            //worker allow rabbit
            worker.getDeployment().getConnections().allowTo(rabbitmq.getDeployment());
            worker.getDeployment().getConnections().allowTo(mysql.getDeployment());
            rabbitmq.getDeployment().getConnections().allowFrom(server.getDeployment());
            mongodb.getDeployment().getConnections().allowFrom(server.getDeployment());
            external.getDeployment().getConnections().allowTo(worker.getDeployment());

        }

    }

    public static void main(String[] args) {
        final App app = new App();
        MCATaskManagerChartOptions mcaTaskManagerChartOptions = MCATaskManagerChartOptions.builder()
                .networkPoliciesEnabled(false)
                .build();
        new MCATaskManagerChart(app, "cdk8s-alternative-to-helm", mcaTaskManagerChartOptions);
        app.synth();
    }
}