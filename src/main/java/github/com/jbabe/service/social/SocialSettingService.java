//package github.com.jbabe.service.social;
//
//import github.com.jbabe.repository.user.UserJpa;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.BeanWrapper;
//import org.springframework.beans.BeanWrapperImpl;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.beans.FeatureDescriptor;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.stream.Stream;
//
//@Service
//@RequiredArgsConstructor
//public class SocialSettingService {
//    private final UserJpa userJpa;
//
//    private final PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public void socialIdSet(UserEntity userEntity, Long socialId) {
//        userEntity.setSocialId(socialId);
//    }
//
//    @Transactional
//    public void makeSocialTemp(SocialAccountDto socialAccountDto) {
//        String dbNick = "(temp)_"+socialAccountDto.getNickName();
//        UserEntity userEntity = UserMapper.INSTANCE.socialAccountDtoToUserEntity(socialAccountDto);
//        userEntity.setNickName(dbNick);
//        userEntity.setEmail("("+socialAccountDto.getProvider()+") "+socialAccountDto.getEmail());
//        userEntity.setSocialId(socialAccountDto.getSocialId());
////        userEntity.setPhoneNumber(socialAccountDto.getSocialId().toString());
//        userEntity.setPassword("Non-Alcoholic");
//        userEntity.setGender(UserEntity.Gender.불명);
//        userEntity.setJoinDate(LocalDateTime.now());
//        userEntity.setDateOfBirth(LocalDate.now());
//        userEntity.setStatus("temp");
//        userEntity.setDeletionDate(LocalDateTime.now().minusDays(6).minusHours(23));
//        userEntity.setFailureCount(0);
//        userJpa.save(userEntity);
//    }
//
//    @Transactional(transactionManager = "tm")
//    public void cancelConnect(UserEntity userEntity) {
//        userEntity.setSocialId(null);
//    }
//
//    @Transactional(transactionManager = "tm")
//    public void applySocialId(UserEntity userEntity, Long socialId) {
//        try {
//            userEntity.setSocialId(socialId-3000000000000000000L);
//        }catch (Exception e){
//            throw new NotAcceptableException("소셜 연결 실패 소셜 아이디를 설정하지 못했습니다.", socialId.toString());
//        }
//    }
//
//    @Transactional(transactionManager = "tm")
//    public void deleteSigningUpAccount(UserEntity userEntity) {
//        try{
//            userJpa.delete(userEntity);
//        } catch (Exception e){
//            throw new NotAcceptableException("소셜 가입 취소 실패", userEntity.getSocialId().toString());
//        }
//    }
//
//    @Transactional(transactionManager = "tm")
//    public void loadSigningUpAccount(UserEntity existingUser, SignUpRequest signUpRequest) {
//        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//        UserEntity updateUser = UserMapper.INSTANCE.signUpRequestToUserEntity(signUpRequest);
//        updateUser.setImageUrl(null);
//        existingUser.setDeletionDate(null);
//        BeanUtils.copyProperties(updateUser, existingUser, getNullPropertyNames(updateUser));
//        Roles roles = rolesJpa.findByName("ROLE_USER");
//        userRolesJpa.save(UserRoles.builder()
//                .userEntity(existingUser)
//                .roles(roles)
//                .build());
//    }
//    private String[] getNullPropertyNames(Object source) {
//        final BeanWrapper src = new BeanWrapperImpl(source);
//        return Stream.of(src.getPropertyDescriptors())
//                .map(FeatureDescriptor::getName)
//                .filter(pn->src.getPropertyValue(pn) == null)
//                .toArray(String[]::new);
//    }
//
//}
