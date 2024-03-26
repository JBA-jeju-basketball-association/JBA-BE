package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
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
    @Mapping(target = "postAttachedFiles", qualifiedByName = "getPostAttachedFiles")
    @Mapping(target = "title", source = "name")
    PostResponseDto PostToPostResponseDto(Post post);

    @Named("getImgUrl")
    default List<String> imgUrl(Set<PostImg> postImgs){
        return postImgs.stream().map(pI->pI.getImgUrl()).toList();
    }
    @Named("getPostAttachedFiles")
    default List<String> postAttachedFile(Set<PostAttachedFile> postAttachedFiles){
        return postAttachedFiles.stream().map(pAF->pAF.getFilePath()).toList();
    }
}
