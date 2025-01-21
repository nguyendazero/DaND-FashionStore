package com.haibazo_bff_its_rct_webapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount")
public class Discount extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "date_end_sale")
    private LocalDateTime dateEndSale;

    @OneToMany(mappedBy = "discount")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "discount")
    private List<ProductAvailableVariant> productAvailableVariants = new ArrayList<>();

}
