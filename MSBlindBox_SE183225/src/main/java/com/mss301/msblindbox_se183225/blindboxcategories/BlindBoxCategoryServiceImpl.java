package com.mss301.msblindbox_se183225.blindboxcategories;

import com.mss301.msblindbox_se183225.externalsecurity.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindBoxCategoryServiceImpl implements BlindBoxCategoryService {

    private final BlindBoxCategoryRepository blindBoxCategoryRepository;

    @Override
    public List<BlindBoxCategoryDTO> findAll() {
        return blindBoxCategoryRepository.findAll().stream().map(
                bc -> BlindBoxCategoryDTO.builder()
                        .id(bc.getId())
                        .name(bc.getName())
                        .description(bc.getDescription())
                        .rarityLevel(bc.getRarityLevel())
                        .priceRange(bc.getPriceRange())
                        .build()
        ).toList();
    }

    @Override
    public BlindBoxCategoryDTO findById(Integer id) {
        BlindBoxCategory bc = blindBoxCategoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Blind box category not found with id: " + id));
        return BlindBoxCategoryDTO.builder()
                .id(bc.getId())
                .name(bc.getName())
                .description(bc.getDescription())
                .rarityLevel(bc.getRarityLevel())
                .priceRange(bc.getPriceRange())
                .build();
    }
}
