package com.hollroom.monthly.dao;

import com.hollroom.monthly.domain.entity.MonthlyProductEntity;
import com.hollroom.monthly.repository.MonthlyProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MonthlyProductDAOImpl implements MonthlyProductDAO {
    private final MonthlyProductRepository productRepo;

    @Override
    public void insertProduct(MonthlyProductEntity entity) {
        productRepo.save(entity);
    }

    @Override
    public Page<MonthlyProductEntity> readProductAll(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    @Override
    public Page<MonthlyProductEntity> readDivisionProduct(Long divisionCode, Pageable pageable) {
        return productRepo.findByDivisionCode(divisionCode,pageable);
    }

    @Override
    public Page<MonthlyProductEntity> readDivisionProducts(List<Long> divisionCodes, Pageable pageable) {
        return productRepo.findByDivisionCodeIn(divisionCodes,pageable);
    }

    @Override
    public MonthlyProductEntity readProduct(Long id) {
        return productRepo.findById(id).orElse(null);
    }
}
