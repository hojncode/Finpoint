package com.fp.finpoint.web.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@NoArgsConstructor
public class MypageDto {

    public String nickname;

    public String email;

    public Integer goal;

    public Long finpoint;

    public Long spend;

    public int pieceKind;

    public Long pieceCnt;

    public Long piecePrice;

    public Resource profileImg;
}
