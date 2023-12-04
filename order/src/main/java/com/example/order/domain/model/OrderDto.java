package com.example.order.domain.model;

import com.example.order.domain.entity.StatusType;
import com.example.order.domain.jsonView.ViewRole;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDto {
    @JsonView({ViewRole.DeleteRequest.class, ViewRole.ViewRequest.class})
    private Long id;
    @NotEmpty(message = "Order content cannot be empty")
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    private List<OrderDetailDto> orderDetailEntityList = new ArrayList<>();
    @JsonView({ViewRole.ViewRequest.class})
    private Double totalAmount;
    @JsonView({ViewRole.ViewRequest.class, ViewRole.AddRequest.class, ViewRole.UpsertRequest.class})
    @NotNull(message = "Discount is required")
    private Double discount;
    @JsonView({ViewRole.ViewRequest.class})
    private Double paymentAmount;
    @JsonView({ViewRole.ViewRequest.class})
    private StatusType status;
}
