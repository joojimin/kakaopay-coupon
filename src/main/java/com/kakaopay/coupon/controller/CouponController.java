package com.kakaopay.coupon.controller;

import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CouponController {

    private final CouponService couponService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coupon/{id}", method = RequestMethod.GET)
    public Coupon getCoupon(@PathVariable Long id) {
        return couponService.get(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public Page<Coupon> getCouponListWithPage(Pageable pageable) {
        log.info("getCouponListWithPage called : " + pageable);
        return couponService.getList(pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/coupon", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
    public Coupon createCoupon(@RequestBody @Valid CouponCreateDTO couponCreateDTO) {
        return couponService.create(couponCreateDTO);
    }
}
