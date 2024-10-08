package com.red.domovie.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.red.domovie.domain.dto.ResponseDTO;
import com.red.domovie.domain.dto.bot.FAQDTO;
import com.red.domovie.domain.dto.bot.QuestionDTO;
import com.red.domovie.domain.dto.chat.AnswerDTO;
import com.red.domovie.service.BotService;
import com.red.domovie.service.CategoryService;
import com.red.domovie.service.ChatService;
import com.red.domovie.service.OpenaiService;

import lombok.RequiredArgsConstructor;

@Controller //@messageMapping의 기본 주소는 "/message"
@RequiredArgsConstructor
public class BotController {
	
	
	@Value("${spring.rabbitmq.template.exchange}")
	private String exchange;
	@Value("${spring.rabbitmq.template.routing-key}")
	private String routingKey;
	
	private final RabbitTemplate rabbitTemplate;
	private final BotService botService;
	private final CategoryService categoryService;
	private final OpenaiService openaiService;
	private final ChatService chatService;
	
	//내부적으로 STOMP의 프로토콜을 사용하여 메세지를 전송
	//@SendTo 어노테이션을 처리하는 구현 객체
	private final SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/question")
	public void question(QuestionDTO dto) throws JsonProcessingException {
		
		System.out.println(">>>client의 question :"+dto);
		String key = dto.getKey();
		String responseMessage = botService.questionProcess(dto);
		
		ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setKey(key);
        responseDTO.setMessage(responseMessage);
		
		//rabbitTemplate.convertAndSend(exchange, routingKey, responseDTO);
        messagingTemplate.convertAndSend("/topic/bot/"+key, responseDTO.getMessage());
		
	}
	
	
	//자주 묻는 질문
	@GetMapping("/api/categories")
	@ResponseBody
    public List<FAQDTO> getAllCategories() {
		System.out.println(categoryService.getAllFaqCategories());
        return categoryService.getAllFaqCategories();
    }
	
	
	//영화추천
	@MessageMapping("/openai")
	public void openai(QuestionDTO dto) {
		
		System.out.println(">>>영화추천 :"+dto);
		String key = dto.getKey();
		String responseMessage = openaiService.aiAnswerProcess(dto);
		
		messagingTemplate.convertAndSend("/topic/bot/"+key, responseMessage);
		
	}
	
	
	//상담사와 채팅
	@MessageMapping("/chat/query")
	public void query(QuestionDTO dto) {
		
		System.out.println(">>>채팅 문의 :"+dto);
		if(chatService.findByRoomId(dto.getKey()).isEmpty()) {
			chatService.saveRoomProcess(dto);
		}
		
		chatService.saveChatQuestion(dto);
		chatService.timeUpdateRoom(dto.getKey());
		messagingTemplate.convertAndSend("/topic/query/"+dto.getKey(), dto.getContent());
		
	}
	
	//유저에게 답변
	@MessageMapping("/chat/answer")
	public void answer(AnswerDTO dto) {
		
		System.out.println(">>>채팅 답변 :"+dto);
		
		chatService.saveChatAnswer(dto);
		chatService.timeUpdateRoom(dto.getKey());
		messagingTemplate.convertAndSend("/topic/answer/"+dto.getKey(), dto.getContent());
		
	}

}
