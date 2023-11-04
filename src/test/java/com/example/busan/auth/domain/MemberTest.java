package com.example.busan.auth.domain;

import com.example.busan.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.example.busan.member.domain.Region.GANGNEUNG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "tests  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("아이디 검증")
    void validateId(final String invalidId) {
        assertThatThrownBy(() -> new Member(invalidId, "name", "password", GANGNEUNG, "01012345678", "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "testsfd  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("비밀번호 검증")
    void validatePassword(final String invalidPassword) {
        assertThatThrownBy(() -> new Member("idididid", "name", invalidPassword, GANGNEUNG, "01012345678", "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지역이 없으면 예외가 발생한다")
    void validateRegion() {
        assertThatThrownBy(() -> new Member("idididid", "name", "passwordpasswo", null, "01012345678", "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회사가 없으면 예외가 발생한다")
    void validateCompany() {
        assertThatThrownBy(() -> new Member("idididid", "name", "passwordpasswo", GANGNEUNG, "01012345678", null, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("휴대폰 번호가 없으면 예외가 발생한다")
    void validatePhone() {
        assertThatThrownBy(() -> new Member("idididid", "name", "passwordpasswo", GANGNEUNG, null, "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이름이 없으면 예외가 발생한다")
    void validateName() {
        assertThatThrownBy(() -> new Member("idididid", null, "passwordpasswo", GANGNEUNG, "01012345678", "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호가 다르면 예외가 발생한다")
    void checkPassword() {
        //given
        final Member member = new Member("ididididid", "name", "password1234", GANGNEUNG, "01012345678", "company", passwordEncoder);

        //when, then
        assertThatThrownBy(() -> member.checkPassword("password12345", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
