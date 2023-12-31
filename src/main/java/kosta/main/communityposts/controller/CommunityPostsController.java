package kosta.main.communityposts.controller;


import kosta.main.communityposts.dto.CommunityPostCreateDto;
import kosta.main.communityposts.dto.CommunityPostResponseDto;
import kosta.main.communityposts.dto.CommunityPostSelectDto;
import kosta.main.communityposts.dto.CommunityPostUpdateDto;
import kosta.main.communityposts.entity.CommunityPost;
import kosta.main.communityposts.service.CommunityPostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/community-posts")
public class CommunityPostsController {
    private final CommunityPostsService communityPostsService;

    /* 커뮤니티 목록 조회 */
    /* 테스트 성공 확인 */
    @GetMapping
    public List<CommunityPostSelectDto> findPosts() {
        return communityPostsService.findPosts();
    }

    /* 커뮤니티 게시글 상세 조회 */
    /* 테스트 성공 확인 */
    @GetMapping("/{communityPostId}")
    public ResponseEntity<CommunityPostSelectDto> findPost(@PathVariable("communityPostId") Integer communityPostId) throws Exception {
        CommunityPostSelectDto communityPost = communityPostsService.findPost(communityPostId);
        return ResponseEntity.ok(communityPost);
    }

    /* 커뮤니티 게시글 작성 */
    /* 테스트 성공 확인 */
    @PostMapping
    public ResponseEntity<CommunityPost> addPost(@RequestBody CommunityPostCreateDto communityPostCreateDto) {
        CommunityPost communityPost = communityPostsService.addPost(communityPostCreateDto);
        return ResponseEntity.ok(communityPost);
    }

    /* 커뮤니티 게시글 수정 */
    @PutMapping("/{communityPostId}")
    public ResponseEntity<CommunityPostResponseDto> updatePost(@PathVariable("communityPostId") Integer communityPostId, @RequestBody CommunityPostUpdateDto communityPostUpdateDto){
        return new ResponseEntity<>(communityPostsService.updatePost(communityPostId, communityPostUpdateDto), HttpStatus.OK);
    }

    /* 커뮤니티 게시글 삭제 */
    @DeleteMapping("/{communityPostId}")
    public void deletePost(@PathVariable("communityPostId") Integer communityPostId) {
        communityPostsService.deletePost(communityPostId);
    }

    /* 커뮤니티 좋아요 */
    @PostMapping("/likes/{communityPostId}")
    public void likePost(@PathVariable("communityPostId") Integer communityPostId) {
        communityPostsService.likePost(communityPostId);
    }

    /* 커뮤니티 좋아요 취소 */
    @DeleteMapping("/likes/{communityPostId}")
    public void disLikePost(@PathVariable("communityPostId") Integer communityPostId) {
        communityPostsService.disLikePost(communityPostId);
    }
}

