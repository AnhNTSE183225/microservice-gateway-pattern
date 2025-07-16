package com.mss301.msblindbox_se183225.blindbox;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlindBoxCreateRequest {
    Integer id;
    String name;
    Integer categoryId;
    Integer brandId;
    String rarity;
    Double price;
    LocalDate releaseDate;
    Integer stock;
}
