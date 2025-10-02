package com.socialmedia.petTreff.dto;

import lombok.Data;

// Nur was client schiken soll
@Data
public class ChatSendDTO {


    private Long recipientId;
    private String content;

}
