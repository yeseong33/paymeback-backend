package com.paymeback.domain.gathering.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.gathering.dto.CreateGatheringRequest;
import com.paymeback.domain.gathering.dto.GatheringResponse;
import com.paymeback.domain.gathering.dto.JoinGatheringRequest;
import com.paymeback.domain.gathering.dto.UpdateGatheringRequest;
import com.paymeback.domain.gathering.service.GatheringService;
import com.paymeback.domain.gathering.service.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Gathering", description = "모임 API")
@RestController
@RequestMapping("/api/v1/gatherings")
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;
    private final QRCodeService qrCodeService;

    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<GatheringResponse>> createGathering(
        @Valid @RequestBody CreateGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.createGathering(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임이 생성되었습니다."));
    }

    @Operation(summary = "모임 참여", description = "QR 코드를 통해 모임에 참여합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임 참여 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 QR 코드"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<GatheringResponse>> joinGathering(
        @Valid @RequestBody JoinGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.joinGathering(request.qrCode(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임에 참여했습니다."));
    }

    @Operation(summary = "결제 요청 생성", description = "모임에 대한 결제 요청을 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 요청 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{gatheringId}/payment-request")
    public ResponseEntity<ApiResponse<GatheringResponse>> createPaymentRequest(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @Parameter(description = "총 결제 금액") @RequestParam BigDecimal totalAmount,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.createPaymentRequest(gatheringId, totalAmount, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "결제 요청이 생성되었습니다."));
    }

    @Operation(summary = "모임 조회", description = "특정 모임의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse<GatheringResponse>> getGathering(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.getGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "모임 수정", description = "모임 정보를 수정합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PatchMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse<GatheringResponse>> updateGathering(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @Valid @RequestBody UpdateGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.updateGathering(gatheringId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임이 수정되었습니다."));
    }

    @Operation(summary = "내가 생성한 모임 목록", description = "내가 생성한 모임 목록을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<GatheringResponse>>> getMyGatherings(
        @PageableDefault(size = 10) Pageable pageable,
        @AuthenticationPrincipal UserDetails userDetails) {

        Page<GatheringResponse> response = gatheringService.getMyGatherings(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "참여한 모임 목록", description = "내가 참여한 모임 목록을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/participated")
    public ResponseEntity<ApiResponse<Page<GatheringResponse>>> getParticipatedGatherings(
        @PageableDefault(size = 10) Pageable pageable,
        @AuthenticationPrincipal UserDetails userDetails) {

        Page<GatheringResponse> response = gatheringService.getParticipatedGatherings(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "모임 종료", description = "모임을 종료합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "종료 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{gatheringId}/close")
    public ResponseEntity<ApiResponse<Void>> closeGathering(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        gatheringService.closeGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("모임이 종료되었습니다."));
    }

    @Operation(summary = "QR 코드 갱신", description = "모임의 QR 코드를 새로 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "갱신 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{gatheringId}/refresh-qr")
    public ResponseEntity<ApiResponse<GatheringResponse>> refreshQRCode(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.refreshQRCode(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "QR 코드가 갱신되었습니다."));
    }

    @Operation(summary = "QR 코드 이미지 조회", description = "모임의 QR 코드 이미지를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping("/{gatheringId}/qr-image")
    public ResponseEntity<byte[]> getQRCodeImage(
        @Parameter(description = "모임 ID") @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse gathering = gatheringService.getGathering(gatheringId, userDetails.getUsername());
        byte[] qrImage = qrCodeService.generateQRCodeImage(gathering.qrCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity.ok()
            .headers(headers)
            .body(qrImage);
    }
}