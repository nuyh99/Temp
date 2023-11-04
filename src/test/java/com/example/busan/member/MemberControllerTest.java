package com.example.busan.member;

import com.example.busan.ApiTest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.Region;
import com.example.busan.member.domain.Role;
import com.example.busan.member.dto.EmailDuplicateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class MemberControllerTest extends ApiTest {

    private final MockHttpSession httpSession = new MockHttpSession();
    @MockBean
    private MemberService memberService;

    @BeforeEach
    void clearSession() {
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("회원 가입 하기")
    void register() throws Exception {
        //given
        final String request = objectMapper.writeValueAsString(
                new RegisterRequest("email@naver.com", "password", "name", Region.GANGNEUNG, "company"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/members")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회원 가입 하기",
                        requestFields(
                                fieldWithPath("email").description("이메일 형식"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("password").description(String.format("특수문자 1개 이상, 영문과 숫자 조합 %d ~ %d", Member.PASSWORD_MINIMUM_LENGTH, Member.PASSWORD_MAXIMUM_LENGTH)),
                                fieldWithPath("region").description("지역"),
                                fieldWithPath("company").description("회사"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원 탈퇴 하기")
    void withdraw() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("email@naver.com", Role.USER));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/members").session(httpSession))
                .andDo(print())
                .andDo(document("회원 탈퇴 하기"))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AUTHORIZATION)).isNull();
        });
    }

    @Test
    @DisplayName("이메일 중복 확인하기")
    void isDuplicatedEmail() throws Exception {
        //given
        given(memberService.isDuplicated("test@gmail.com"))
                .willReturn(new EmailDuplicateResponse(false));
        
        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/members/{email}", "test@gmail.com"))
                .andDo(print())
                .andDo(document("이메일 중복 확인하기",
                        pathParameters(parameterWithName("email").description("확인할 이메일")),
                        responseFields(fieldWithPath("isDuplicated").description("중복 여부"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
