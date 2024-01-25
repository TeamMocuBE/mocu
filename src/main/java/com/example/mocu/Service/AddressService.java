package com.example.mocu.Service;

import com.example.mocu.Dao.AddressDao;
import com.example.mocu.Dto.address.GetAddressResponse;
import com.example.mocu.Dto.address.PostAddressRequest;
import com.example.mocu.Dto.address.PostAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressDao addressDao;
    public PostAddressResponse registerAddress(Long userId, PostAddressRequest postAddressRequest) {
        log.info("[AddressService.registerAddress]");

        // TODO. 등록 가능한 주소인지 검사

        long addressId = addressDao.createAddress(userId, postAddressRequest);
        return new PostAddressResponse(addressId);
    }

    public List<GetAddressResponse> getAddress(Long userId) {
        log.info("AddressService.getAddress");

        return addressDao.getAddress(userId);
    }
}
