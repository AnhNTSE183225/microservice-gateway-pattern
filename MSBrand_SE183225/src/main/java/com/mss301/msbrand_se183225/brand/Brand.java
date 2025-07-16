package com.mss301.msbrand_se183225.brand;

import com.mss301.msbrand_se183225.blindbox.BlindBox;
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
@Table(name = "Brand")
public class Brand {

    @Id
    @Column(name = "BrandID")
    Integer id;

    @Column(name = "BrandName", columnDefinition = "VARCHAR(100)")
    String name;

    @Column(name = "CountryOfOrigin", columnDefinition = "VARCHAR(100)")
    String countryOfOrigin;

    @OneToMany(mappedBy = "brand")
    List<BlindBox> blindBoxes;
}
