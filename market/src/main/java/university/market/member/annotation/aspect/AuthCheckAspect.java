package university.market.member.annotation.aspect;

import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.MemberVO;
import university.market.member.domain.auth.AuthType;
import university.market.member.exception.MemberException;
import university.market.member.exception.MemberExceptionType;
import university.market.member.utils.http.HttpRequest;

@Aspect
@Component
public class AuthCheckAspect {

    @Autowired
    private HttpRequest httpRequest;

    @Before("@annotation(university.market.member.annotation.AuthCheck)")
    public void checkAuth(JoinPoint joinPoint) {
        MemberVO currentUser;
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof WebSocketSession) {
            WebSocketSession session = (WebSocketSession) joinPoint.getArgs()[0];
            currentUser = httpRequest.getCurrentMember(session);
        } else {
            currentUser = httpRequest.getCurrentMember();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthCheck authCheck = method.getAnnotation(AuthCheck.class);

        AuthType[] requiredAuths = authCheck.value();
        boolean hasRequiredAuth = false;

        for (AuthType requiredAuth : requiredAuths) {
            if (currentUser.getAuth().compareTo(requiredAuth) >= 0) {
                hasRequiredAuth = true;
                break;
            }
        }

        if (!hasRequiredAuth) {
            throw new MemberException(MemberExceptionType.UNAUTHORIZED_PERMISSION);
        }
    }
}

