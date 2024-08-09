package university.market.member.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;

@Slf4j
@Component
public class JwtTokenProviderImpl implements JwtTokenProvider{
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expire-length}")
    private int expireTimeMilliSecond;
    private final String MEMBER_ID = "memberId";

    @Override
    public String generateToken(final long memberId) {
        return generateToken(memberId, () -> new Date(new Date().getTime() + expireTimeMilliSecond));
    }

    @Override
    public String generateToken(final long memberId, final ExpireDateSupplier expireDateSupplier) {
        return Jwts.builder()
                .claim(MEMBER_ID, memberId)
                .expiration(expireDateSupplier.expireDate())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public void validateToken(final String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        }  catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new MemberException(MemberExceptionType.INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new MemberException(MemberExceptionType.EXPIRED_ACCESS_TOKEN);
        }
    }

    @Override
    public long extractMemberId(final String token) {
        try {
            return Long.parseLong(
                    Jwts.parser()
                    .setSigningKey(secretKey).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(MEMBER_ID)
                    .toString()
            );

        } catch (RuntimeException e) {
            throw new MemberException(MemberExceptionType.INVALID_ACCESS_TOKEN);
        }
    }
}
