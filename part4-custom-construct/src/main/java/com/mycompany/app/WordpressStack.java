package com.mycompany.app;

import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Size;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.util.List;

public class WordpressStack extends Construct {

    public WordpressStack(final Construct scope, final String id, final WordpressProps props) {
        super(scope, id);

        String secretName = "mysql-pass";
        String password = props.getMySQLPassword();

        Secret mysqlSecret = new Secret(this, "mysql-secret", new SecretProps.Builder()
            .metadata(new ApiObjectMetadata.Builder()
                .name(secretName)
                .build())
            .build());

        String mysqlSecretKey = "password";
        mysqlSecret.addStringData(mysqlSecretKey, password);

        Deployment deployment = new Deployment(this, "mysql-deployment-cdk8splus", new DeploymentProps.Builder().build());

        String mysqlContainerImage = props.getMySQLImage();

        Container mysqlContainer = deployment.addContainer(new ContainerProps.Builder()
            .name("mysql-container")
            .image(mysqlContainerImage)
            .port(3306)
            .build());

        EnvValue envValFromSecret = EnvValue.fromSecretValue(new SecretValue.Builder()
            .key(mysqlSecretKey)
            .secret(mysqlSecret)
            .build());

        String mySQLPasswordEnvName = "MYSQL_ROOT_PASSWORD";

        mysqlContainer.getEnv().addVariable(mySQLPasswordEnvName, envValFromSecret);

        PersistentVolumeClaim mysqlPVC = new PersistentVolumeClaim(this, "mysql-pvc", new PersistentVolumeClaimProps.Builder()
            .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
            .storage(Size.gibibytes(props.getMySQLStorage()))
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

        Deployment wordPressDeployment = new Deployment(this, "wordpress-deployment-cdk8splus", new DeploymentProps.Builder().build());

        String wordpressContainerImage = props.getWordpressImage();

        Container wordpressContainer = wordPressDeployment.addContainer(new ContainerProps.Builder()
                .name("wordpress-container")
                .image(wordpressContainerImage)
                .port(80)
                .build());

//        String secretKey = "password";
//        Secret mysqlSecret = Secret.fromSecretName(this, "existing-secret", secretKey);
//        EnvValue envValFromSecret = EnvValue.fromSecretValue(new SecretValue.Builder()
//                .key(secretKey)
//                .secret(mysqlSecret)
//                .build());

        String wordpressMySQLPasswordEnvName = "WORDPRESS_DB_PASSWORD";
        String wordpressMySQLDBHostEnvName = "WORDPRESS_DB_HOST";
        String wordpressMySQLDBHostEnvValue = "mysql-service";

        wordpressContainer.getEnv().addVariable(wordpressMySQLPasswordEnvName, envValFromSecret);
        wordpressContainer.getEnv().addVariable(wordpressMySQLDBHostEnvName, EnvValue.fromValue(wordpressMySQLDBHostEnvValue));

        PersistentVolumeClaim wordpressPVC = new PersistentVolumeClaim(this, "wordpress-pvc", new PersistentVolumeClaimProps.Builder()
                .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
                .storage(Size.gibibytes(props.getWordpressStorage()))
                .build());

        String wordpressVolumeName = "wordpress-persistent-storage";
        Volume wordpressVolume = Volume.fromPersistentVolumeClaim(this, wordpressVolumeName, wordpressPVC);

        String wordpressVolumeMountPath = "/var/www/html";
        wordpressContainer.mount(wordpressVolumeMountPath, wordpressVolume);

        String wordpressServiceName = "wordpress-service";

        wordPressDeployment.exposeViaService(new DeploymentExposeViaServiceOptions.Builder()
                .name(wordpressServiceName)
                .serviceType(ServiceType.LOAD_BALANCER)
                .ports(List.of(new ServicePort.Builder()
                        .port(80)
                        .build()))
                .build());
    }
}
