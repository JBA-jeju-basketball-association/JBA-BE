package github.com.jbabe.repository.post;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postAttachedFile.QPostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.postImg.QPostImg;
import github.com.jbabe.repository.user.QUser;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    private final QPost qPost = QPost.post;
    private final QPostImg qPostImg = QPostImg.postImg;
    private final QPostAttachedFile qPostAttachedFile = QPostAttachedFile.postAttachedFile;

    @Override
    public Page<Post> searchPostList(String keyword, Post.Category category, Pageable pageable){
//        QPost qPost = QPost.post;
        BooleanExpression predicate = qPost.category.eq(category)
                .and(qPost.isAnnouncement.eq(false))
                .and(qPost.postStatus.eq(Post.PostStatus.NORMAL));
        if(keyword!=null) {
            predicate = predicate.and(qPost.name.containsIgnoreCase(keyword));
//                    .or(qPost.content.containsIgnoreCase(keyword)));
        }
        List<Post> postList = getPostListQuery(qPost, predicate, pageable);




        Long total = getPostTotalCount(qPost, predicate);

        return new PageImpl<>(postList, pageable, total != null ? total : 0);

    }

    private Long getPostTotalCount(QPost qPost, BooleanExpression predicate) {
        return jpaQueryFactory.select(qPost.postId.count())
                .from(qPost)
                .where(predicate)
                .fetchOne();
    }

    private List<Post> getPostListQuery(QPost qPost, BooleanExpression predicate, Pageable pageable) {
        List<Tuple> tuples = jpaQueryFactory
                .select(qPost.postId, qPost.isAnnouncement, qPost.name, qPost.createAt, qPost.user.name, qPost.viewCount, qPost.foreword)
                .from(qPost)
                .where(predicate)
                .orderBy(qPost.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return tuplesToPostEntity(qPost, tuples);
    }


    @Override
    public List<Post> getAnnouncementPosts(Post.Category categoryEnum, Sort sort) {
        List<Tuple> tuples = jpaQueryFactory
                .select(qPost.postId, qPost.isAnnouncement, qPost.name, qPost.createAt, qPost.user.name, qPost.viewCount, qPost.foreword)
                .from(qPost)
                .where(qPost.isAnnouncement.eq(true)
                        .and(qPost.category.eq(categoryEnum))
                        .and(qPost.postStatus.eq(Post.PostStatus.NORMAL)))
                .orderBy(qPost.createAt.desc())
                .fetch();

        return tuplesToPostEntity(qPost, tuples);
    }

    private List<Post> tuplesToPostEntity(QPost qPost, List<Tuple> tuples) {
        return tuples.stream().map(tuple -> Post.builder()
                        .postId(tuple.get(qPost.postId))
                        .isAnnouncement(tuple.get(qPost.isAnnouncement))
                        .name(tuple.get(qPost.name))
                        .createAt(tuple.get(qPost.createAt))
                        .user(User.builder()
                                .name(tuple.get(qPost.user.name))
                                .build())
                        .viewCount(tuple.get(qPost.viewCount))
                        .foreword(tuple.get(qPost.foreword))
                        .build())
                .toList();
    }


    @Override
    public Page<Post> getPostsListFileFetch(Pageable pageable, String keyword, SearchCriteriaEnum searchCriteria, Post.Category categoryEnum, LocalDate startDate, LocalDate endDate) {
        BooleanExpression predicate = null;
        if (categoryEnum != null) predicate = qPost.category.eq(categoryEnum);
        if (keyword != null) {
            predicate = switch (searchCriteria) {
                case TITLE ->
                        predicate != null ? predicate.and(qPost.name.containsIgnoreCase(keyword)) : qPost.name.containsIgnoreCase(keyword);
                case EMAIL ->
                        predicate != null ? predicate.and(qPost.user.email.containsIgnoreCase(keyword)) : qPost.user.email.containsIgnoreCase(keyword);
                case CONTENT ->
                        predicate != null ? predicate.and(qPost.content.containsIgnoreCase(keyword)) : qPost.content.containsIgnoreCase(keyword);
                case ID ->
                        predicate != null ? predicate.and(qPost.postId.eq(Integer.valueOf(keyword))) : qPost.postId.eq(Integer.valueOf(keyword));
            };
        }
        if(startDate != null) {
            predicate = predicate != null ? predicate.and(qPost.createAt.between(startDate.atStartOfDay(), endDate.atStartOfDay()))
                    : qPost.createAt.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        }
        // 서브쿼리방식
        List<Integer> postIds = jpaQueryFactory.select(qPost.postId)
                .from(qPost)
                .where(predicate)
                .orderBy(qPost.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostAttachedFile> postAttachedFiles = jpaQueryFactory
                .select(qPostAttachedFile)
                .from(qPostAttachedFile)
                .where(qPostAttachedFile.post.postId.in(postIds))
                .fetch();

        List<Post> postList =  jpaQueryFactory
                .select(Projections.constructor(Post.class, qPost, qPost.user.email, qPostImg.imgUrl))
                .from(qPost)
                .leftJoin(qPost.postImgs, qPostImg)
                .where(qPost.postId.in(postIds))
                .groupBy(qPost.postId)
                .orderBy(qPost.createAt.desc())
                .fetch();
        postList.forEach(post -> {
            post.setPostAttachedFiles(postAttachedFiles.stream()
                    .filter(postAttachedFile -> post.getPostId().equals(postAttachedFile.getPost().getPostId()))
                    .toList());
        });


      /*  //패치조인방식
        List<Post> postList = jpaQueryFactory
                .selectFrom(qPost)
                .leftJoin(qPost.postAttachedFiles).fetchJoin()
                .orderBy(qPost.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<Integer> postId = postList.stream().map(Post::getPostId).toList();
        List<Tuple> emailAndImgUrl = jpaQueryFactory
                .select(qPost.postId, qPost.user.email, qPostImg.imgUrl)
                .from(qPost)
                .leftJoin(qPost.postImgs, qPostImg)
                .where(qPost.postId.in(postId))
                .groupBy(qPost.postId)
                .fetch();

        Map<Integer, Tuple> emailAndImgUrlMap = emailAndImgUrl.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(qPost.postId), Function.identity()));

        postList.forEach(post -> {
            Tuple tuple = emailAndImgUrlMap.get(post.getPostId());
            if(tuple.get(qPost.user.email) != null){
                post.setUser(User.builder().email(tuple.get(qPost.user.email)).build());
            }
            if(tuple.get(qPostImg.imgUrl) != null){
                post.setPostImgs(Collections.singleton(
                        PostImg.builder().imgUrl(tuple.get(qPostImg.imgUrl)).build()
                ));
            }else post.setPostImgs(null);
        });
*/


        Long total = getPostTotalCount(qPost, predicate);


        return new PageImpl<>(postList, pageable, total != null ? total : 0);

    }

    @Override
    public Optional<Post> getPostJoinFiles(Integer postId) {
        Tuple postTuple = jpaQueryFactory
                .select(qPost, qPost.user.name)
                .from(qPost)
                .leftJoin(qPost.user, QUser.user)
                .where(qPost.postId.eq(postId))
                .fetchOne();
        List<PostAttachedFile> postAttachedFiles = jpaQueryFactory
                .selectFrom(qPostAttachedFile)
                .where(qPostAttachedFile.post.postId.eq(postId))
                .fetch();
        List<PostImg> postImgs = jpaQueryFactory
                .selectFrom(qPostImg)
                .where(qPostImg.post.postId.eq(postId))
                .fetch();
        Optional<Post> post = Optional.ofNullable(postTuple).map(tuple -> tuple.get(qPost));
        post.ifPresent(p -> {
            p.setTempWriterName(postTuple.get(qPost.user.name));
            p.setPostAttachedFiles(postAttachedFiles);
            p.setPostImgs(postImgs);
        });
        return post;
    }
}
