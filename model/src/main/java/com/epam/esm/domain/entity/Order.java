package com.epam.esm.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The Order entity represents a purchase made by a
 * user for a gift certificate.
 * The order contains information about the certificate
 * being purchased, the cost of the purchase,
 * the user making the purchase, and the creation date of the order.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = -3312407588923463088L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cost", nullable = false, updatable = false)
    private BigDecimal cost;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP", updatable = false)
    private LocalDateTime createDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "gift_certificate_id", nullable = false)
    private Certificate certificate;

    /**
     * Sets the create date to the current time
     * before the order is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }
}