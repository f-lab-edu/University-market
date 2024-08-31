package university.market.dibs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import university.market.dibs.domain.DibsVO;
import university.market.dibs.service.DibsService;
import university.market.member.annotation.AuthCheck;
import university.market.member.domain.auth.AuthType;
import university.market.member.utils.http.HttpRequest;

@Tag(name = "Dibs", description = "찜 관련 API")
@RestController
@RequestMapping("/api/dibs")
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;
    private final HttpRequest httpRequest;

    @Operation(summary = "찜 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "클라이언트 에러", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json")),
    })
    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @GetMapping("/{itemId}")
    public ResponseEntity<Void> addDibs(@PathVariable Long itemId) {
        dibsService.addDibs(itemId, httpRequest.getCurrentMember());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "클라이언트 에러", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json")),
    })
    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @DeleteMapping("/{dibsId}")
    public ResponseEntity<Void> removeDibs(@PathVariable Long dibsId) {
        dibsService.removeDibs(dibsId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자신의 찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "클라이언트 에러", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json")),
    })
    @AuthCheck({AuthType.ROLE_VERIFY_USER, AuthType.ROLE_ADMIN})
    @GetMapping("/list")
    public ResponseEntity<List<DibsVO>> getDibsByMemberId() {
        List<DibsVO> dibsList = dibsService.getDibsByMemberId(httpRequest.getCurrentMember());
        return ResponseEntity.ok(dibsList);
    }
}
