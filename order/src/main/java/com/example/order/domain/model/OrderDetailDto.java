package com.example.order.domain.model;

import com.example.order.domain.jsonView.ViewRole;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDetailDto {
    @JsonView({ViewRole.DeleteRequest.class, ViewRole.ViewRequest.class})
    private Long id;
    @JsonView({ViewRole.UpsertRequest.class, ViewRole.ViewRequest.class, ViewRole.AddRequest.class})
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    @JsonView({ViewRole.ViewRequest.class})
    private Double unitAmount;
    @NotNull(message = "Product id is required")
    @JsonView({ViewRole.UpsertRequest.class, ViewRole.ViewRequest.class, ViewRole.AddRequest.class})
    private Long productId;
    @JsonView({ViewRole.ViewRequest.class})
    private String productName;
}
