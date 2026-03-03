package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.model.dto.MemberCreateDto;
import kr.co.promptech.springboottutorial.model.dto.MemberUpdateDto;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostRejectionService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;
    private final BoardService boardService;
    private final MemberService memberService;
    private final PostRejectionService postRejectionService;

    @GetMapping("/request")
    public String requestPage(Model model){
        model.addAttribute("postList", postService.getRequestedPost());
        List<Board> boardList = boardService.getAllBoards();
        Map<Long, String> boardPaletteMap = new LinkedHashMap<>();
        for (int i = 0; i < boardList.size(); i++) {
            boardPaletteMap.put(boardList.get(i).getId(), "p" + (i % 6 + 1));
        }
        model.addAttribute("boardPaletteMap", boardPaletteMap);
        return "admin/request";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, Model model){
        postService.updateStatus(id,"APPROVED");
        return "redirect:/admin/request";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, @RequestParam String reason, Principal principal){
        postService.updateStatus(id,"REJECTED");
        Long rejectedBy = memberService.getMemberByUsername(principal.getName()).getId();
        postRejectionService.save(id, reason, rejectedBy);
        return "redirect:/admin/request";
    }

    @GetMapping("/members")
    public String memberPage(Model model){
        List<Member> memberList = memberService.getAllMemberExceptAdmin();
        model.addAttribute("memberList", memberList);
        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);
        Map<Long, String> boardMap = new LinkedHashMap<>();
        for (Board board : boardList) {
            boardMap.put(board.getId(), board.getName());
        }
        model.addAttribute("boardMap", boardMap);
        return "admin/members";
    }
    @GetMapping("/boards")
    public String boardsPage(Model model){
        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);
        return "admin/boards";
    }

    @PostMapping("/board/create")
    public String createBoard(@ModelAttribute BoardCreateDto boardCreateDto){
        boardService.createBoard(boardCreateDto);

        return "redirect:/admin/boards";
    }


    @PostMapping("/board/update/{id}")
    public String updateBoard(@PathVariable Long id,@ModelAttribute BoardCreateDto boardCreateDto){
        boardService.updateBoard(id, boardCreateDto);

        return "redirect:/admin/boards";
    }

    @PostMapping("/board/delete/{id}")
    public String deleteBoard(@PathVariable Long id){
        List<Post> posts = postService.getPostsByBoardId(id);
        for (Post post : posts) {
            postService.deletePost(post);
        }
        boardService.deleteBoard(id);
        return "redirect:/admin/boards";
    }

    @PostMapping("/member/create")
    public String createMember(@ModelAttribute MemberCreateDto memberCreateDto){
        memberService.create(memberCreateDto);
        return "redirect:/admin/members";
    }

    @PostMapping("/member/update/{id}")
    public String updateMember(@PathVariable Long id,@ModelAttribute MemberUpdateDto memberUpdateDto){
        memberService.update(id,memberUpdateDto);
        return "redirect:/admin/members";
    }

    @PostMapping("/member/delete/{id}")
    public String deleteMember(@PathVariable Long id){
        List<Post> posts = postService.getPostsByMemberId(id);
        for (Post post : posts) {
            postService.deletePost(post);
        }
        memberService.delete(id);
        return "redirect:/admin/members";
    }


}
