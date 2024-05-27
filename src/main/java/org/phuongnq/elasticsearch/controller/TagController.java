package org.phuongnq.elasticsearch.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import org.phuongnq.elasticsearch.model.TagDTO;
import org.phuongnq.elasticsearch.service.TagService;
import org.phuongnq.elasticsearch.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(final TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("tags", tagService.findAll());
        return "tag/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("tag") final TagDTO tagDTO) {
        return "tag/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("tag") @Valid final TagDTO tagDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tag/add";
        }
        tagService.create(tagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("tag.create.success"));
        return "redirect:/tags";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id, final Model model) {
        model.addAttribute("tag", tagService.get(id));
        return "tag/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final UUID id,
            @ModelAttribute("tag") @Valid final TagDTO tagDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tag/edit";
        }
        tagService.update(id, tagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("tag.update.success"));
        return "redirect:/tags";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final UUID id,
            final RedirectAttributes redirectAttributes) {
        tagService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("tag.delete.success"));
        return "redirect:/tags";
    }

}
