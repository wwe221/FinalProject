package Final.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import Final.frame.Biz;
import Final.vo.Reservation;
import Final.vo.User;

@Controller
public class ReservationController {

	@Resource(name = "ubiz")
	Biz<String, User> ubiz;

	@Resource(name = "reserbiz")
	Biz<Integer, Reservation> rbiz;

	@Resource(name = "Ureserbiz")
	Biz<String, Reservation> uresbiz;

	@RequestMapping("/schedule.mc")
	public ModelAndView schedule1(HttpSession session, String type) {
		ModelAndView mv = new ModelAndView();

		int stype = Integer.parseInt(type);
		session.setAttribute("stype", stype);
		mv.setViewName("schedule/schedule");
		return mv;
	}

	// Click Reservation OK Button
	@RequestMapping("/schregisterimpl.mc")
	public void schregisterimpl(Reservation reserve, HttpServletResponse response, HttpSession session) {
		System.out.println(reserve);
		String dfull = reserve.getCalDate();
		String ddate = dfull.substring(0, 10);
		String dtime = dfull.substring(11, 16);
		System.out.println(dfull + " = " + ddate + "" + dtime);
		reserve.setCalDate(ddate);
		reserve.setsTime(dtime);
		String[] time = dtime.split(":");
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		int eTime = Integer.parseInt(reserve.geteTime());
		int emin = eTime % 60;
		int ehour = eTime / 60;
		minute+=emin;
		int uphour = minute/60;
		minute%=60;
		hour += ehour+uphour;
		hour = hour % 24;
		reserve.seteTime(hour+":"+minute);
		System.out.println(reserve.geteTime());
		/* create PinNumber */
		Random r = new Random();
		String key = ""; // pinNumber
		for (int i = 0; i < 6; i++) {
			String ran = Integer.toString(r.nextInt(9) + 1);
			if (!key.contains(ran)) {
				key += ran;
			} else {
				i -= 1;
			}
		}
		//사용자가 등록 할 때 배차가 된다
		//-> 일정 시작 20분전에 배차한다.
		//-> 일정 시작 20분전에 쿼리를 날릴거니까
		// 그걸 그냥 받아서 실행시키는 녀석만 만들자.
		// 호호
		int pinNum = Integer.parseInt(key); // pinNumber(Final)
		reserve.setPinNum(pinNum); // set PinNum (DB)
		System.out.println(reserve.toString());
		int calid = reserve.getCalid();
		try {
			rbiz.register(reserve);
			sendPush(reserve);
			response.sendRedirect("schelist.mc?userid="+reserve.getUserid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/schelist.mc")
	public ModelAndView schelist(Reservation reserve, String userid) {
		ModelAndView mv = new ModelAndView();
		ArrayList<Reservation> rlist = null;
		try {
			rlist = uresbiz.getAll(userid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("rlist", rlist);
		mv.addObject("center", "schedule/schelist");
		mv.setViewName("main");
		return mv;
	}

	@RequestMapping("/schedetail.mc")
	public ModelAndView schedetail(Reservation reserve, int calid, HttpSession session) {
		ModelAndView mv = new ModelAndView();

		try {
			reserve = rbiz.get(calid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		session.setAttribute("sch", reserve);
		mv.addObject("center", "schedule/schedetail");
		mv.setViewName("main");
		return mv;
	}

	@RequestMapping("/schedelete.mc")
	public void schedelete(Reservation reserve, int calid, HttpServletResponse response) {

		try {
			reserve = rbiz.get(calid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String uid = reserve.getUserid();

		try {
			rbiz.remove(calid);
			System.out.println("DELETED : " + calid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response.sendRedirect("schelist.mc?userid=" + uid);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping("/scheupdate.mc")
	public ModelAndView scheupdate(Reservation reserve, int calid, HttpSession session, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();

		try {
			reserve = rbiz.get(calid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		session.setAttribute("sch", reserve);
		mv.setViewName("schedule/scheupdate");
		return mv;
	}

	@RequestMapping("/scheupdateimpl.mc")
	public void scheupdateimpl(Reservation reserve, int calid, HttpServletResponse response) {

		String dfull = reserve.getCalDate();
		String ddate = dfull.substring(0, 10);
		String dtime = dfull.substring(11, 16);
		System.out.println(dfull + " = " + ddate + "" + dtime);
		reserve.setCalDate(ddate);
		reserve.setsTime(dtime);

		try {
			rbiz.modify(reserve);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("modified : " + reserve);
		try {
			response.sendRedirect("schedetail.mc?calid=" + calid);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void sendPush(Reservation reserve) {
		String uid = reserve.getUserid();
		User u = null;
		try {
			u = ubiz.get(uid);
			String token = u.getUserdevice();
			int pin = reserve.getPinNum();
			FcmUtil fcm = new FcmUtil();
			fcm.send_FCM(token, "decoca", pin + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Move to CarStatusController.java
	/*
	 * public void sendPush(Reservation reserve) { String uid = reserve.getUserid();
	 * User u = null; try { u = ubiz.get(uid); String token = u.getUserdevice(); int
	 * pin = reserve.getPinNum(); FcmUtil fcm = new FcmUtil(); fcm.send_FCM(token,
	 * "decoca", pin + ""); } catch (Exception e) { e.printStackTrace(); } }
	 */

}
