package com.epam.esm.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "gift_certificate_id", nullable = false)
    private Certificate certificate;

    public Order(Long id, BigDecimal cost, LocalDateTime createDate, User user, Certificate certificate) {
        this.id = id;
        this.cost = cost;
        this.createDate = createDate;
        this.user = user;
        this.certificate = certificate;
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }
}