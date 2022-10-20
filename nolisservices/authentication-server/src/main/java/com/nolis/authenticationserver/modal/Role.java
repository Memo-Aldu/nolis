package com.nolis.authenticationserver.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document("app_roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String name;

}
