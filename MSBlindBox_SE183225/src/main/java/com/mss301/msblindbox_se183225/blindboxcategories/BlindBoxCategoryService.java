package com.mss301.msblindbox_se183225.blindboxcategories;

import java.util.List;

public interface BlindBoxCategoryService {
    List<BlindBoxCategoryDTO> findAll();

    BlindBoxCategoryDTO findById(Integer id);
}
