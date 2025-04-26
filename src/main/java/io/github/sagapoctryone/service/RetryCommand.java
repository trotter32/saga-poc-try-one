package io.github.sagapoctryone.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetryCommand {

    String className;

    String methodName;

    Object[] params;
}
