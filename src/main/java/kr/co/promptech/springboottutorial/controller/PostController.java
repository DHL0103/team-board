package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.PostFileService;
import kr.co.promptech.springboottutorial.service.PostRejectionService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/{boardId}/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final PostFileService postFileService;
    private final BoardService boardService;
    private final PostRejectionService postRejectionService;

    /**
     * @param postId        조회할 게시글 PK
     * @param model     뷰에 전달할 데이터 컨테이너
     * @param principal 현재 로그인한 사용자 정보 (optional)
     * @return 게시글 상세 뷰 이름 (post/detail)
     * 게시글 상세 페이지 렌더링. 첨부파일, 반려사유, 보드 정보, 작성자 여부를 함께 전달
     */
    @GetMapping("/{postId}")
    public String getPostDetailPage(@PathVariable Long postId, Model model, Principal principal) {
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("postFiles", postFileService.getFilesByPostId(postId));
        model.addAttribute("rejections", postRejectionService.getByPostId(postId));
        Board board = boardService.getBoardById(post.getBoardId());
        if (board != null) {
            model.addAttribute("boardName", board.getName());
            model.addAttribute("boardColor", board.getColor());
        }

        if (principal != null) {
            Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();
            model.addAttribute("isOwner", post.getMemberId().equals(currentMemberId));
        } else {
            model.addAttribute("isOwner", false);
        }

        return "post/detail";
    }

    /**
     * @param postCreateDto 게시글 생성 데이터 (제목, 내용, 보드ID 등)
     * @param files         첨부파일 목록 (optional)
     * @param principal     현재 로그인한 사용자 정보
     * @return 보드 상세 페이지로 리다이렉트
     * 게시글 생성 후 첨부파일 저장. 파일 저장 실패 시 게시글은 유지
     */
    @PostMapping("/create")
    public String createPost(PostCreateDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             Principal principal) {
        Long postId = postService.createPost(postCreateDto, principal.getName());
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        postFileService.saveFile(file, postId);
                    } catch (Exception e) {
                        // 파일 저장 실패해도 게시글은 유지
                        log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }
        return "redirect:/board/{boardId}";
    }

    /**
     * @param id        삭제할 게시글 PK
     * @param principal 현재 로그인한 사용자 정보
     * @return 메인 보드 페이지로 리다이렉트, 본인 아닐 경우 에러 파라미터와 함께 상세 페이지로 리다이렉트
     * 작성자 본인만 삭제 가능
     */
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();

        if (!post.getMemberId().equals(currentMemberId)) {
            // 본인이 아니면 삭제 거부 (에러 페이지나 메시지 처리)
            return "redirect:/post/" + id + "?error=unauthorized";
        }

        postService.deletePost(post);

        return "redirect:/board";
    }

    /**
     * @param id            수정할 게시글 PK
     * @param postCreateDto 수정할 게시글 데이터
     * @param files         새로 추가할 첨부파일 목록 (optional)
     * @param deleteFileIds 삭제할 첨부파일 ID 목록 (optional)
     * @param principal     현재 로그인한 사용자 정보
     * @return 게시글 상세 페이지로 리다이렉트, 본인 아닐 경우 에러 파라미터와 함께 리다이렉트
     * 작성자 본인만 수정 가능. 파일 삭제 후 신규 파일 저장
     */
    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,
                             PostCreateDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                             Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();

        if (!post.getMemberId().equals(currentMemberId)) {
            return "redirect:/post/" + id + "?error=unauthorized";
        }

        postService.updatePost(id, postCreateDto);

        if (deleteFileIds != null) {
            postFileService.deleteFiles(deleteFileIds);
        }

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        postFileService.saveFile(file, id);
                    } catch (Exception e) {
                        log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }

        return "redirect:/post/" + id;
    }

    /**
     * @param id        승인 요청할 게시글 PK
     * @param principal 현재 로그인한 사용자 정보
     * @return 게시글 상세 페이지로 리다이렉트, 본인 아닐 경우 에러 파라미터와 함께 리다이렉트
     * 게시글 상태를 REQUESTED로 변경. 작성자 본인만 요청 가능
     */
    @PostMapping("/request/{id}")
    public String requestPost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();
        if (!post.getMemberId().equals(currentMemberId)) {
            return "redirect:/post/" + id + "?error=unauthorized";
        }
        postService.updateStatus(id,"REQUESTED");
        return "redirect:/post/" + id;
    }
}
