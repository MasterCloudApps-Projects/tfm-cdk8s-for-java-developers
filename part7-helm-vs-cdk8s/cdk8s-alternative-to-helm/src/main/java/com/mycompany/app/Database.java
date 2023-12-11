package com.mycompany.app;


import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Size;
import org.cdk8s.plus25.*;
import software.constructs.Construct;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Database extends WebService {

    private Secret secret;

    public Database(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Database(final Construct scope, final String id, final DatabaseProps props) {
        super(scope, id, props);

        // Create resources specific to the database

        AwsElasticBlockStorePersistentVolume awsElasticBlockStorePersistentVolume = AwsElasticBlockStorePersistentVolume.Builder.create(this, "pv")
                .volumeId("pv")
//                .claim(mysqlPVC)
                .storage(Size.gibibytes(2))
                .accessModes(List.of(PersistentVolumeAccessMode.READ_WRITE_ONCE))
                .storageClassName("cosas")
                .build();

        getDeployment().getContainers().get(0).mount("/db/releaseName-mysql", awsElasticBlockStorePersistentVolume);

        // Optionally create a Secret
        Secret secret = new Secret(this, "secret", SecretProps.builder()
                .metadata(new ApiObjectMetadata.Builder()
                        .name("my-secret")
                        .build())
                .type("Opaque")
                .stringData(Map.of("password", generateBase64EncodedPassword(props.getPassword())))
                .build());

        this.secret = secret;

    }

    public static String generateBase64EncodedPassword(String password) {
        String base64EncodedPassword;
        // Check if the provided password is null or empty
        if (password == null || password.isEmpty()) {
            // Generate a random 8-character string
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[4]; // 8 characters in base64 = 6 bytes, rounding up to 4*2 = 8 bytes
            secureRandom.nextBytes(randomBytes);

            // Encode the random bytes to base64
            base64EncodedPassword = Base64.getEncoder().encodeToString(randomBytes);

        } else {
            // Encode the provided password to base64
            base64EncodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

        }

        return base64EncodedPassword;

    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }
}

