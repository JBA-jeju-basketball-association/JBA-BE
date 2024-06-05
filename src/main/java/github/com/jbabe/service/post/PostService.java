package github.com.jbabe.service.post;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.post.PostJpa;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFileJpa;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.postImg.PostImgJpa;
import github.com.jbabe.repository.user.UserJpa;
import github.com.jbabe.service.exception.BadRequestException;
import github.com.jbabe.service.exception.NotAcceptableException;
import github.com.jbabe.service.exception.NotFoundException;
import github.com.jbabe.service.mapper.PostMapper;
import github.com.jbabe.service.storage.StorageService;
import github.com.jbabe.service.userDetails.CustomUserDetails;
import github.com.jbabe.web.dto.post.PostModifyDto;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostJpa postJpa;
    private final UserJpa userJpa;
    private final PostImgJpa postImgJpa;
    private final PostAttachedFileJpa postAttachedFileJpa;
    private final StorageService storageService;

    public Map<String, Object> getAllPostsList(Pageable pageable, String category) {
        Post.Category categoryEnum = Post.Category.pathToEnum(category);
        List<Post> posts = postJpa
                .findByIsAnnouncementTrueAndPostStatusAndCategory(Post.PostStatus.NORMAL, categoryEnum);
        Page<Post> pagePosts = postJpa
                .findByIsAnnouncementFalseAndPostStatusAndCategory(pageable, Post.PostStatus.NORMAL, categoryEnum);
        if(!(pageable.getPageNumber() ==0) && pageable.getPageNumber()+1>pagePosts.getTotalPages()) throw new NotFoundException("Page Not Found", pageable.getPageNumber());
        List<PostsListDto> postsListDto = new ArrayList<>();
        for(Post post: posts) postsListDto.add(PostMapper.INSTANCE.PostToPostsListDto(post));
        for(Post post: pagePosts) postsListDto.add(PostMapper.INSTANCE.PostToPostsListDto(post));

        return Map.of(
                "posts", postsListDto,
                "totalPosts", pagePosts.getTotalElements()+posts.size(),
                "totalPages", pagePosts.getTotalPages()
        );
    }

    @Transactional
    public PostResponseDto getPostByPostId(String category, Integer postId) {

        Post post = postJpa.findByIdUrlsJoin(Post.Category.pathToEnum(category), postId).orElseThrow(
                ()-> new NotFoundException("Post Not Found", postId));

        post.increaseViewCount();

        return PostMapper.INSTANCE.PostToPostResponseDto(post);
    }

    @Transactional
    public boolean createPost(PostReqDto postReqDto, String category, List<FileDto> files, boolean isOfficial) {
        Post.Category categoryEnum = Post.Category.pathToEnum(category);
        ////테스트 임시 작성자임의 등록
        Post post = PostMapper.INSTANCE.PostRequestToPostEntity(postReqDto, categoryEnum, userJpa.findById(5).orElseThrow(()->
                new NotFoundException("User Not Found", 5)),isOfficial);
        post.addFiles(files, postReqDto.getPostImgs());
        try{
            postJpa.save(post);
        }catch (DataIntegrityViolationException sqlException){
            throw new BadRequestException("DB에 반영하는데 실패하였습니다. (제목이 중복됐을 가능성이 있습니다.)  "+sqlException.getMessage(),postReqDto.getTitle());
        }
        return true;

    }

    @Transactional
    public boolean updatePost(PostModifyDto postModifyDto, Integer postId, List<FileDto> newFiles, boolean isOfficial, CustomUserDetails customUserDetails) {
        Integer userId = Optional.ofNullable(customUserDetails)
                .map(CustomUserDetails::getUserId)
                .orElse(5);

        Post originPost = postJpa.findById(postId).orElseThrow(
                ()-> new NotFoundException("Post Not Found", postId));

        if (!userId.equals(originPost.getUser().getUserId()))
            throw new NotAcceptableException("로그인한 유저의 정보와 게시글 작성자 정보가 다름", String.valueOf(postId));

        originPost.notifyAndEditSubjectLineContent(postModifyDto, newFiles, isOfficial);

        return true;
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
}
