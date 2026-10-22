package com.maxshop.api.service;

import com.maxshop.api.dto.order.CartItemRequest;
import com.maxshop.api.dto.order.CartResponse;
import com.maxshop.api.entity.CartItem;
import com.maxshop.api.entity.Product;
import com.maxshop.api.entity.User;
import com.maxshop.api.exception.BusinessException;
import com.maxshop.api.exception.ResourceNotFoundException;
import com.maxshop.api.repository.CartItemRepository;
import com.maxshop.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse addItem(User user, CartItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (product.getStockQuantity() < request.quantity()) {
            throw new BusinessException("Estoque insuficiente para este produto.");
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        item.setQuantity(item.getQuantity() + request.quantity());
        cartItemRepository.save(item);

        return getCart(user);
    }

    @Transactional
    public void removeItem(User user, java.util.UUID productId) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho."));
        cartItemRepository.delete(item);
    }

    public CartResponse getCart(User user) {
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());

        List<CartResponse.Item> responseItems = items.stream()
                .map(i -> CartResponse.Item.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .unitPrice(i.getProduct().getPrice())
                        .quantity(i.getQuantity())
                        .lineTotal(i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .toList();

        BigDecimal subtotal = responseItems.stream()
                .map(CartResponse.Item::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder().items(responseItems).subtotal(subtotal).build();
    }
}
