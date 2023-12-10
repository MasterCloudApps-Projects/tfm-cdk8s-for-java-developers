package com.mycompany.app;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class DatabaseProps extends WebServiceProps{

  private String password;
  private String username;
  private String storageClass;

  public DatabaseProps(final String image, final String tag, final int replicas, final List<Integer> port, final boolean enableIngress) {
    super(image, tag, replicas, port, enableIngress);
    this.password = password;
    this.storageClass = storageClass;
    this.username = username;
  }

}