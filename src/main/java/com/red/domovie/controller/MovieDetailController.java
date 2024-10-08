package com.red.domovie.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rabbitmq.client.AMQP.Basic.Return;
import com.red.domovie.domain.dto.movieDetail.PostMovieRatingDTO;
import com.red.domovie.security.CustomUserDetails;
import com.red.domovie.service.MovieDetailService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;



@RequiredArgsConstructor
@Controller
public class MovieDetailController {
	
	private final MovieDetailService movieDetailService;
	
	//영화 상세 정보 
	@GetMapping("/movies/detail/{movieID}")
	public String MovieDetail(@PathVariable(name = "movieID") String movieID,Model model,@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(name = "page" ,defaultValue = "0") int page) {
		
		System.out.println("movieID:"+movieID);
		//
		movieDetailService.findMovieDetail(movieID,model);
		
		
		return "views/movieDetail/list";
	}
	
	
	//비동기 처리를 위한 사용자 리뷰 요청
	@GetMapping("/movies/detail/{movieID}/usercomments")
	public String findUserComments(@PathVariable(name = "movieID") String movieID,@AuthenticationPrincipal CustomUserDetails userDetails,Model model){
		
		System.out.println(movieID);
		
		if(userDetails != null) {
			System.out.println("특정리뷰 가져오기 작동중");
			 movieDetailService.findUserMovieRating(userDetails.getUserId(),movieID,model);
			
		}
		
		return "views/movieDetail/listFragments :: userReviewSection";
			
		}

	
	
	//비동기 처리를 위한 전체 리뷰 요청
	@GetMapping("/movies/detail/{movieID}/comments")
	public String findAllComments(@PathVariable(name = "movieID") String movieID,Model model ,@RequestParam(name = "page" ,defaultValue = "1") int page){
		
		
		
	    //영화에 맞는 모든 리뷰 가져오기
		movieDetailService.findAllComments(movieID,model, page);
		
		return "views/movieDetail/listFragments :: reviewList";
			
		}

	
	//비동기처리를 위한 평균평점 요청
	@GetMapping("/movies/detail/{movieID}/average")
	public String getMethodName(@PathVariable(name = "movieID") String movieID,Model model) {
		
		
		movieDetailService.findAverageRating(movieID,model);
		
		return "views/movieDetail/listFragments :: averageRating";
		
	}
	
	
	
	
	//리뷰 저장
	@ResponseBody
	@PostMapping("/movies/detail/comment/usercomments")
	public String  postMovieRating(@RequestBody PostMovieRatingDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
	
    	
		System.out.println(dto);
		if(userDetails != null) {
			//리뷰 저장
			movieDetailService.saveMovieRating(userDetails.getUserId(),dto);
			
			
		}else {
			
			return "redirect:/login";
		}
		
		  return null;
		  
	
	}
	
	
	//리뷰 수정
	@ResponseBody
	@PutMapping("/movies/detail/comment/usercomments")
	public String putMovieRating(@RequestBody PostMovieRatingDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

		System.out.println(dto);
		if(userDetails != null) {
			//리뷰 저장
			movieDetailService.updateMovieRating(userDetails.getUserId(),dto);
			
			
		}
		
		  return null;
	}
	
	

	//리뷰 삭제
	@ResponseBody
	@DeleteMapping("/movies/detail/comment/usercomments")
	public String deleteMovieRating (@RequestBody String movieId, @AuthenticationPrincipal CustomUserDetails userDetails) {
	
    	System.out.println(">>>>>>>>삭제할 영화" +movieId);
		if(userDetails != null) {

			movieDetailService.deleteMovieRating(userDetails.getUserId(),movieId);
			
			
		}
		
		  return null;
		  
	
	}
	
	

}