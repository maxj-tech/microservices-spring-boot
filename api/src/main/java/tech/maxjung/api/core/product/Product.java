package tech.maxjung.api.core.product;

public record Product(
        int productId,
        String name,
        int weight,
        String serviceAddress   // todo rethink
) {}

