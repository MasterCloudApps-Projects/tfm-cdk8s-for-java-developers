package com.mycompany.app;


import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;
import software.constructs.Construct;


public class MyChart extends Chart {

    public MyChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);
    }

    public static void main(String[] args) {
        App app = new App();

        DynamoDBChart dynamodDB = new DynamoDBChart(app, "dynamodb", null);
        DeploymentChart deployment = new DeploymentChart(app, "deployment", null, dynamodDB.getFieldExportForTable(),
                dynamodDB.getFieldExportForRegion(), dynamodDB.getConfigMap());

        deployment.addDependency(dynamodDB);

        app.synth();
    }
}