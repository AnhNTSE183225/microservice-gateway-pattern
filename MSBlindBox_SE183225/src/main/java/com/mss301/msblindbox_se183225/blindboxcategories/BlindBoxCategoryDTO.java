package com.mss301.msblindbox_se183225.blindboxcategories;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlindBoxCategoryDTO {
    Integer id;
    String name;
    String description;
    String rarityLevel;
    String priceRange;
}
