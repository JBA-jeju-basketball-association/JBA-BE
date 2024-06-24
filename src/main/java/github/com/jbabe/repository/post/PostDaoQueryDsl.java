package github.com.jbabe.repository.post;


import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.postAttachedFile.PostAttachedFile;
import github.com.jbabe.repository.postAttachedFile.QPostAttachedFile;
import github.com.jbabe.repository.postImg.PostImg;
import github.com.jbabe.repository.postImg.QPostImg;
import github.com.jbabe.repository.user.QUser;
import github.com.jbabe.repository.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostDaoQueryDsl {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Post> searchPostList(String keyword, Post.Category category, Pageable pageable){
        QPost qPost = QPost.post;
        BooleanExpression predicate = qPost.category.eq(category)
                .and(qPost.name.containsIgnoreCase(keyword))
                .and(qPost.isAnnouncement.eq(false))
                .and(qPost.postStatus.eq(Post.PostStatus.NORMAL));

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


    public List<Post> getAnnouncementPosts(Post.Category categoryEnum, Sort sort) {
        QPost qPost = QPost.post;
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


    public Page<Post> getPostsListFileFetch(Pageable pageable) {
        QPost qPost = QPost.post;
        QPostImg qPostImg = QPostImg.postImg;
        QPostAttachedFile qPostAttachedFile = QPostAttachedFile.postAttachedFile;

        // 서브쿼리방식
        List<Integer> postIds = jpaQueryFactory.select(qPost.postId)
                .from(qPost)
                .orderBy(qPost.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostAttachedFile> postAttachedFiles = jpaQueryFactory
                .select(Projections.constructor(PostAttachedFile.class, qPostAttachedFile.post.postId, qPostAttachedFile.fileName, qPostAttachedFile.filePath))
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
                    .collect(Collectors.toSet()));
        });


      /*  //패치조인 방식
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


        Long total = getPostTotalCount(qPost, null);


        return new PageImpl<>(postList, pageable, total != null ? total : 0);

    }
}
