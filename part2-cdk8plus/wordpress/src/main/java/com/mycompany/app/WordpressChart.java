package com.mycompany.app;

import org.cdk8s.Chart;
import org.cdk8s.ChartProps;
import org.cdk8s.Size;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class WordpressChart extends Chart {

    public WordpressChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public WordpressChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        String secretName = "mysql-pass";
        ISecret mysqlSecret = Secret.fromSecretName(this, "existing-secret", secretName);

        String secretKey = "password";

        Deployment deployment = new Deployment(this, "wordpress-deployment-cdk8splus", new DeploymentProps.Builder().build());

        String containerImage = "wordpress:4.8-apache";

        Container wordpressContainer = deployment.addContainer(new ContainerProps.Builder()
                .name("wordpress-container")
                .image(containerImage)
                .port(80)
                .build());

        EnvValue envValFromSecret = EnvValue.fromSecretValue(new SecretValue.Builder()
                .key(secretKey)
                .secret(mysqlSecret)
                .build());

        String wordpressMySQLPasswordEnvName = "WORDPRESS_DB_PASSWORD";
        String wordpressMySQLDBHostEnvName = "WORDPRESS_DB_HOST";
        String wordpressMySQLDBHostEnvValue = "mysql-service";

        wordpressContainer.getEnv().addVariable(wordpressMySQLPasswordEnvName, envValFromSecret);
        wordpressContainer.getEnv().addVariable(wordpressMySQLDBHostEnvName, EnvValue.fromValue(wordpressMySQLDBHostEnvValue));

        PersistentVolumeClaim wordpressPVC = new PersistentVolumeClaim(this, "wordpress-pvc", new PersistentVolumeClaimProps.Builder()
                .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
                .storage(Size.gibibytes(2))
                .build());

        String wordpressVolumeName = "wordpress-persistent-storage";
        Volume wordpressVolume = Volume.fromPersistentVolumeClaim(this, wordpressVolumeName, wordpressPVC);

        String wordpressVolumeMountPath = "/var/www/html";
        wordpressContainer.mount(wordpressVolumeMountPath, wordpressVolume);

        String wordpressServiceName = "wordpress-service";

        deployment.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
                .name(wordpressServiceName)
                .serviceType(ServiceType.LOAD_BALANCER)
                .ports(List.of(new ServicePort.Builder()
                        .port(80)
                        .build()))
                .build());
    }
}

