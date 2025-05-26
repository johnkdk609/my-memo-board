package com.mymemo.backend.init;

import com.github.javafaker.Faker;
import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import com.mymemo.backend.repository.MemoRepository;
import com.mymemo.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;

@Slf4j
@Component
@Profile("dev")     // dev 환경에서만 실행됨
@RequiredArgsConstructor
public class FakeMemoDataLoader implements CommandLineRunner {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final Faker faker = new Faker(new Locale("ko"));
    private final Random random = new Random();
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void run(String... args) {
        String email = "faker@example.com";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다: " + email));

        long count = memoRepository.countByUser(user);
        if (count > 0) {
            log.info("이미 더미 메모가 존재합니다. ({}개). 생성 생략.", count);
            return;
        }

        log.info("Faker 더미 메모 데이터를 생성합니다...");

        for (int i = 0; i < 10000; i++) {
            String title = faker.book().title();
            String content = faker.lorem().paragraph(3);
            MemoCategory category = MemoCategory.values()[random.nextInt(MemoCategory.values().length)];
            boolean isPinned = random.nextBoolean();
            Visibility visibility = random.nextBoolean() ? Visibility.PUBLIC : Visibility.PRIVATE;

            Memo memo = new Memo(user, title, content, category, visibility, isPinned, false, 0);
            memoRepository.save(memo);

            if (i % 1000 == 0) {
                memoRepository.flush();
                entityManager.clear();
            }
        }

        log.info("더미 메모 생성 완료!");
    }
}
