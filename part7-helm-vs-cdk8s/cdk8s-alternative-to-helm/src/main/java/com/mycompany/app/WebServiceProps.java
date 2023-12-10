package com.mycompany.app;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.cdk8s.plus25.ServiceType;

import java.util.List;

@SuperBuilder
@Getter
public class WebServiceProps {

  private final String image;
  private final String tag;
  private final int replicas;
  private final List<Integer> port;
  private final boolean ingressEnabled;
  public String ingressHost;
  public ServiceType ingressServiceType;

  public WebServiceProps(final String image, final String tag, final int replicas, final List<Integer> port, final boolean ingressEnabled) {
    this.image = image;
    this.tag = tag;
    this.replicas = replicas;
    this.port = port;
    this.ingressEnabled = ingressEnabled;
  }
}