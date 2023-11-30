package com.mycompany.app;


import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Size;
import org.cdk8s.plus27.*;
import org.cdk8s.plus27.k8s.*;
import org.cdk8s.plus27.k8s.Volume;
import software.constructs.Construct;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Database extends WebService {

    public Database(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Database(final Construct scope, final String id, final DatabaseProps props) {
        super(scope, id, props);

        // Create resources specific to the database

        PersistentVolumeClaim mysqlPVC = new PersistentVolumeClaim(this, "mysql-pvc", new PersistentVolumeClaimProps.Builder()
                .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
                .storage(Size.gibibytes(2))
                .build());

        String mysqlVolumeName = "mysql-persistent-storage";
//        Volume mysqlVolume = Volume.builder().

        String mySQLVolumeMountPath = "/var/lib/mysql";
//        mysqlContainer.mount(mySQLVolumeMountPath, mysqlVolume);

        // Optionally create a Secret
        if (props != null && props.getPassword() != null) {
            Secret secret = new Secret(this, "secret", SecretProps.builder()
                    .metadata(new ApiObjectMetadata.Builder()
                            .name("my-secret")
                            .build())
                    .stringData(Map.of("password",props.getPassword())) // Assuming getSecretData() returns the required secret data
                    .build());
        }

//        // Create Deployment for the database
//        Deployment databaseDeployment = new Deployment(this, "databaseDeployment", DeploymentProps.builder()
//                // Add deployment properties specific to the database
//                .build());
//
//        // Create a Service for the database
//        Service databaseService = new Service(this, "databaseService", ServiceProps.builder()
//                // Add service properties specific to the database
//                .build());
    }
}
