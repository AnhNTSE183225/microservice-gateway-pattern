package com.mss301.msblindbox_se183225.blindbox;

import com.mss301.msblindbox_se183225.blindboxcategories.BlindBoxCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "BlindBoxes")
public class BlindBox {

    @Id
    @Column(name = "BlindBoxID")
    Integer id;

    @Column(name = "Name")
    String name;

    @ManyToOne
    @JoinColumn(name = "CategoryID")
    BlindBoxCategory category;

    @Column(name = "BrandID")
    Integer brandId;

    @Column(name = "Rarity", columnDefinition = "VARCHAR(50)")
    String rarity;

    @Column(name = "Price", columnDefinition = "DECIMAL(10,2)")
    Double price;

    @Column(name = "ReleaseDate", columnDefinition = "DATE")
    LocalDate releaseDate;

    @Column(name = "Stock", columnDefinition = "INT")
    Integer stock;
}
