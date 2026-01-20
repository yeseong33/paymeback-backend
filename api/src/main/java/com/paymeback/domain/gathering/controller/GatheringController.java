package com.paymeback.domain.gathering.controller;

import com.paymeback.common.response.ApiResponse;
import com.paymeback.domain.gathering.dto.CreateGatheringRequest;
import com.paymeback.domain.gathering.dto.GatheringResponse;
import com.paymeback.domain.gathering.dto.JoinGatheringRequest;
import com.paymeback.domain.gathering.dto.UpdateGatheringRequest;
import com.paymeback.domain.gathering.service.GatheringService;
import com.paymeback.domain.gathering.service.QRCodeService;
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

@RestController
@RequestMapping("/api/v1/gatherings")
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;
    private final QRCodeService qrCodeService;

    @PostMapping
    public ResponseEntity<ApiResponse<GatheringResponse>> createGathering(
        @Valid @RequestBody CreateGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.createGathering(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임이 생성되었습니다."));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<GatheringResponse>> joinGathering(
        @Valid @RequestBody JoinGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.joinGathering(request.qrCode(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임에 참여했습니다."));
    }

    @PostMapping("/{gatheringId}/payment-request")
    public ResponseEntity<ApiResponse<GatheringResponse>> createPaymentRequest(
        @PathVariable Long gatheringId,
        @RequestParam BigDecimal totalAmount,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.createPaymentRequest(gatheringId, totalAmount, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "결제 요청이 생성되었습니다."));
    }

    @GetMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse<GatheringResponse>> getGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.getGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse<GatheringResponse>> updateGathering(
        @PathVariable Long gatheringId,
        @Valid @RequestBody UpdateGatheringRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.updateGathering(gatheringId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "모임이 수정되었습니다."));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<GatheringResponse>>> getMyGatherings(
        @PageableDefault(size = 10) Pageable pageable,
        @AuthenticationPrincipal UserDetails userDetails) {

        Page<GatheringResponse> response = gatheringService.getMyGatherings(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/participated")
    public ResponseEntity<ApiResponse<Page<GatheringResponse>>> getParticipatedGatherings(
        @PageableDefault(size = 10) Pageable pageable,
        @AuthenticationPrincipal UserDetails userDetails) {

        Page<GatheringResponse> response = gatheringService.getParticipatedGatherings(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{gatheringId}/close")
    public ResponseEntity<ApiResponse<Void>> closeGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        gatheringService.closeGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("모임이 종료되었습니다."));
    }

    @PostMapping("/{gatheringId}/refresh-qr")
    public ResponseEntity<ApiResponse<GatheringResponse>> refreshQRCode(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails) {

        GatheringResponse response = gatheringService.refreshQRCode(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "QR 코드가 갱신되었습니다."));
    }

    @GetMapping("/{gatheringId}/qr-image")
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable Long gatheringId,
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