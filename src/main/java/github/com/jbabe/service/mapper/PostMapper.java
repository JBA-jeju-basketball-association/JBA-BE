package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);


    @Mapping(target = "title", source = "name")
    @Mapping(target = "writer", source = "post.user.name")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    PostsListDto PostToPostsListDto(Post post);

    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "writer", source = "post.user.name")
    @Mapping(target = "postImgs",qualifiedByName = "getImgUrl")
    @Mapping(target = "files", source = "postAttachedFiles", qualifiedByName = "getPostAttachedFiles")
    @Mapping(target = "title", source = "name")
    PostResponseDto PostToPostResponseDto(Post post);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "name", source = "postReqDto.title")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "isAnnouncement", source = "isOfficial")
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "postImgs", ignore = true)
    Post PostRequestToPostEntity(PostReqDto postReqDto, Post.Category category, User user, boolean isOfficial);

    @Named("getImgUrl")
    default List<FileDto> imgUrl(Set<PostImg> postImgs){
        //fileName과 imgUrl을 각각 변수에 삽입하여 FileDto 객체를 생성하여 반환
        return postImgs.stream().map(pI->new FileDto(pI.getFileName(),pI.getImgUrl())).toList();
    }
    @Named("getPostAttachedFiles")
    default List<FileDto> postAttachedFile(Set<PostAttachedFile> postAttachedFiles){
        return postAttachedFiles.stream().map(pAF->new FileDto(pAF.getFileName(),pAF.getFilePath())).toList();
    }

}
