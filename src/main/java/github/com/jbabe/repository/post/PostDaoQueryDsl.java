package github.com.jbabe.repository.post;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    private List<Post> tuplesToPostEntityForManage(QPost qPost, QPostImg qPostImg, QUser qUser, List<Tuple> tuples) {
//        Set<PostAttachedFile> postAttachedFiles = qPost.postAttachedFiles;

        return tuples.stream().map(tuple-> {
                    Post post = tuple.get(qPost);
                    Objects.requireNonNull(post).setPostImgs(Collections.singleton(
                            PostImg.builder()
                                    .imgUrl(tuple.get(qPostImg.imgUrl))
                                    .build()
                    ));
                    return post;
                }).toList();
    }

    public Page<Post> getPostsListFileFetch(Pageable pageable) {
        QPost qPost = QPost.post;
        QPostImg qPostImg = QPostImg.postImg;
        QUser qUser = QUser.user;
        QPostAttachedFile qPostAttachedFile = QPostAttachedFile.postAttachedFile;
        List<Post> tuples =  jpaQueryFactory
                .select(qPost)
                .from(qPost)
                .leftJoin(qPost.postAttachedFiles).fetchJoin()
                .leftJoin(qPost.postImgs).fetchJoin()
                .leftJoin(qPost.user).fetchJoin()
                .orderBy(qPost.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
//        List<Post> posts = tuplesToPostEntityForManage(qPost, qPostImg, qUser, tuples);



        Long total = getPostTotalCount(qPost, null);


        return new PageImpl<>(tuples, pageable, total != null ? total : 0);

    }
}
