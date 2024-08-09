package university.market.verify.email.utils.random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomUtilImpl implements RandomUtil {

    private final SecureRandom secureRandom;
    private final ThreadLocalRandom threadLocalRandom;

    public RandomUtilImpl() throws NoSuchAlgorithmException {
        this.secureRandom = SecureRandom.getInstanceStrong();
        this.threadLocalRandom = ThreadLocalRandom.current();
    }

    public String generateRandomCode(final char leftLimit, final char rightLimit, final int limit) {
        StringBuilder randomString = new StringBuilder(limit);

        for (int i = 0; i < limit; i++) {
            int randomCodePoint = leftLimit + secureRandom.nextInt(rightLimit - leftLimit + 1);
            randomString.append((char) randomCodePoint);
        }

        return randomString.toString();
    }

    public String generateRandomCountCode(final char leftLimit, final char rightLimit, final int leftCountLimit,
                                          final int rightCountLimit) {
        int limit = threadLocalRandom.nextInt(leftCountLimit, rightCountLimit + 1);
        StringBuilder randomString = new StringBuilder(limit);
        for (int i = 0; i < limit; i++) {
            int randomCodePoint = leftLimit + threadLocalRandom.nextInt(rightLimit - leftLimit + 1);
            randomString.append((char) randomCodePoint);
        }

        return randomString.toString();
    }

    @Override
    public int generateRandomIntCode(int leftLimit, int rightLimit) {
        return threadLocalRandom.nextInt(leftLimit, rightLimit + 1);
    }
}
