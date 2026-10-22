package com.maxshop.api.service;

import com.maxshop.api.dto.product.ProductRequest;
import com.maxshop.api.dto.product.ProductResponse;
import com.maxshop.api.entity.Category;
import com.maxshop.api.entity.Product;
import com.maxshop.api.entity.ProductImage;
import com.maxshop.api.entity.User;
import com.maxshop.api.exception.ResourceNotFoundException;
import com.maxshop.api.repository.CategoryRepository;
import com.maxshop.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> search(String query, UUID categoryId, BigDecimal minPrice,
                                         BigDecimal maxPrice, Pageable pageable) {
        return productRepository.search(query, categoryId, minPrice, maxPrice, pageable)
                .map(this::toResponse);
    }

    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(User seller, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        String slug = generateSlug(request.name());

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .price(request.price())
                .compareAtPrice(request.compareAtPrice())
                .stockQuantity(request.stockQuantity())
                .active(true)
                .build();

        if (request.images() != null) {
            List<ProductImage> images = request.images().stream()
                    .map(url -> ProductImage.builder().product(product).url(url).build())
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(User seller, UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ResourceNotFoundException("Produto não encontrado.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCompareAtPrice(request.compareAtPrice());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);

        return toResponse(product);
    }

    @Transactional
    public void deactivate(User seller, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ResourceNotFoundException("Produto não encontrado.");
        }

        product.setActive(false);
    }

    private String generateSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .sellerName(product.getSeller().getName())
                .images(product.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList()))
                .build();
    }
}
