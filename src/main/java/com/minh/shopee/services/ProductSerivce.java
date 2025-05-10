package com.minh.shopee.services;

import java.util.Set;

public interface ProductSerivce {

    <T> Set<T> getAllProducts(Class<T> type);
}
