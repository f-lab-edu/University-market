package university.market.member.utils.jwt;
public interface JwtTokenProvider {

    String generateToken(long memberId);

    String generateToken(long email, ExpireDateSupplier expireDateSupplier);

    void validateToken(String token);

    long extractMemberId(String token);

}
