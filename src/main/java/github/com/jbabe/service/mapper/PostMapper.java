package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.post.ManagePostsDto;
import github.com.jbabe.web.dto.post.PostReqDto;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import github.com.jbabe.web.dto.storage.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);


    @Mapping(target = "title", source = "name")
    @Mapping(target = "writer", source = "post.user.name")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    PostsListDto PostToPostsListDto(Post post);

    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "writer", source = "post.tempWriterName")
    @Mapping(target = "files", source = "postAttachedFiles")
    @Mapping(target = "title", source = "name")
    PostResponseDto PostToPostResponseDto(Post post);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "name", source = "postReqDto.title")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "isAnnouncement", source = "isOfficial")
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "postImgs", ignore = true)
    Post PostRequestToPostEntity(PostReqDto postReqDto, Post.Category category, User user, boolean isOfficial);

    // TODO: 관리자페이지 작업중 썸네일가져오는 로직필요
    @Mapping(target = "title", source = "name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "files", source = "postAttachedFiles")
    @Mapping(target = "thumbnail", source = "postImgs", qualifiedByName = "getThumbnail")
    @Mapping(target = "postStatus", expression = "java(post.getPostStatus().getPath())")
    @Mapping(target = "category", expression = "java(post.getCategory().getPath())")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "deleteAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "content", qualifiedByName = "getMinusTagValues")
    ManagePostsDto PostToManagePostsDto(Post post);

//    @Named("getImgUrl")
//    default List<FileDto> imgUrl(Set<PostImg> postImgs){
//        //fileName과 imgUrl을 각각 변수에 삽입하여 FileDto 객체를 생성하여 반환
//        return postImgs.stream().map(pI->new FileDto(pI.getFileName(),pI.getImgUrl())).toList();
//    }
    @Named("getThumbnail")
    default String getThumbnail(List<PostImg> postImgs){
        return postImgs!=null?postImgs.stream().findFirst().map(PostImg::getImgUrl).orElse(null):null;
    }
    @Named("getMinusTagValues")
    default String getMinusTagValues (String content){
        String textContent = content.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();

        return textContent.length() > 50 ? textContent.substring(0, 50) + "..." : textContent;
    }
//    @Named("getPostAttachedFiles")
//    default List<FileDto> postAttachedFile(Set<PostAttachedFile> postAttachedFiles){
//        return postAttachedFiles.stream().map(pAF->new FileDto(pAF.getFileName(),pAF.getFilePath())).toList();
//    }

    @Mapping(target = "fileId", source = "postAttachedFileId")
    @Mapping(target = "fileUrl", source = "filePath")
    FileDto postAttachedFileToFileDto(PostAttachedFile postAttachedFile);

    List<FileDto> postAttachedFilesToFileDtos(List<PostAttachedFile> postAttachedFiles);

    @Mapping(target = "fileId", source = "postImgId")
    @Mapping(target = "fileUrl", source = "imgUrl")
    FileDto postImgToFileDto(PostImg postImg);
    List<FileDto> postImgsToFileDtos(List<PostImg> postImgs);

}
