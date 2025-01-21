package com.haibazo_bff_its_rct_webapi.specification;

import com.haibazo_bff_its_rct_webapi.enums.Collections;
import com.haibazo_bff_its_rct_webapi.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasSize(String size) {
        return (root, query, criteriaBuilder) -> {
            if (size == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, ProductAvailableVariant> availableVariantJoin = root.join("productAvailableVariants");
            Join<ProductAvailableVariant, ProductVariant> variantJoin = availableVariantJoin.join("productVariants");
            return criteriaBuilder.equal(variantJoin.get("value"), size);
        };
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, criteriaBuilder) -> {
            if (color == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, ProductAvailableVariant> availableVariantJoin = root.join("productAvailableVariants");
            Join<ProductAvailableVariant, ProductVariant> variantJoin = availableVariantJoin.join("productVariants");
            return criteriaBuilder.equal(variantJoin.get("value"), color);
        };
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, ProductAvailableVariant> availableVariantJoin = root.join("productAvailableVariants");
            return criteriaBuilder.greaterThanOrEqualTo(availableVariantJoin.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, ProductAvailableVariant> availableVariantJoin = root.join("productAvailableVariants");
            return criteriaBuilder.lessThanOrEqualTo(availableVariantJoin.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("name"), categoryName);
        };
    }

    public static Specification<Product> hasStyle(String styleName) {
        return (root, query, criteriaBuilder) -> {
            if (styleName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("style").get("name"), styleName);
        };
    }

    public static Specification<Product> hasCollectionType(Collections collectionType) {
        return (root, query, criteriaBuilder) -> {
            if (collectionType == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, CollectionProduct> collectionJoin = root.join("collectionProducts");
            return criteriaBuilder.equal(collectionJoin.get("collection").get("collectionType"), collectionType);
        };
    }

    public static Specification<Product> sortBy(String sortBy, String sortOrder) {
        return (root, query, criteriaBuilder) -> {
            String finalSortBy = (sortBy == null || sortBy.isEmpty()) ? "id" : sortBy;
            String finalSortOrder = (sortOrder == null || sortOrder.isEmpty()) ? "asc" : sortOrder;

            List<Order> orders = new ArrayList<>();

            switch (finalSortBy) {
                case "rating" -> {
                    Subquery<Double> subquery = Objects.requireNonNull(query).subquery(Double.class);
                    Root<Review> reviewRoot = subquery.from(Review.class);
                    subquery.select(criteriaBuilder.avg(reviewRoot.get("stars")))
                            .where(criteriaBuilder.equal(reviewRoot.get("product"), root));
                    Order order = finalSortOrder.equals("asc")
                            ? criteriaBuilder.asc(subquery)
                            : criteriaBuilder.desc(subquery);
                    orders.add(order);
                }
                case "price" -> {
                    Order order = finalSortOrder.equals("asc")
                            ? criteriaBuilder.asc(root.join("productAvailableVariants").get("price"))
                            : criteriaBuilder.desc(root.join("productAvailableVariants").get("price"));
                    orders.add(order);
                }
                case "id" -> {
                    Order order = finalSortOrder.equals("asc")
                            ? criteriaBuilder.asc(root.get("id"))
                            : criteriaBuilder.desc(root.get("id"));
                    orders.add(order);
                }
            }

            Objects.requireNonNull(query).orderBy(orders);
            return criteriaBuilder.conjunction();
        };
    }
}
