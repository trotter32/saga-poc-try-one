package io.github.sagapoctryone.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class RepoCallArg {
    @Id
    String id;

    String argumentClass;

    String repositoryClass;

    String auxiliaryId;
}
