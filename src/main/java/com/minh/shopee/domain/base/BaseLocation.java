package com.minh.shopee.domain.base;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseLocation {
    @Id
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
}
