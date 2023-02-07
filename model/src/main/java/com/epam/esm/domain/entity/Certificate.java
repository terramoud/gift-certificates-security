package com.epam.esm.domain.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents relevant entity from database's table
 *
 * @author Oleksadr Koreshev
 * @since 1.0
 */

@Component
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificates")
public class Certificate extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "last_update_date", nullable = false)
    private LocalDateTime lastUpdateDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE})
    @JoinTable(name = "certificates_tags",
            joinColumns = @JoinColumn(name = "certificate_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    private Set<Tag> tags = new HashSet<>();

    public Certificate(Long id,
                       String name,
                       String description,
                       BigDecimal price,
                       Integer duration,
                       LocalDateTime createDate,
                       LocalDateTime lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public void mergeTags(Set<Tag> newTags) {
        Set<Long> newTagsIds = newTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Set<Tag> duplicatedTags = this.tags.stream()
                .filter(sourceTag -> newTagsIds.contains(sourceTag.getId()))
                .collect(Collectors.toSet());
        this.tags.removeAll(duplicatedTags);
        this.tags.addAll(newTags);
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.lastUpdateDate = createDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
    }
}
