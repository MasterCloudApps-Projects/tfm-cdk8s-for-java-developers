package com.mycompany.app;

import lombok.Builder;
import lombok.Getter;
import org.cdk8s.ChartProps;

@Builder
@Getter
public class MCATaskManagerChartOptions implements ChartProps {
    public boolean networkPoliciesEnabled;
}
