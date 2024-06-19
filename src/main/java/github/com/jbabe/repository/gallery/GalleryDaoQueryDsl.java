package github.com.jbabe.repository.gallery;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.galleryImg.QGalleryImg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class GalleryDaoQueryDsl {
    private final JPAQueryFactory jpaQueryFactory;

    private Page<Gallery> checkElementCountAndReturnPage(QGallery qGallery, BooleanExpression predicate,  List<Gallery> galleryList, Pageable pageable) {
        Long total = jpaQueryFactory.select(qGallery.galleryId.count())
                .from(qGallery)
                .where(predicate)
                .fetchOne();
        return new PageImpl<>(galleryList, pageable, total != null ? total : 0);

    }

    public Page<Gallery> getGalleryList(Pageable pageable, boolean official) {
        QGallery qGallery = QGallery.gallery;

        BooleanExpression predicate = qGallery.isOfficial.eq(official)
                .and(qGallery.galleryStatus.eq(Gallery.GalleryStatus.NORMAL));

            List<Gallery> galleryList = getGalleryListWithThumbnailQuery(qGallery, predicate, pageable);

            return checkElementCountAndReturnPage(qGallery, predicate, galleryList, pageable);
        }

    public Page<Gallery> searchGalleryList(boolean official, String keyword, Pageable pageable){
        QGallery qGallery = QGallery.gallery;

        BooleanExpression predicate = qGallery.isOfficial.eq(official)
                .and(qGallery.name.containsIgnoreCase(keyword))
                .and(qGallery.galleryStatus.eq(Gallery.GalleryStatus.NORMAL));

        List<Gallery> galleryList = getGalleryListWithThumbnailQuery(qGallery, predicate, pageable);

        // 갤러리 이미지를 가져오는 방법 1 n+1 문제로 탈락
//        galleryList.forEach(gallery -> {
//            GalleryImg galleryImg = jpaQueryFactory.selectFrom(qGalleryImg)
//                    .where(qGalleryImg.gallery.eq(gallery))
//                    .fetchFirst();
//            gallery.setGalleryImgs(List.of(galleryImg));
//        });

        return checkElementCountAndReturnPage(qGallery, predicate, galleryList, pageable);
    }

    private List<Gallery> getGalleryListWithThumbnailQuery(QGallery qGallery, BooleanExpression predicate, Pageable pageable) {
        QGalleryImg qGalleryImg = QGalleryImg.galleryImg;
//        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable.getSort(), qGallery);



        List<Tuple> results = jpaQueryFactory.select(qGallery, qGalleryImg.fileName, qGalleryImg.fileUrl)
                .from(qGallery)
                .leftJoin(qGallery.galleryImgs, qGalleryImg)
                .where(predicate)
                .orderBy(qGallery.createAt.desc())
                .groupBy(qGallery.galleryId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return results.stream().map(tuple -> {
            Gallery gallery = tuple.get(qGallery);
            GalleryImg galleryImg = tuple.get(qGalleryImg.fileUrl) != null?
                    GalleryImg.builder()
                            .fileUrl(tuple.get(qGalleryImg.fileUrl))
                            .fileName(tuple.get(qGalleryImg.fileName))
                            .build()
                    : null;
            if (galleryImg != null) {
                Objects.requireNonNull(gallery)
                        .setGalleryImgs(Collections.singletonList(galleryImg));
            }else{
                Objects.requireNonNull(gallery)
                        .setGalleryImgs(Collections.emptyList());
            }
            return gallery;
        }).toList();
    }

//    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort, QGallery qGallery) {
//        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//        for (Sort.Order order : sort) {
//            PathBuilder<Object> pathBuilder = new PathBuilder<>(Object.class, qGallery.getMetadata());
//            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC,
//                    pathBuilder.get(order.getProperty()));
//            orderSpecifiers.add(orderSpecifier);
//        }
//        return orderSpecifiers;
//    }
}
