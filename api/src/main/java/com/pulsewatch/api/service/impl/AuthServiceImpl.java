package com.pulsewatch.api.service.impl;

import com.pulsewatch.api.domain.JoinVO;
import com.pulsewatch.api.dto.JoinDTO;
import com.pulsewatch.api.service.AuthService;
import com.pulsewatch.common.error.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        if(phoneNumberCheck(joinRequest.getPhoneNumber())) throw new BusinessException("유효하지 않은 전화번호 입니다","member");

        //생년월일 검증
        if(birthdayCheck(joinRequest.getBirthDay())) throw new BusinessException("유효하지 않은 생년월일 입니다","member");

        //내부 객체로 형변환
        JoinVO joinVO = JoinDTO.joinRequest.convertJoinVO(joinRequest);

        // 개인정보 암호화
        JoinVO encodeMember = encodeToPersonalInformation(joinVO);

        //이름 전화번호로 회원가입 여부 조회
        Integer joinCheck = authMapper.getJoinCheck(joinVO);

        if(joinCheck == null || joinCheck > 0) throw new BusinessException("이미 가입된 회원입니다.","member");
        //DB 저장
        Integer memberJoin = authMapper.setInsertMemberJoin(joinVO);
        if(memberJoin == null || memberJoin == 0) throw new BusinessException("회원정보 저장에 실패했습니다.","member");


        return null;
    }

    //아이디 중복체크0
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

            e.printStackTrace();
            return false;
        }
    }

    //개인정보 암호화
    public JoinVO encodeToPersonalInformation(JoinVO joinVO) {
        JoinVO result = JoinVO.builder()
                .name(joinVO.getName())
                .password(passwordEncoder.encode(joinVO.getPassword()))
                .phoneNumber(passwordEncoder.encode(joinVO.getPhoneNumber()))
                .birthDay(passwordEncoder.encode(joinVO.getBirthDay()))
                .build();
        return result;
    }
}
