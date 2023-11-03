package com.example.busan.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.springframework.util.Assert;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.containsWhitespace;

@Entity
public class Member {

    public static final int ID_MINIMUM_LENGTH = 6;
    public static final int ID_MAXIMUM_LENGTH = 20;
    public static final int PASSWORD_MINIMUM_LENGTH = 8;
    public static final int PASSWORD_MAXIMUM_LENGTH = 30;

    @Id
    private String id;
    private String password;
    @Enumerated(EnumType.STRING)
    private Region region;
    private String company;

    protected Member() {
    }

    public Member(final String id, final String password, final Region region, final String company) {
        validate(id, password, region, company);
        this.id = id;
        this.password = password;
        this.region = region;
        this.company = company;
    }

    private void validate(final String id, final String password, final Region region, final String company) {
        if (isBlank(id) || containsWhitespace(id) ||
                id.length() < ID_MINIMUM_LENGTH ||
                id.length() > ID_MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(format("아이디는 공백을 제외하고 %d ~ %d 글자입니다.", ID_MINIMUM_LENGTH, ID_MAXIMUM_LENGTH));
        }
        if (isBlank(password) || containsWhitespace(password) ||
                password.length() < PASSWORD_MINIMUM_LENGTH ||
                password.length() > PASSWORD_MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(format("비밀번호는 %d ~ %d 글자입니다.", PASSWORD_MINIMUM_LENGTH, PASSWORD_MAXIMUM_LENGTH));
        }
        Assert.notNull(region, "지역이 필요합니다.");
        Assert.notNull(company, "회사가 필요합니다.");
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Region getRegion() {
        return region;
    }

    public String getCompany() {
        return company;
    }
}
