package com.example.busan.auth;

import com.example.busan.ApiTest;
import com.example.busan.auth.domain.Auth;
import com.example.busan.auth.domain.Role;
import com.example.busan.auth.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class AuthControllerTest extends ApiTest {

    @MockBean
    private AuthService authService;
    private MockHttpSession httpSession;

    @BeforeEach
    void clearSession() {
        httpSession = new MockHttpSession();
    }

    @Test
    @DisplayName("로그인하기")
    void login() throws Exception {
        //given
        final Auth auth = new Auth("id", Role.USER);
        given(authService.login(any()))
                .willReturn(auth);
        final String request = objectMapper.writeValueAsString(new LoginRequest("id", "password"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/auth/login")
                                .session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("로그인하기",
                        requestFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("password").description("비밀번호"))))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AuthController.AUTHORIZATION)).isEqualTo(auth);
        });
    }

    @Test
    @DisplayName("로그아웃 하기")
    void logout() throws Exception {
        final Auth auth = new Auth("id", Role.USER);
        httpSession.setAttribute(AuthController.AUTHORIZATION, auth);

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/auth/logout").session(httpSession))
                .andDo(print())
                .andDo(document("로그아웃하기"))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AuthController.AUTHORIZATION)).isNull();
        });
    }
}
