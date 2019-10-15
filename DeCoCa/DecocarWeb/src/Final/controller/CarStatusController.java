package Final.controller;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import Final.frame.Biz;
import Final.vo.CarStatus;



@Controller
public class CarStatusController {

	@Resource(name = "csbiz")
	Biz<Integer, CarStatus> csbiz;
	

	@RequestMapping("/carmap.mc")
	public ModelAndView carmap() {
		ModelAndView mv = new ModelAndView();
		
		//carlocation4 : �������� ���� �浵�� ��Ÿ���� ����
		mv.setViewName("car/carlocation4");
		return mv;
	}

}
