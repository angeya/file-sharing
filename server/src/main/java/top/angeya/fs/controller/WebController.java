package top.angeya.fs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angeya
 * @createTime 2023/2/7 22:18
 */
@RestController
public class WebController {

    /**
     * 返回视图，方便输入URL
     * @return 页面
     */
    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index.html");
        return mav;
    }
}
