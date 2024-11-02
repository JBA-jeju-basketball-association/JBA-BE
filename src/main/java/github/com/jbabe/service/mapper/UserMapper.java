package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.manageUser.ManageUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mapping(target = "dateOfBirth", dateFormat = "yyMMdd")
//    @Mapping(target = "gender", source = "gender.path")
    @Mapping(target = "loginAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "deleteAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "lockAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "permission", source = "role.path")
    @Mapping(target = "userStatus", source = "userStatus.path")
    ManageUserDto UserToManageUserDto(User user);
}
