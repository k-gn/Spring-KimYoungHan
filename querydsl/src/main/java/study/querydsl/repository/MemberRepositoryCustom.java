package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;

import java.util.List;

/*
    Querydsl 전용 기능인 회원 search를 작성할 수 없다. -> 사용자 정의 리포지토리 필요

    사용자 정의 리포지토리 사용법
        1. 사용자 정의 인터페이스 작성
        2. 사용자 정의 인터페이스 구현
        3. 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속
 */
public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
