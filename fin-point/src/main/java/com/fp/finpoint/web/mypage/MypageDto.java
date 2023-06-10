package com.fp.finpoint.web.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageDto {

    public String nickname;

    public String email;

    public Long goal;

    public Long finpoint;

    public Long spend;

    public Long pieceKind;

    public Long pieceCnt;

    public Long piecePrice;

}
