package com.mycompany.app;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.App;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.ClusterAttributes;
import software.amazon.awscdk.services.eks.ICluster;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

public class DynamoDBAppStack extends Stack {
    private String kubectlRoleARN;
    private String eksClusterName;

    private static final String PRIMARY_KEY_NAME = "shortcode";
    private static final String TABLE_NAME = "urls";

    private static final String APP_DIRECTORY = "/Users/andrestorresgarcia/Development/cdk8s-for-go-developers/java/part5-dynamodb-eks-ack-cdk8s/dynamodb-app";



    public DynamoDBAppStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.kubectlRoleARN = System.getenv("KUBECTL_ROLE_ARN");
        if (this.kubectlRoleARN == null || this.kubectlRoleARN.isEmpty()) {
            throw new RuntimeException("Missing env variable EKS_CLUSTER_NAME");
        }

        this.eksClusterName = System.getenv("EKS_CLUSTER_NAME");
        if (this.eksClusterName == null || this.eksClusterName.isEmpty()) {
            throw new RuntimeException("Missing env variable KUBECTL_ROLE_ARN");
        }

        Table table = Table.Builder.create(this, "dynamodb-table")
            .tableName(TABLE_NAME)
            .partitionKey(Attribute.builder()
                .name(PRIMARY_KEY_NAME)
                .type(AttributeType.STRING)
                .build())
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .removalPolicy(RemovalPolicy.DESTROY)
            .build();


        @NotNull ICluster eksCluster = Cluster.fromClusterAttributes(this, "existing cluster",
            ClusterAttributes.builder()
                .clusterName(eksClusterName)
                .kubectlRoleArn(kubectlRoleARN)
                .build());

        DockerImageAsset appDockerImage = DockerImageAsset.Builder.create(this, "app-image")
            .directory(APP_DIRECTORY)
            .build();

        App app = new App();
        AppChartProps appProps = new AppChartProps(appDockerImage.getImageUri(), table.getTableName());

        eksCluster.addCdk8sChart("dynamodbapp-chart", new DynamoDBAppChart(app, "dynamodb-cdk8s", appProps));
    }


    public static void main(String[] args) {
        final App app = new App();
        new DynamoDBAppStack(app, "DynamoDBEKSAppStack", new StackProps.Builder().build());
        app.synth();
    }

}
