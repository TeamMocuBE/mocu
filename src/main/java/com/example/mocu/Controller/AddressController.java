package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.address.GetAddressResponse;
import com.example.mocu.Dto.address.PostAddressRequest;
import com.example.mocu.Dto.address.PostAddressResponse;
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
}
