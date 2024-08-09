package university.market.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.auth.AuthType;
import university.market.member.service.MemberService;
import university.market.member.service.dto.request.JoinRequest;
import university.market.member.service.dto.request.LoginRequest;
import university.market.member.service.dto.response.LoginResponse;
import university.market.member.utils.http.HttpRequest;
import university.market.verify.email.service.dto.CheckVerificationCodeRequest;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final HttpRequest httpRequest;

    @PostMapping("/join")
    public ResponseEntity<Void> joinMember(@RequestBody JoinRequest joinRequest) {
        memberService.joinMember(joinRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginMember(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = memberService.loginMember(loginRequest);
        return ResponseEntity.ok(response);
    }

    @AuthCheck(AuthType.ROLE_ADMIN)
    @PostMapping("/join/admin")
    public ResponseEntity<Void> joinAdminMember(@RequestBody JoinRequest joinRequest) {
        memberService.joinAdminUser(joinRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @AuthCheck(AuthType.ROLE_USER)
    @PostMapping("/verify")
    public ResponseEntity<Void> memberEmailVerify(
            @RequestBody CheckVerificationCodeRequest checkVerificationCodeRequest) {
        memberService.verifyEmailUser(checkVerificationCodeRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @AuthCheck(AuthType.ROLE_ADMIN)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @AuthCheck({AuthType.ROLE_ADMIN, AuthType.ROLE_VERIFY_USER, AuthType.ROLE_USER})
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteMyself() {
        memberService.deleteMyself(httpRequest.getCurrentMember());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
