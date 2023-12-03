package com.example.product.domain.model;

import com.example.product.domain.jsonView.ViewRole;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDto {
    @JsonView({ViewRole.DeleteRequest.class, ViewRole.ViewRequest.class})
    private Long id;
    @NotBlank(message = "Name is required")
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    private String name;
    @NotBlank(message = "Description is required")
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    private String description;
    @NotNull(message = "Price is required")
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    private Double price;
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    @NotBlank(message = "Category is required")
    private String category;
    @JsonView({ViewRole.DeleteRequest.class})
    Boolean deleted;
}
