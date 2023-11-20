package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;
import org.cdk8s.Size;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class MySQLChart extends Chart {

    public MySQLChart(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MySQLChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        String secretName = "mysql-pass";
        String password = "Password123";

        Secret mysqlSecret = new Secret(this, "mysql-secret", new SecretProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name(secretName)
                        .build())
                .build());

        String secretKey = "password";
        mysqlSecret.addStringData(secretKey, password);

        Deployment deployment = new Deployment(this, "mysql-deployment-cdk8splus", new DeploymentProps.Builder().build());

        String containerImage = "mysql";

        Container mysqlContainer = deployment.addContainer(new ContainerProps.Builder()
                .name("mysql-container")
                .image(containerImage)
                .port(3306)
                .build());

        EnvValue envValFromSecret = EnvValue.fromSecretValue(new SecretValue.Builder()
                .key(secretKey)
                .secret(mysqlSecret)
                .build());

        String mySQLPasswordEnvName = "MYSQL_ROOT_PASSWORD";

        mysqlContainer.getEnv().addVariable(mySQLPasswordEnvName, envValFromSecret);

        PersistentVolumeClaim mysqlPVC = new PersistentVolumeClaim(this, "mysql-pvc", new PersistentVolumeClaimProps.Builder()
                .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
                .storage(Size.gibibytes(2))
                .build());

        String mysqlVolumeName = "mysql-persistent-storage";
        Volume mysqlVolume = Volume.fromPersistentVolumeClaim(this, mysqlVolumeName, mysqlPVC);

        String mySQLVolumeMountPath = "/var/lib/mysql";
        mysqlContainer.mount(mySQLVolumeMountPath, mysqlVolume);

        String mySQLServiceName = "mysql-service";
        String clusterIPNone = "None";

        new Service(this, "mysql-service", new ServiceProps.Builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name(mySQLServiceName)
                        .build())
                .selector(deployment.toPodSelector())
                .clusterIp(clusterIPNone)
                .ports(List.of(new ServicePort.Builder()
                        .port(3306)
                        .build()))
                .build());
    }
}
