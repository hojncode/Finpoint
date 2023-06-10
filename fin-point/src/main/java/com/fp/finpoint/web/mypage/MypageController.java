package com.fp.finpoint.web.mypage;

import com.fp.finpoint.domain.file.service.FileService;
import com.fp.finpoint.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final FileService fileService;

    @GetMapping("/mypage")
    public String mailConfirm(Model model)
    {
        MypageDto mypageDto = new MypageDto();
        mypageDto.setFinpoint(47000L);
        mypageDto.setPieceCnt(27L);
        mypageDto.setPieceKind(5L);
        mypageDto.setPiecePrice(570000L);
        mypageDto.setEmail("test@gmail.com");
        mypageDto.setGoal(50000L);
        mypageDto.setNickname("테스트");
        mypageDto.setSpend(47000L);
        model.addAttribute("mypageDto",mypageDto);
        return "mypage";
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> uploadFile(@RequestParam("profileImage") MultipartFile file) throws IOException {
        fileService.saveFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("message", "프로필 이미지 업데이트 성공");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public String image(){
        return "profile";
    }

}
