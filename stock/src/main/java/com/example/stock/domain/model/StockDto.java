package com.example.stock.domain.model;

import com.example.stock.domain.jsonView.ViewRole;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockDto {

    @JsonView({ViewRole.DeleteRequest.class, ViewRole.ViewRequest.class})
    private Long id;
    @NotNull(message = "Product id is required")
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    private Long productId;
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    @NotNull(message = "Stock is required")
    @Size(min = 0, message = "Stock must be greater than or equal to 0")
    private Long stock;
}
