package com.hollroom.admin.service;

import com.hollroom.admin.domain.dto.InquiryListDTO;

import java.util.List;

public interface InquiryListService {
    //문의하기 전체 목록보기
    List<InquiryListDTO> selectInquiry();
    //문의하기 글 전체 개수
    Long selectInquiryCount();
    //문의하기 답변 개수
    Long selectInquiryAnswerCount();
    //문의하기 글 상세보기
    InquiryListDTO selectInquiryByPostId(Long postId);
    //문의하기답변 등록
    void updateInquiryAnswer(InquiryListDTO inquiry);
}
