package com.mycompany.app;

import imports.aws.k8s.services.*;
import imports.aws.k8s.services.dynamodb.*;
import org.cdk8s.ApiObjectMetadata;
import org.cdk8s.Chart;
import org.cdk8s.ChartProps;
import org.cdk8s.plus25.ConfigMap;
import org.cdk8s.plus25.ConfigMapProps;
import software.constructs.Construct;

import java.util.List;

public class DynamoDBChart extends Chart {

    private static final String PRIMARY_KEY_NAME = "shortcode";
    private static final String BILLING_MODE = "PAY_PER_REQUEST";
    private static final String HASH_KEY_TYPE = "HASH";
    private static final String FIELD_EXPORT_NAME_FOR_TABLE = "export-dynamodb-tablename";
    private static final String FIELD_EXPORT_NAME_FOR_REGION = "export-dynamodb-region";
    private static final String CONFIG_MAP_NAME = "export-dynamodb-urls-info";

    private FieldExport fieldExportForTable;
    private FieldExport fieldExportForRegion;
    private ConfigMap configMap;

    public DynamoDBChart(final Construct scope, final String id, final ChartProps options) {
        super(scope, id, options);

        String tableName = System.getenv("TABLE_NAME");
        if (tableName == null || tableName.isEmpty()) {
            throw new RuntimeException("Missing environment variable TABLE_NAME");
        }

        Table table = new Table(this, "dynamodb-ack-cdk8s-table", new TableProps.Builder()
            .spec(new TableSpec.Builder()
                .attributeDefinitions(List.of(new TableSpecAttributeDefinitions.Builder()
                    .attributeName(PRIMARY_KEY_NAME)
                    .attributeType("S")
                    .build()))
                .billingMode(BILLING_MODE)
                .tableName(tableName)
                .keySchema(List.of(new TableSpecKeySchema.Builder()
                    .attributeName(PRIMARY_KEY_NAME)
                    .keyType(HASH_KEY_TYPE)
                    .build()))
                .build())
            .build());

         configMap = new ConfigMap(this, "config-map", new ConfigMapProps.Builder()
            .metadata(new ApiObjectMetadata.Builder()
                .name(CONFIG_MAP_NAME)
                .build())
            .build());

         fieldExportForTable = new FieldExport(this, "fexp-table", new FieldExportProps.Builder()
            .metadata(new ApiObjectMetadata.Builder()
                .name(FIELD_EXPORT_NAME_FOR_TABLE)
                .build())
            .spec(new FieldExportSpec.Builder()
                .from(new FieldExportSpecFrom.Builder()
                    .path(".spec.tableName")
                    .resource(new FieldExportSpecFromResource.Builder()
                        .group("dynamodb.services.k8s.aws")
                        .kind("Table")
                        .name(table.getName())
                        .build())
                    .build())
                .to(new FieldExportSpecTo.Builder()
                    .name(configMap.getName())
                    .kind(FieldExportSpecToKind.CONFIGMAP)
                    .build())
                .build())
            .build());

         fieldExportForRegion = new FieldExport(this, "fexp-region", new FieldExportProps.Builder()
            .metadata(new ApiObjectMetadata.Builder()
                .name(FIELD_EXPORT_NAME_FOR_REGION)
                .build())
            .spec(new FieldExportSpec.Builder()
                .from(new FieldExportSpecFrom.Builder()
                    .path(".status.ackResourceMetadata.region")
                    .resource(new FieldExportSpecFromResource.Builder()
                        .group("dynamodb.services.k8s.aws")
                        .kind("Table")
                        .name(table.getName())
                        .build())
                    .build())
                .to(new FieldExportSpecTo.Builder()
                    .name(configMap.getName())
                    .kind(FieldExportSpecToKind.CONFIGMAP)
                    .build())
                .build())
            .build());
    }

    public FieldExport getFieldExportForTable() {
        return fieldExportForTable;
    }

    public void setFieldExportForTable(FieldExport fieldExportForTable) {
        this.fieldExportForTable = fieldExportForTable;
    }

    public FieldExport getFieldExportForRegion() {
        return fieldExportForRegion;
    }

    public void setFieldExportForRegion(FieldExport fieldExportForRegion) {
        this.fieldExportForRegion = fieldExportForRegion;
    }

    public ConfigMap getConfigMap() {
        return configMap;
    }

    public void setConfigMap(ConfigMap configMap) {
        this.configMap = configMap;
    }
}
