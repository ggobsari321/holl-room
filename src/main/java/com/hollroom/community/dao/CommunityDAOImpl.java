package com.hollroom.community.dao;

import com.hollroom.common.TabType;
import com.hollroom.community.domain.dto.CommunityPagingEntityDTO;
import com.hollroom.community.domain.entity.AttachFileEntity;
import com.hollroom.community.domain.entity.CommentsEntity;
import com.hollroom.community.domain.entity.CommunityEntity;
import com.hollroom.community.domain.entity.HeartEntity;
import com.hollroom.community.repository.AttachFileRepository;
import com.hollroom.community.repository.CommentsRepository;
import com.hollroom.community.repository.CommunityRepository;
import com.hollroom.community.repository.HeartRepository;
import com.hollroom.user.entity.UserEntity;
import com.hollroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommunityDAOImpl implements CommunityDAO{
    private final CommunityRepository communityRepository;
    private final AttachFileRepository attachFileRepository;
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final HeartRepository heartRepository;

    @Override
    public Long insert(CommunityEntity entity) {
       return communityRepository.save(entity).getPostId();
    }

    @Override
    public void insertFile(AttachFileEntity entity) {
        attachFileRepository.save(entity);
    }

//    @Override
//    public void insertFile(List<AttachFileEntity> fileEntityList) {
//        for (AttachFileEntity attachFileEntity : fileEntityList) {
//            attachFileRepository.save(attachFileEntity);
//        }
//    }

    //삭제 처리된 게시글은 가져오지 않게
    @Override
    public CommunityPagingEntityDTO pagingFindAll(String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
        //삭제된 게시글은 불러오지 않도록 메소드 변경
        Page<CommunityEntity> pages = communityRepository.findByDeleted(null,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());

        //큰 DTO를 리턴
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO pagingFindCategory(String category, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
        //삭제된 게시글은 불러오지 않도록 메소드 변경
        Page<CommunityEntity> pages = communityRepository.findByCategoryAndDeleted(category,null,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityEntity findById(Long postId) {
        return communityRepository.findByPostId(postId);
    }

    @Override
    public List<AttachFileEntity> getFiles(Long postId, TabType tab) {
//        System.out.println("read 탭타입 수정++++++++++++++++++++++++++++++++++++++++++++++++");
        return attachFileRepository.findByPostIdAndTabType(postId,tab);
    }

    @Override
    public void updateViewCounting(Long postId) {
        CommunityEntity entity = communityRepository.findByPostId(postId);
//        System.out.println("조회수 수정 전::"+entity.getViewCount());
        entity.setViewCount(entity.getViewCount()+1);
        communityRepository.save(entity);

    }

    @Override
    public void update(CommunityEntity entity) {
        //제목, 카테고리, 본문내용
        CommunityEntity updateEntity = communityRepository.findByPostId(entity.getPostId());
        updateEntity.setTitle(entity.getTitle());
        updateEntity.setCategory(entity.getCategory());
        updateEntity.setContent(entity.getContent());
        communityRepository.save(updateEntity);

    }

    @Override
    public void delete(Long postId) {
        //해당 postId로 해당 엔티티 정보를 조회해서 해당 엔티티 정보를 업데이트 삭제여부 및 삭제날짜 활성화
        CommunityEntity deleteEntity = communityRepository.findByPostId(postId);
        deleteEntity.setDeleted("true"); //true라는 스트링값이 있으면 '삭제'라는 의미
        deleteEntity.setDeletedAt(deleteEntity.getUpdatedAt());

        communityRepository.save(deleteEntity);
    }

    @Override
    public void commentInsert(CommentsEntity commentsEntity) {
        commentsRepository.save(commentsEntity);
    }

    @Override
    public List<CommentsEntity> getComments(Long postId) {
        //삭제된 댓글은 가져오지 않도록 수정
//        List<CommentsEntity> commentsEntityList = commentsRepository.findByPostId(postId);
        List<CommentsEntity> commentsEntityList = commentsRepository.findByPostIdAndDeletedCom(postId,null);
        return commentsEntityList;
    }

    @Override
    public void commentUpdate(CommentsEntity commentsEntity) {
        CommentsEntity updateEntity = commentsRepository.findByCommentId(commentsEntity.getCommentId());
        updateEntity.setComments(commentsEntity.getComments());
        commentsRepository.save(updateEntity);
    }

    @Override
    public void commentDelete(Long commentId) {
        CommentsEntity deleteEntity = commentsRepository.findByCommentId(commentId);
        deleteEntity.setDeletedCom("true"); //true라는 스트링값이 있으면 삭제처리되었다는 의미
        deleteEntity.setDeleteAt(deleteEntity.getDeleteAt());

        commentsRepository.save(deleteEntity);
    }

    @Override
    public List<CommunityEntity> searchSimple(String content) {
        return communityRepository.findByContentContaining(content);
    }

    @Override
    public List<CommunityEntity> search(CommunityEntity entity) {
        return communityRepository.findByCategoryAndContentContaining(entity.getCategory(),entity.getContent());
    }
    //조건 검색 메소드들
    @Override
    public CommunityPagingEntityDTO deepSearchTitle(String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");

        Page<CommunityEntity> pages = communityRepository.findByDeletedAndTitleContaining(null,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchContent(String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");

        Page<CommunityEntity> pages = communityRepository.findByDeletedAndContentContaining(null,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchWriter(String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = null;
        //먼저 작성자 닉네임을 가진 유저 아이디를 찾는다.
         UserEntity userEntity = userRepository.findByUserNickname(content).orElse(null);

        // 찾은 유저 아이디로 조회를 해서 페이징 값을 받는다.
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
        Page<CommunityEntity> pages =null;
        if(userEntity!=null){
            pagingEntityDTO = new CommunityPagingEntityDTO();
            Long id = userEntity.getId();
            pages = communityRepository.findByDeletedAndUserId(null,id,pageRequest);
            pagingEntityDTO.setCommunityEntities(pages.getContent());
            pagingEntityDTO.setTotalPages(pages.getTotalPages());
            pagingEntityDTO.setNowPageNumber(pages.getNumber());
            pagingEntityDTO.setPageSize(pages.getSize());
            pagingEntityDTO.setHasNextPage(pages.hasNext());
            pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
            pagingEntityDTO.setFirstPage(pages.isFirst());
            pagingEntityDTO.setLastPage(pages.isLast());

        }

        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchCateTitle(String category, String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");

        Page<CommunityEntity> pages = communityRepository.findByDeletedAndCategoryAndTitleContaining(null,category,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchCateContent(String category, String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");

        Page<CommunityEntity> pages = communityRepository.findByDeletedAndCategoryAndContentContaining(null,category,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchCateWriter(String category, String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = null; //
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
//        System.out.println("++++++++++버깅찾기1+++++++");
        UserEntity userEntity = userRepository.findByUserNickname(content).orElse(null);
//        System.out.println("++++++++++버깅찾기2+++++++");
        if (userEntity != null) {
            Page<CommunityEntity> pages = communityRepository.findByDeletedAndCategoryAndUserId(null, category, userEntity.getId(), pageRequest);
//            System.out.println("++++++++++버깅찾기3+++++++");
            pagingEntityDTO = new CommunityPagingEntityDTO(); //
            pagingEntityDTO.setCommunityEntities(pages.getContent());
            pagingEntityDTO.setTotalPages(pages.getTotalPages());
            pagingEntityDTO.setNowPageNumber(pages.getNumber());
            pagingEntityDTO.setPageSize(pages.getSize());
            pagingEntityDTO.setHasNextPage(pages.hasNext());
            pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
            pagingEntityDTO.setFirstPage(pages.isFirst());
            pagingEntityDTO.setLastPage(pages.isLast());
        }

//        System.out.println("++++++++++버깅찾기4+++++++");
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchTitleAndContent(String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
        Page<CommunityEntity> pages = communityRepository.findByDeletedAndTitleContainingOrContentContaining(null,content,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public CommunityPagingEntityDTO deepSearchCateTitleAndContent(String category, String content, String page) {
        CommunityPagingEntityDTO pagingEntityDTO = new CommunityPagingEntityDTO();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page),10, Sort.Direction.DESC,"postId");
        Page<CommunityEntity> pages = communityRepository.findByDeletedAndCategoryAndTitleContainingOrContentContaining(null,category,content,content,pageRequest);

        pagingEntityDTO.setCommunityEntities(pages.getContent());
        pagingEntityDTO.setTotalPages(pages.getTotalPages());
        pagingEntityDTO.setNowPageNumber(pages.getNumber());
        pagingEntityDTO.setPageSize(pages.getSize());
        pagingEntityDTO.setHasNextPage(pages.hasNext());
        pagingEntityDTO.setHasPreviousPage(pages.hasPrevious());
        pagingEntityDTO.setFirstPage(pages.isFirst());
        pagingEntityDTO.setLastPage(pages.isLast());
        return pagingEntityDTO;
    }

    @Override
    public List<CommunityEntity> findTopByViewCount() {
        List<CommunityEntity> list = communityRepository.findTop3ByOrderByViewCountDesc();
//        System.out.println("상위 조회수 엔티티 매핑확인::"+ list);
        return list;
    }

    @Override
    public boolean countHeart(Long postId, Long UserId) {
        return heartRepository.existsByPostIdAndUserId(postId, UserId);
    }

    @Override
    public void createHeart(Long postId, Long userId, TabType tab, String checkHeart) {
        HeartEntity entity = new HeartEntity(postId,userId,tab,checkHeart);
        heartRepository.save(entity);
    }

    @Override
    public HeartEntity getHeart(Long postId, Long userId, TabType tab) {
        return heartRepository.findByPostIdAndUserIdAndTabType(postId,userId,tab);
    }


}
