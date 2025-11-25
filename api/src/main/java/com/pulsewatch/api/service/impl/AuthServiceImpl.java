package com.pulsewatch.api.service.impl;

import com.pulsewatch.api.domain.JoinVO;
import com.pulsewatch.api.dto.JoinDTO;
import com.pulsewatch.api.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public JoinDTO.joinResponse setMemberJoin(JoinDTO.joinRequest joinRequest) {
        //전화번호 검증
        if(phoneNumberCheck(joinRequest.getPhoneNumber()));

        //생년월일 검증
        if(birthdayCheck(joinRequest.getBirthday())) ;

        //내부 객체로 형변환
        JoinVO joinVO = JoinDTO.joinRequest.convertJoinVO(joinRequest);

        // 개인정보 암호화
        JoinVO encodeMember = encodeToPersonalInformation(joinVO);

        //DB 저장

        return null;
    }

    @Override
    public Boolean getIdCheck(String id) {

        Integer result = authMapper.getIdCheck(id);
        return result == null || result <= 0;
    }


    // 전화번호 검증
    public Boolean phoneNumberCheck(String phoneNumber) {
        if(phoneNumber == null) return false;
        // 숫자만 남기기
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        // 휴대폰 번호는 10~11자리
        if (digits.length() < 10 || digits.length() > 11) return false;

        // 국내 휴대폰 패턴
        return digits.matches("^01[016789][0-9]{7,8}$");
    }

    // 생년월일 검증
    public Boolean birthdayCheck(String birthday) {
        if(birthday == null) return false;

        // 숫자만 남기기 (1999-12-31 -> 19991231)
        String digits = birthday.replaceAll("[^0-9]", "");

        if (digits.length() != 8) return false;

        try {
            java.time.LocalDate birth = java.time.LocalDate.parse(
                    digits,
                    java.time.format.DateTimeFormatter.BASIC_ISO_DATE
            );

            java.time.LocalDate now = java.time.LocalDate.now();

            int year = birth.getYear();
            if (year < 1900 || year > now.getYear()) return false;
            if (birth.isAfter(now)) return false;

            // 나이 상한(예: 120세) 정도만 가볍게 제한
            if (java.time.Period.between(birth, now).getYears() > 120) return false;

            return true;
        } catch (Exception e) {
            log.warn("[JOIN] invalid birthday: {}", birthday);
            return false;
        }
    }

    //개인정보 암호화
    public JoinVO encodeToPersonalInformation(JoinVO joinVO) {
        JoinVO result = JoinVO.builder()
                .name(joinVO.getName())
                .password(passwordEncoder.encode(joinVO.getPassword()))
                .phoneNumber(passwordEncoder.encode(joinVO.getPhoneNumber()))
                .birthday(passwordEncoder.encode(joinVO.getBirthday()))
                .build();
        return result;
    }
}
