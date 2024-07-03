package com.hollroom.mypage.dao;

import com.hollroom.community.domain.entity.CommunityEntity;
import com.hollroom.mypage.repository.MywriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MywriteDAOImpl implements MywriteDAO {
    @Autowired
    private MywriteRepository mywriteRepository;


    @Override
    public Page<CommunityEntity> findByUserIdAndCategory(Long userId, String category, Pageable pageable) {
        if ("all".equals(category)) {
            return mywriteRepository.findByUserId(userId, pageable);
        } else {
            return mywriteRepository.findByUserIdAndCategory(userId, category, pageable);
        }
    }
}