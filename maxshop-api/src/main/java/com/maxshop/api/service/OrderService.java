package com.maxshop.api.service;

import com.maxshop.api.dto.order.CheckoutRequest;
import com.maxshop.api.dto.order.OrderResponse;
import com.maxshop.api.entity.*;
import com.maxshop.api.exception.BusinessException;
import com.maxshop.api.exception.ResourceNotFoundException;
import com.maxshop.api.repository.CartItemRepository;
import com.maxshop.api.repository.OrderRepository;
import com.maxshop.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(99);
    private static final BigDecimal DEFAULT_SHIPPING_COST = BigDecimal.valueOf(19.90);

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse checkout(User customer, CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(customer.getId());

        if (cartItems.isEmpty()) {
            throw new BusinessException("Seu carrinho está vazio.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Estoque insuficiente para: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .seller(product.getSeller())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build());
        }

        BigDecimal shippingCost = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : DEFAULT_SHIPPING_COST;

        BigDecimal total = subtotal.add(shippingCost);

        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDENTE)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .discount(BigDecimal.ZERO)
                .total(total)
                .shippingAddress(request.shippingAddress())
                .paymentMethod(request.paymentMethod())
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        orderRepository.save(order);
        cartItemRepository.deleteByUserId(customer.getId());

        return toResponse(order);
    }

    public Page<OrderResponse> findByCustomer(User customer, Pageable pageable) {
        return orderRepository.findByCustomerId(customer.getId(), pageable).map(this::toResponse);
    }

    public OrderResponse findById(User customer, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        if (!order.getCustomer().getId().equals(customer.getId())
                && customer.getRole() != Role.ADMIN) {
            throw new ResourceNotFoundException("Pedido não encontrado.");
        }

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.Item> items = order.getItems().stream()
                .map(i -> OrderResponse.Item.builder()
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
