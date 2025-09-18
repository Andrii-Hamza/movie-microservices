package com.programming.order_service.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.programming.order_service.dto.InventoryResponse;
import com.programming.order_service.dto.OrderLineItemsDto;
import com.programming.order_service.dto.OrderRequest;
import com.programming.order_service.model.Order;
import com.programming.order_service.model.OrderLineItems;
import com.programming.order_service.repo.OrderRepository;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    @TimeLimiter(name = "inventory")
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());

        ScopedSpan inventoryServiceLookup = tracer.startScopedSpan("InventoryServiceLookup");

        try {
            log.info("Checking inventory for skuCodes: {}", skuCodes);
            InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                                    .build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponsArray)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                log.info("Order {} placed successfully", order.getOrderNumber());
                return "Order Placed successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later.");
            }
        } catch (Exception e) {
            inventoryServiceLookup.error(e);
            throw e;
        } finally {
            inventoryServiceLookup.finish();
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
