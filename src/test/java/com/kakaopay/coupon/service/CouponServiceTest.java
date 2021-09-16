package com.kakaopay.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.kakaopay.coupon.core.CodeGenerator;
import com.kakaopay.coupon.error.exception.CodeCollisionException;
import com.kakaopay.coupon.error.exception.DuplicateEmailException;
import com.kakaopay.coupon.error.exception.EmptyEmailException;
import com.kakaopay.coupon.error.exception.NotExistCouponException;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import com.kakaopay.coupon.repository.CouponRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepo;

    @Mock
    private CodeGenerator codeGenerator;

    @InjectMocks
    private CouponService couponService;


    @Test
    public void couponGetTest() {
        // given
        final String expectedEmail = "jimin.joo@nhnsoft.com";
        Coupon expected = new Coupon(expectedEmail, "");
        BDDMockito.given(couponRepo.findOne(1L)).willReturn(expected);

        // when
        Coupon coupon = couponService.get(1l);

        // then
        assertThat(coupon).isNotNull();
        assertThat(coupon.getEmail()).isEqualTo(expectedEmail);

        BDDMockito.then(couponRepo).should().findOne(1L);
    }

    @Test
    public void couponGetTestNotFound() {
        // when
        assertThatThrownBy(() -> couponService.get(1l))
            .isInstanceOf(NotExistCouponException.class)
            .hasMessageStartingWith("Not exist coupon with id");
    }

    @Test
    public void createTest() {
        // given
        final String email = "jimin.joo@nhnsoft.com";
        final String code = "abc";
        BDDMockito.given(couponRepo.existsByEmail(email)).willReturn(false);
        BDDMockito.given(codeGenerator.generateCode()).willReturn(code);
        BDDMockito.given(couponRepo.save(any(Coupon.class))).willReturn(new Coupon(email, code));

        // when
        Coupon actual = couponService.create(new CouponCreateDTO(email));

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(email);

        BDDMockito.then(couponRepo).should().existsByEmail(email);
        BDDMockito.then(codeGenerator).should().generateCode();
        BDDMockito.then(couponRepo).should().save(any(Coupon.class));
    }

    @Test
    public void createTestEmailIsEmpty() {
        // when
        assertThatThrownBy(() -> couponService.create(new CouponCreateDTO("")))
            .isInstanceOf(EmptyEmailException.class)
            .hasMessage("Fail to create Coupon. Email is null or empty.");
    }

    @Test
    public void createTestDuplicateEmail() {
        // given
        final String email = "jimin.joo@nhnsoft.com";
        final String code = "abc";
        BDDMockito.given(couponRepo.existsByEmail(email)).willReturn(true);
        BDDMockito.given(codeGenerator.generateCode()).willReturn(code);

        // when
        assertThatThrownBy(() -> couponService.create(new CouponCreateDTO(email)))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("Fail to create Coupon. Already coupon issued for this mail.");

        // then
        BDDMockito.then(couponRepo).should().existsByEmail(email);
        BDDMockito.then(codeGenerator).should().generateCode();
    }

    @Test
    public void createTestDuplicateCode() {
        // given
        final String email = "jimin.joo@nhnsoft.com";
        final String code = "abc";
        BDDMockito.given(codeGenerator.generateCode()).willReturn(code);
        BDDMockito.given(couponRepo.existsByCode(code)).willReturn(true);

        // when
        assertThatThrownBy(() -> couponService.create(new CouponCreateDTO(email)))
            .isInstanceOf(CodeCollisionException.class)
            .hasMessage("Fail to create Coupon. Collision occur more than 5 in code generator.");

        // then
        BDDMockito.then(codeGenerator).should(times(5)).generateCode();
        BDDMockito.then(couponRepo).should(times(5)).existsByCode(code);
    }
}
