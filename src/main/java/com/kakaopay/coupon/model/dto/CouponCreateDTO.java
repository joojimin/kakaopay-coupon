package com.kakaopay.coupon.model.dto;

import com.kakaopay.coupon.error.exception.InvalidEmailException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class CouponCreateDTO {

    @NotBlank
    @Email(message = InvalidEmailException.errorCode)
    String email;

    public CouponCreateDTO(String email) {
        this.email = email;
    }
}
