package com.red.domovie.domain.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
	
    // 메시지 타입 : 입장, 채팅, 나감
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    
    private MessageType type; // 메시지 타입
    private long roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
}