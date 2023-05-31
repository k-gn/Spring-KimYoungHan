package hello.jdbc.connection;

// 상수 클래스는 추상 클래스로 선언
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
