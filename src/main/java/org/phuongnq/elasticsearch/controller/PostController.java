package org.phuongnq.elasticsearch.controller;

import jakarta.validation.Valid;
import org.phuongnq.elasticsearch.domain.Tag;
import org.phuongnq.elasticsearch.domain.User;
import org.phuongnq.elasticsearch.model.PostRequest;
import org.phuongnq.elasticsearch.repos.TagRepository;
import org.phuongnq.elasticsearch.repos.UserRepository;
import org.phuongnq.elasticsearch.service.PostService;
import org.phuongnq.elasticsearch.util.CustomCollectors;
import org.phuongnq.elasticsearch.util.WebUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public PostController(final PostService postService, final UserRepository userRepository,
                          final TagRepository tagRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getUsername)));
        model.addAttribute("tagsValues", tagRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Tag::getId, Tag::getName)));
    }

    @GetMapping
    public String list(@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,
                       @RequestParam(name = "q", required = false) String search, final Model model) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(10);

        model.addAttribute("postPage", postService.findAll(PageRequest.of(currentPage, pageSize), search));
        model.addAttribute("q", search);
        return "post/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("post") final PostRequest postDTO) {
        return "post/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("post") @Valid final PostRequest postDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "post/add";
        }
        postService.create(postDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("post.create.success"));
        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("post", postService.get(id));
        return "post/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
                       @ModelAttribute("post") @Valid final PostRequest postDTO, final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "post/edit";
        }
        postService.update(id, postDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("post.update.success"));
        return "redirect:/posts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id, final RedirectAttributes redirectAttributes) {
        postService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("post.delete.success"));
        return "redirect:/posts";
    }

}
