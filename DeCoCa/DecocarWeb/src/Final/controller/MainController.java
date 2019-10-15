package Final.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import Final.frame.Biz;
import Final.vo.CarStatus;
import Final.vo.Reservation;
import Final.vo.User;

@Controller
public class MainController {
	@Resource(name="ubiz")
	Biz<String, User> ubiz;
	

	@Resource(name = "csbiz")
	Biz<Integer, CarStatus> csbiz;

	@Resource(name = "reserbiz")
	Biz<Integer, Reservation> rbiz;

	@RequestMapping("/main.mc")
	public ModelAndView main() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("center", "center");
		mv.setViewName("main");
		//mv.setViewName("map2");
		return mv;
	}

	@RequestMapping("/loginimpl.mc")
	public ModelAndView loginimpl(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		String userid= request.getParameter("userid");
		String pwd= request.getParameter("pwd");
		int usertype = 0 ;
		ArrayList<CarStatus> cslist = null;
		ArrayList<Reservation> relist = null;
		try {
			User dbuser = ubiz.get(userid);
			cslist = csbiz.get();
			relist = rbiz.get();
			if(pwd.equals(dbuser.getPwd())) {
				session.setAttribute("loginuser", dbuser);
				usertype = dbuser.getUsertype();
				System.out.println("����Ÿ�� : "+usertype);
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();

				out.println("<script>alert('��й�ȣ�� Ʋ�Ƚ��ϴ�.'); location.href='login.mc'</script>");
				out.flush();
			}
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>alert('���̵� Ʋ�Ƚ��ϴ�.'); location.href='login.mc'</script>");
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		try {
			// �ڵ��� ����
			cslist = csbiz.get();

			// ���� ����
			relist = rbiz.get();
		} catch (Exception e) {

			e.printStackTrace();
		}
		mv.addObject("cslist", cslist);
		mv.addObject("relist", relist);

		if (usertype == 1) {
			mv.setViewName("admin/admin");			
		} else {
			mv.addObject("center", "center");
			mv.setViewName("main");
		}
		return mv;
	}

	@RequestMapping("/logout.mc")
	public ModelAndView logout(ModelAndView mv, HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		mv.addObject("center", "center");
		mv.setViewName("main");
		return mv;
	}

	
	@RequestMapping("/userupdateimpl.mc")
	public ModelAndView uupduserupdateimplate(HttpServletRequest request,User user,String userid,HttpServletResponse response) {
		ModelAndView mv= new ModelAndView();
		System.out.println(user);
		try {
			ubiz.modify(user);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;
				out = response.getWriter();
				out.println("<script>alert('�����Ǿ����ϴ�.'); location.href='main.mc'</script>");
				out.flush();
				
				mv.addObject("center","uupdate");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.setViewName("main");
		return mv;
	}

	// �ΰ��� �ð� ���� �ι�
	@RequestMapping("/schedule.mc")
	public ModelAndView schedule() {
		ModelAndView mv = new ModelAndView();
		// mv.addObject("center","scheregister");
		mv.setViewName("schedule");
		return mv;
	}
	
	// �����쿡�� value�� �������� 
	@RequestMapping("/schregisterimpl.mc")
	public void schregisterimpl(Reservation reserve, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		System.out.println(reserve.toString());
		try {
			rbiz.register(reserve);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			response.sendRedirect("schelist.mc");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// ������ ����Ʈ
	@RequestMapping("/schelist.mc")
	public ModelAndView schelist(Reservation reserve ,ArrayList<Reservation> rlist) {
		ModelAndView mv = new ModelAndView();
		
		try {
			rlist = rbiz.get();
		} catch (Exception e) {	
			e.printStackTrace();
		}
//		System.out.println(rlist.);
		mv.addObject("rlist", rlist);
		mv.addObject("center","schelist");
		mv.setViewName("main");
		return mv;
	}
	
	
	// ����������
	@RequestMapping("/mypage.mc")
	public ModelAndView mypage(String userid) {
		ModelAndView mv = new ModelAndView();
		User user = null;
		try {
			user = ubiz.get(userid);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		mv.addObject("u",user);
		mv.addObject("center","mypage");
		mv.setViewName("main");
		return mv;
	}
	
	
}