package com.mycompany.app;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class DatabaseProps extends WebServiceProps{

  private String password;
  private String storageClass;

  public DatabaseProps(final String image, final String tag, final int replicas, final int port, final int containerPort) {
    super(image, tag, replicas, port, containerPort);
    this.password = password;
    this.storageClass = storageClass;
  }


//  public static final class Builder {
//    private String password;
//    private String storageClass;
//
//    public Builder password(String password) {
//      this.password = password;
//      return this;
//    }
//
//    public Builder storageClass(String storageClass) {
//      this.storageClass = storageClass;
//      return this;
//    }
//
//
//    public DatabaseProps build() {
//      return new DatabaseProps(image, tag, replicas, port, containerPort);
//    }
//  }
}