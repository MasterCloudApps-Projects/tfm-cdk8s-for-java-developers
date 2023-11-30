package com.mycompany.app;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class WebServiceProps {

  private final String image;
  private final String tag;
  private final int replicas;
  private final int port;
  private final int containerPort;

  public WebServiceProps(final String image, final String tag, final int replicas, final int port, final int containerPort) {
    this.image = image;
    this.tag = tag;
    this.replicas = replicas;
    this.port = port;
    this.containerPort = containerPort;
  }

//  public String getImage() {
//    return this.image;
//  }
//
//  public int getReplicas() {
//    return this.replicas;
//  }
//
//  public int getPort() {
//    return this.port;
//  }
//
//  public int getContainerPort() {
//    return this.containerPort;
//  }
//
//  public String getTag() {
//    return tag;
//  }

//  public static final class Builder {
//    private String image;
//    private String tag;
//    private int replicas = 1;
//    private int port = 80;
//    private int containerPort = 8080;
//
//    public Builder image(String image) {
//      this.image = image;
//      return this;
//    }
//
//    public Builder replicas(int replicas) {
//      this.replicas = replicas;
//      return this;
//    }
//
//    public Builder port(int port) {
//      this.port = port;
//      return this;
//    }
//
//    public Builder containerPort(int containerPort) {
//      this.containerPort = containerPort;
//      return this;
//    }
//
//    public Builder tag(String tag) {
//      this.tag = tag;
//      return this;
//    }
//
//    public WebServiceProps build() {
//      return new WebServiceProps(image, tag, replicas, port, containerPort);
//    }
//  }
}