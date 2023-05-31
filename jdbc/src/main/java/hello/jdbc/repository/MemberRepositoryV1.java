package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource 사용, JdbcUtils 사용
 *
 * # 커넥션 풀 : 미리 커넥션을 생성해두고 사용하는 방법
 * 실무에서 커넥션 풀은 기본으로 사용한다.
 * 커넥션을 계속 새로 만드는 과정은 복잡하고 시간도 많이 소모되는 일이다. (응답 속도에 영향을 준다.)
 * 심지어 데이터베이스 마다 커넥션 생성 시간이 다르다.
 * 따라서 커넥션 풀은 이런 문제를 해결해줄 수 있다.
 *
 * 커넥션 풀은 서버당 최대 커넥션 수를 제한할 수 있다. (무한정 연결 생성 방지)
 * 오픈 소스를 사용하는 것이 좋다.
 * 커넥션 풀 숫자는 서비스 특징, 서버 스펙, DB 스펙에 따라 다르기 떄문에 성능 테스트를 통해 정한다. (보통은 10개를 쓴다.)
 */
@Slf4j
public class MemberRepositoryV1 {

    // DataSource 인터페이스를 통해 커넥션 획득 방법을 추상화 (추상화 된 인터페이스에만 의존하기 때문에 로직 변경이 없음)
    // 이렇게 추상화 하지 않으면 커넥션 획득 방법에 따른 로직 수정이 필요해진다.
    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }


    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }


    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con); // 이 때 커넥션 풀에 해당 커넥션 반환
    }


    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }


}
