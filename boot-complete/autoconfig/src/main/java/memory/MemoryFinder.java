package memory;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryFinder {

    public Memory get() {
        long max = Runtime.getRuntime().maxMemory(); // JVM 이 사용할 수 있는 최대 메모리 (넘어가면 OOM)
        long total = Runtime.getRuntime().totalMemory(); // JVM 이 확보한 전체 메모리 (JVM 은 처음부터 max 까지 확보하지 않고 필요할 때마다 조금씩 확보한다)
        long free = Runtime.getRuntime().freeMemory(); // total 중에 사용하지 않은 메모리
        long used = total - free; // JVM 이 사용중인 메모리
        return new Memory(used, max);
    }

    @PostConstruct
    public void init() {
        log.info("init memoryFinder");
    }
}
