package com.clienthub.singleton;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final Product product;

    public ProductService(Product product) {
        this.product = product;
        System.out.println();
    }

    public void processProduct() {
        System.out.println("Product name: " + product.getName());
        System.out.println("Product price: " + product.getPrice());
    }

}
