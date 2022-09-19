package drrename;

import com.github.ktools1000.AnotherThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@ComponentScan(basePackages = {"drrename", "com"})
@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        DrRenameApplication3.main(args);
    }

    @Bean("low-priority-executor")
    public ExecutorService getLowPriorityExecutor() {
        return Executors.newFixedThreadPool(1,
                new AnotherThreadFactory(Thread.MIN_PRIORITY));
    }

}
