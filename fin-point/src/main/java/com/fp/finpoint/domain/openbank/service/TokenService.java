package com.fp.finpoint.domain.openbank.service;

import com.fp.finpoint.domain.member.entity.Member;
import com.fp.finpoint.domain.member.repository.MemberRepository;
import com.fp.finpoint.domain.openbank.BankingFeign;
import com.fp.finpoint.domain.openbank.Entity.Token;
import com.fp.finpoint.domain.openbank.repository.TokenRepository;
import com.fp.finpoint.global.exception.BusinessLogicException;
import com.fp.finpoint.global.exception.ExceptionCode;
import com.fp.finpoint.global.jwt.JwtUtil;
import com.fp.finpoint.web.openbank.dto.AccountResponseDto;
import com.fp.finpoint.web.openbank.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;
    private final BankingFeign bankingFeign;
    private final MemberRepository memberRepository;

    @Value("${bank.client_id}")
    private String clientId;
    @Value("${bank.client_secret}")
    private String clientSecret;
    @Value("${bank.redirect_uri}")
    private String redirectUri;
    @Value("${bank.grant_type}")
    private String grantType;
    @Value("${bank.state}")
    private String state;

    public String getRequireUrl() {
        String requireUrl = "https://testapi.openbanking.or.kr/oauth/2.0/authorize?"
                + "response_type=code&client_id="
                + clientId
                + "&redirect_uri="
                + redirectUri
                + "&scope="
                + "login%20inquiry%20transfer"
                + "&state="
                + state
                + "&auth_type=0";
        log.info("URL = {}" , requireUrl);
        return requireUrl;
    }
    public void saveToken(String requestToken, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto =
                bankingFeign.requestToken(requestToken, clientId, clientSecret, redirectUri, grantType);
        tokenRepository.save(tokenResponseDto.toEntity());
        Cookie cookie = new Cookie("Sequence", tokenResponseDto.getUser_seq_no());
        response.addCookie(cookie);
    }

    @Transactional
    public void registration(Cookie[] cookies) throws UnsupportedEncodingException {
        String accessToken = JwtUtil.getAccessToken(cookies);
        String seqNum = JwtUtil.getSequence(cookies);
        String email = JwtUtil.getEmail(accessToken);
        Member savedMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Token savedToken = tokenRepository.findByUserSeqNo(seqNum)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TOKEN_NOT_FOUND));
        savedMember.setToken(savedToken);
        log.info("token id is = {}", savedToken.getId());
    }

    public void getAccountList(Long memberId) {
        Member savedMember = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Token savedToken = savedMember.getToken();
        String token = createToken(savedToken.getToken_type(), savedToken.getAccess_token());
        AccountResponseDto accountResponseDto =
                bankingFeign.getAccountList(token, savedToken.getUserSeqNo(), "N", "D");
        // 등록된 계좌중 첫번째 계좌의 Fintech_use_num 을 가져와서 저장
        String finUseNum = accountResponseDto.getRes_list().get(0).getFintech_use_num();
        savedMember.setFintech_use_num(finUseNum);
        log.info("fin_use_num={}", savedMember.getFintech_use_num());
    }

    public String createToken(String type, String accessToken) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(accessToken);
        return sb.toString();
    }
}
