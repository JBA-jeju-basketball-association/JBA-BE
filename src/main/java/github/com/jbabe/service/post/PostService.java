package github.com.jbabe.service.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.post.PostJpaDao;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFileJpa;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.postImg.PostImgJpa;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.SearchQueryParamUtil;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.PostMapper;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import github.com.jbabe.web.dto.myPage.MyPage;
import github.com.jbabe.web.dto.post.*;
import github.com.jbabe.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostJpaDao postJpa;
    private final UserJpa userJpa;
    private final PostImgJpa postImgJpa;
    private final PostAttachedFileJpa postAttachedFileJpa;
    private final StorageService storageService;

/* 키워드검색이랑 통합
    public MyPage<PostsListDto> getAllPostsList(Pageable pageable, String category) {

        Post.Category categoryEnum = Post.Category.pathToEnum(category);



        List<Post> announcementPosts = postDaoQueryDsl.getAnnouncementPosts(categoryEnum, pageable.getSort());
        Page<Post> generalPosts = postJpa
                .findByIsAnnouncementFalseAndPostStatusAndCategory(Post.PostStatus.NORMAL, categoryEnum, pageable);

        return getReturnContents(pageable, announcementPosts, generalPosts);

    }*/



    @Transactional
    public PostResponseDto getPostByPostId(String category, Integer postId) {

//        Post post = postJpa.findByIdUrlsJoin(Post.Category.pathToEnum(category), postId).orElseThrow(
//                ()-> new NotFoundException("Post Not Found", postId));
        PostResponseDto postResponse = postJpa.getPostJoinFiles(postId);
//        post.increaseViewCount();

        return postResponse;
    }

    @Transactional
    public boolean createPost(PostReqDto postReqDto, String category, List<FileDto> files, boolean isOfficial, CustomUserDetails customUserDetails) {
        Post.Category categoryEnum = Post.Category.pathToEnum(category);
        Post post = PostMapper.INSTANCE.PostRequestToPostEntity(postReqDto, categoryEnum, userJpa.findById(customUserDetails.getUserId()).orElseThrow(()->
                new NotFoundException("User Not Found", customUserDetails.getUserId())),isOfficial);
        post.addFiles(files, postReqDto.getPostImgs());
        try{
            postJpa.save(post);
        }catch (DataIntegrityViolationException sqlException){
            throw new ConflictException("Title Duplication",postReqDto.getTitle());
        }catch (JpaSystemException jpaSystemException){
            throw new BadRequestException("Missing A Required Value", postReqDto);
        }
        catch (Exception e){
            throw new BadRequestException("Post Creation Failed", postReqDto);
        }
        return true;
    }

    @Transactional
    public boolean updatePost(PostModifyDto postModifyDto, Integer postId, List<FileDto> newFiles, Boolean isOfficial, CustomUserDetails customUserDetails) {
//        Integer userId = Optional.ofNullable(customUserDetails)
//                .map(CustomUserDetails::getUserId)
//                .orElse(5);

        Post originPost = postJpa.findById(postId).orElseThrow(
                ()-> new NotFoundException("Post Not Found", postId));

        //이미지
        List<FileDto> remainingImgs = postModifyDto.getPostImgs();
        checkAndDeleteImages(originPost, remainingImgs);
        if(!CollectionUtils.isEmpty(remainingImgs)&&!CollectionUtils.isEmpty(originPost.getPostImgs())){
            remainingImgs.removeIf(imgs->originPost.getPostImgs().stream()
                    .anyMatch(existingImage-> existingImage.getImgUrl().equals(imgs.getFileUrl())));
        }

        //첨부파일
        List<FileDto> remainingFiles = postModifyDto.getRemainingFiles();
        checkAndDeleteFiles(originPost, remainingFiles);
        if(!CollectionUtils.isEmpty(remainingFiles)&&!CollectionUtils.isEmpty(originPost.getPostAttachedFiles())){
            remainingFiles.removeIf(files->originPost.getPostAttachedFiles().stream()
                    .anyMatch(existingFile-> existingFile.getFilePath().equals(files.getFileUrl())));
        }


        //기존 파일에 새로 추가된 파일들을 추가
        if (newFiles != null) {
            if (remainingFiles == null) {
                remainingFiles = new ArrayList<>();
            }
            remainingFiles.addAll(newFiles);
        }



//        if (!userId.equals(originPost.getUser().getUserId()))
//            throw new NoAcceptableException("로그인한 유저의 정보와 게시글 작성자 정보가 다름", String.valueOf(postId));
        originPost.notifyAndEditSubjectLineContent(postModifyDto, isOfficial);

        return true;


    }

    private void checkAndDeleteFiles(Post originPost, List<FileDto> remainingFiles) {
        List<PostAttachedFile> originPostPostAttachedFiles = originPost.getPostAttachedFiles();
        //기존 파일들 중 삭제된 파일들을 찾아 삭제
        if(!CollectionUtils.isEmpty(remainingFiles) && !CollectionUtils.isEmpty(originPostPostAttachedFiles)){
            List<PostAttachedFile> filesToBeDeleted = getFilesToBeDeleted(originPostPostAttachedFiles, remainingFiles);
            originPost.getPostAttachedFiles().removeAll(filesToBeDeleted);
            postAttachedFileJpa.deleteAll(filesToBeDeleted);
        }//남긴 파일이 없을경우 기존 파일들을 모두 삭제
        else if(!CollectionUtils.isEmpty(originPostPostAttachedFiles)){
            originPost.getPostAttachedFiles().clear();
            postAttachedFileJpa.deleteAll(originPostPostAttachedFiles);
            storageService.uploadCancel(originPostPostAttachedFiles.stream()
                    .map(PostAttachedFile::getFilePath).toList());
        }
    }

    private void checkAndDeleteImages(Post originPost, List<FileDto> remainingImgs) {
        List<PostImg> originImgs = originPost.getPostImgs();
        //기존 이미지 중 삭제된 파일들을 찾아 삭제
        if(!CollectionUtils.isEmpty(remainingImgs) && !CollectionUtils.isEmpty(originImgs)){
            List<PostImg> imgsToBeDeleted = getImagesToBeDeleted(originImgs, remainingImgs);
            originPost.getPostImgs().removeAll(imgsToBeDeleted);
            postImgJpa.deleteAll(imgsToBeDeleted);
        }//남긴 이미지가 없을경우 기존 이미지들을 모두 삭제
        else if(!CollectionUtils.isEmpty(originImgs)){
            originPost.getPostImgs().clear();
            postImgJpa.deleteAll(originImgs);
            storageService.uploadCancel(originImgs.stream()
                    .map(PostImg::getImgUrl).toList());
        }
    }

    private List<PostImg> getImagesToBeDeleted(List<PostImg> originImgs, List<FileDto> remainingImgs) {
        List<PostImg> imgsToBeDeleted = new ArrayList<>();
//        for(PostImg postImg: originImgs){
//            if(remainingImgs.stream().noneMatch(f->f.getFileUrl() != null && f.getFileUrl().equals(postImg.getImgUrl()))){
//                imgsToBeDeleted.add(postImg);
//            }
//        }
        if(!imgsToBeDeleted.isEmpty()) storageService.uploadCancel(imgsToBeDeleted.stream()
                .map(PostImg::getImgUrl).toList());
        return imgsToBeDeleted;
    }

    private List<PostAttachedFile> getFilesToBeDeleted(List<PostAttachedFile> originFiles, List<FileDto> remainingFiles) {
        List<PostAttachedFile> filesToBeDeleted = new ArrayList<>();
        for(PostAttachedFile postAttachedFile: originFiles){
            if(remainingFiles.stream().noneMatch(f->f.getFileUrl().equals(postAttachedFile.getFilePath()))){
                filesToBeDeleted.add(postAttachedFile);
            }
        }
        if(!filesToBeDeleted.isEmpty()) storageService.uploadCancel(filesToBeDeleted.stream()
                .map(PostAttachedFile::getFilePath).toList());
        return filesToBeDeleted;
    }

    @Transactional
    public void deletePost(Integer postId) {
        if (!postJpa.existsById(postId))
            throw new NotFoundException("Post Not Found", postId);

        deletePostAssociatedData(postId);

        postJpa.deleteById(postId);
    }
    public void deletePostAssociatedData(Integer postId) {
        List<PostImg> imagesToDelete = postImgJpa.findAllByPostPostId(postId);
        List<PostAttachedFile> filesToDelete = postAttachedFileJpa.findAllByPostPostId(postId);
        if(!imagesToDelete.isEmpty()) storageService.uploadCancel(imagesToDelete.stream()
                .map(PostImg::getImgUrl).toList());
        if(!filesToDelete.isEmpty()) storageService.uploadCancel(filesToDelete.stream()
                .map(PostAttachedFile::getFilePath).toList());
    }

    @Transactional(readOnly = true)
    public MyPage<PostsListDto> searchPostList(Pageable pageable, String category, String keyword) {

        if(keyword!=null&&keyword.length() == 1) throw new BadRequestException("검색어는 2글자 이상이어야 합니다.", keyword);
        Post.Category categoryEnum = Post.Category.pathToEnum(category);
        Page<Post> postList = postJpa.searchPostList(keyword, categoryEnum, pageable);

        return getReturnContentsNew(pageable, postList);
    }
    private MyPage<PostsListDto> getReturnContentsNew(Pageable pageable, Page<Post> generalPosts) {
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>generalPosts.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<PostsListDto> postsListDto = generalPosts.stream()
                .map(PostMapper.INSTANCE::PostToPostsListDto).toList();

        return MyPage.<PostsListDto>builder()
                .type(PostsListDto.class)
                .content(postsListDto)
                .totalElements(generalPosts.getTotalElements())
                .totalPages( generalPosts.getTotalPages())
                .build();
    }

    private MyPage<PostsListDto> getReturnContents(Pageable pageable, List<Post> announcementPosts, Page<Post> generalPosts) {
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>generalPosts.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<PostsListDto> postsListDto = Stream.concat(
                        announcementPosts.stream(),
                        generalPosts.stream()
                )
                .map(PostMapper.INSTANCE::PostToPostsListDto)
                .toList();

        return MyPage.<PostsListDto>builder()
                .type(PostsListDto.class)
                .content(postsListDto)
                .totalElements(generalPosts.getTotalElements()+announcementPosts.size())
                .totalPages( generalPosts.getTotalPages())
                .build();
    }

    @Transactional
    public void updateIsAnnouncement(int postId) {
        Post post = postJpa.findById(postId).orElseThrow(
                ()-> new NotFoundException("Post Not Found", postId));
        post.updateIsAnnouncement();
    }

//    @Transactional(readOnly = true)
    public MyPage<ManagePostsDto> getManagePostsList(Pageable pageable, String keyword, SearchCriteriaEnum searchCriteria, Post.Category categoryEnum, LocalDate startDate, LocalDate endDate) {
        SearchQueryParamUtil.validateAndAdjustDates(keyword, searchCriteria, startDate, endDate);
        startDate = startDate == null ? LocalDate.of(2024,1,1) : startDate;
        endDate = endDate == null ? LocalDate.now().plusDays(1) : endDate.plusDays(1);


        Page<Post> postList = postJpa.getPostsListFileFetch(pageable, keyword, searchCriteria, categoryEnum, startDate, endDate);
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>postList.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        return MyPage.<ManagePostsDto>builder()
                .type(ManagePostsDto.class)
                .content(postList.stream()
                        .map(PostMapper.INSTANCE::PostToManagePostsDto)
                        .toList())
                .totalElements(postList.getTotalElements())
                .totalPages( postList.getTotalPages())
                .build();


    }
}
