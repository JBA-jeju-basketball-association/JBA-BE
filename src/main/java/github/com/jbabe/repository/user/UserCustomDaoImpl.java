package github.com.jbabe.repository.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.web.dto.manageUser.UserSearchCriteriaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class UserCustomDaoImpl implements UserCustomDao{
    private final JPAQueryFactory jpaQueryFactory;
    private final QUser qUser = QUser.user;


    @Override
    public Page<User> searchUser(UserSearchCriteriaEnum criteria, String keyword, User.Role permissions, Pageable pageable, LocalDate startDate, LocalDate endDate) {
        BooleanExpression predicate = null;

        if(criteria!=null && keyword!=null){
            predicate =
                    switch (criteria) {
                        case USER_EMAIL -> qUser.email.toLowerCase().startsWith(keyword.toLowerCase());
                        case USER_ID -> qUser.userId.eq(Integer.parseInt(keyword));
                        case USER_NAME -> qUser.name.containsIgnoreCase(keyword);
                        case USER_TEAM -> qUser.team.eq(keyword);
            };
        }
        if(permissions!=null) predicate = predicate!=null? predicate.and(qUser.role.eq(permissions))
                : qUser.role.eq(permissions);

        if(startDate!=null) {
            predicate = predicate != null ? predicate.and(qUser.createAt.between(startDate.atStartOfDay(), endDate.atStartOfDay()))
                    : qUser.createAt.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        }
        List<User> userList = jpaQueryFactory.selectFrom(qUser)
                .where(predicate)
                .orderBy(qUser.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(qUser.userId.count())
                .from(qUser)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(userList, pageable, total!=null? total:0);
    }
}
