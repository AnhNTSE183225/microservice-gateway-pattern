package com.mss301.msblindbox_se183225.blindboxcategories;

import com.mss301.msblindbox_se183225.blindbox.BlindBox;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "BlindBoxCategories")
public class BlindBoxCategory {

    @Id
    @Column(name = "CategoryID")
    Integer id;

    @Column(name = "CategoryName", columnDefinition = "VARCHAR(255)")
    String name;

    @Column(name = "Description", columnDefinition = "TEXT")
    String description;

    @Column(name = "RarityLevel", columnDefinition = "VARCHAR(50)")
    String rarityLevel;

    @Column(name = "PriceRange", columnDefinition = "VARCHAR(100)")
    String priceRange;

    @OneToMany(mappedBy = "category")
    List<BlindBox> blindBoxes;
}
