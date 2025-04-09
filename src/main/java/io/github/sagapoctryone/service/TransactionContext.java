package io.github.sagapoctryone.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionContext {
    public static final ThreadLocal<String> transactionId = new ThreadLocal<>();
}
