package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.address.*;
import com.example.mocu.Service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AddressController {
    private final AddressService addressService;

    /**
     * 주소 등록
     */
    @PostMapping("/{userId}/address/register")
    public BaseResponse<PostAddressResponse> registerAddress(@PathVariable Long userId, @RequestBody PostAddressRequest postAddressRequest) {
        log.info("[AddressController.registerAddress]");

        return new BaseResponse<>(addressService.registerAddress(userId, postAddressRequest));
    }

    /**
     * 주소 조회
     */
    @GetMapping("/{userId}/address")
    public BaseResponse<List<GetAddressResponse>> getAddresses(@PathVariable Long userId) {
        log.info("[AddressControlelr.getAddress");

        return new BaseResponse<>(addressService.getAddress(userId));
    }

    /**
     * 주소 수정
     */
    @PatchMapping("/{userId}/modify-address/{addressId}")
    public BaseResponse<PatchUserAddressResponse> modifyAddress(@PathVariable Long userId, @RequestBody PatchUserAddressRequest patchUserAddressRequest, @PathVariable Long addressId) {
        log.info("[AddressController.modifyAddress]");

        return new BaseResponse<>(addressService.modifyAddress(userId, patchUserAddressRequest, addressId));
    }

    /**
     * 주소 선택 (현 위치 변경)
     */
    @PatchMapping("/{userId}/set-address")
    public BaseResponse<SelectUserAddressResponse> selectAddress(@PathVariable Long userId, @RequestBody PatchUserSetAddress patchUserSetAddress) {
        log.info("[AddressController.selectAddress]");

        return new BaseResponse<>(addressService.selectAddress(userId, patchUserSetAddress));
    }
}
