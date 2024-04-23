package tech.maxjung.api.composite.product;

public record ServiceAddresses(
  String compositeAddress,
  String productAddress,
  String reviewAddress,
  String recommendationAddress
) {

  public static ServiceAddresses empty() {
    return new ServiceAddresses(null, null, null, null);
  }

}
