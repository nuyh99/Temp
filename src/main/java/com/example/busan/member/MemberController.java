package com.example.busan.member;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.member.dto.EmailDuplicateResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.busan.auth.AuthController.AUTHORIZATION;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody final RegisterRequest request) {
        memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@Authorized final Authentication authentication, final HttpSession session) {
        session.removeAttribute(AUTHORIZATION);
        memberService.deleteById(authentication.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmailDuplicateResponse> isDuplicated(@PathVariable("email") final String email) {
        final EmailDuplicateResponse response = memberService.isDuplicated(email);
        return ResponseEntity.ok(response);
    }
}
