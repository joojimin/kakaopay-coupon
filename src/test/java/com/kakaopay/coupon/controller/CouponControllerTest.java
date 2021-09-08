package com.kakaopay.coupon.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.kakaopay.coupon.AcceptanceTest;
import com.kakaopay.coupon.model.Coupon;
import com.kakaopay.coupon.model.dto.CouponCreateDTO;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class CouponControllerTest extends AcceptanceTest {

    @Test
    public void 쿠폰_발급() {
        // given
        CouponCreateDTO request = new CouponCreateDTO("jimin.joo@naver.com");

        // when
        ExtractableResponse<Response> response = 쿠폰_발급_요청(request);

        // then
        쿠폰_발급_성공(response);
    }

    @Test
    public void 쿠폰_조회() {
        // given
        Coupon expected = 쿠폰_발급_성공(쿠폰_발급_요청(new CouponCreateDTO("jimin.joo@nhnsoft.com")));

        // when
        ExtractableResponse<Response> response = 쿠폰_조회_요청(expected.getId());

        // then
        쿠폰_조회_성공_JsonPath(response, expected);
    }

    public static ExtractableResponse<Response> 쿠폰_발급_요청(final CouponCreateDTO request) {
        return RestAssured
            .given()
                .log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/coupon")
            .then()
                .log().all()
                .extract();
    }

    public static Coupon 쿠폰_발급_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.as(Coupon.class);
    }

    public static ExtractableResponse<Response> 쿠폰_조회_요청(final Long id) {
        // when
        return RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/api/v1/coupon/" + id)
                .then()
                    .log().all()
                    .extract();
    }

    public static void 쿠폰_조회_성공(ExtractableResponse<Response> response, Coupon expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        Coupon actual = response.as(Coupon.class);
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }

    public static void 쿠폰_조회_성공_JsonPath(ExtractableResponse<Response> response, Coupon expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isEqualTo(expected.getId());
        assertThat(response.jsonPath().getString("email")).isEqualTo(expected.getEmail());
    }
}
