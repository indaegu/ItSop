package kosta.main.exchangeposts.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExchangePostListDTO { // 교환 게시글 전체 목록을 전송하는 DTO
  private final Integer exchangePostId; // 교환 게시글 ID
  private final String title; // 교환 게시글 제목
  private final String preferItem; // 선호아이템
  private final String address; // 거래 희망 주소
  private final String exchangePostStatus; // 교환 게시글 상태(EXCHANGING , RESERVATION, COMPLETED, DELETED)
  private final LocalDateTime created_at; // 교환 게시글 작성일
  private final String imgUrl; // 교환 게시글에 등록된 item의 대표이미지 URㅣ
  private final Integer bidCount; // 해당 교환 게시글에 등록된 입찰의 갯수를 세서 Integer 값으로 반환
}
