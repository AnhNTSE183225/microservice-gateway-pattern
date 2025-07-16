package com.mss301.msbrand_se183225.blindbox;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BlindBoxDTO(
        Integer id,
        String name,
        Integer categoryId,
        String categoryName,
        Integer brandId,
        String rarity,
        Double price,
        LocalDate releaseDate,
        Integer stock
) {
}
