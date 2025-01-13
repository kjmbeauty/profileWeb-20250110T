package com.gyojincompany.profile.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.gyojincompany.profile.dao.MemberDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	
	@Autowired
	private SqlSession sqlSession;
	
	@PostMapping(value = "/joinOk")
	public String joinOk(HttpServletRequest request, Model model) {
		
		String mid = request.getParameter("mid");
		String mpw = request.getParameter("mpw");
		String mname = request.getParameter("mname");
		String memail = request.getParameter("memail");
		
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		
		int idFlag = mDao.idCheckDao(mid);//1이 반환되면 이미 가입하려는 아이디가 존재->가입불가
		
		if(idFlag == 0) { //기존 아이디 존재하지 않음->가입 메소드 호출		
			int joinFlag = mDao.joinMemberDao(mid, mpw, mname, memail);//반환되는 값이 1이면 가입성공
			if(joinFlag == 1) { //가입 성공
				model.addAttribute("msg", mname + "님 가입을 축하드립니다.로그인 하세요.");
				model.addAttribute("url", "login");			
			} else { //가입 실패
				model.addAttribute("msg", "회원 가입 실패!다시 확인하신 후 가입하세요.");
				model.addAttribute("url", "join");
			}
		} else { //기존 아이디 존재
			model.addAttribute("msg", "가입하시려는 아이디가 존재합니다!다시 확인하신 후 가입하세요.");
			model.addAttribute("url", "join");			
		}
		return "alert/alert";
	}
	
	@GetMapping(value = "/idcheck")
	public String idcheck(HttpServletRequest request, Model model, HttpServletResponse response) {
		
		String idcheck = request.getParameter("idcheck");
		
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		
		int idFlag = mDao.idCheckDao(idcheck);//1이 반환되면 이미 가입하려는 아이디가 존재->가입불가
		
		if(idFlag == 1) {
			model.addAttribute("msg", "가입하시려는 아이디가 존재합니다!다시 확인하신 후 가입하세요.");
			model.addAttribute("url", "join");
			
			return "alert/alert";
		} else {
			// java로 자바스크립트 경고창 띄우기
			try {
				response.setContentType("text/html;charset=utf-8");//경고창 텍스트를 utf-8로 인코딩
				response.setCharacterEncoding("utf-8");
				PrintWriter printWriter = response.getWriter();
				printWriter.println("<script>alert('"+"가입 가능한 아이디 입니다."+"');</script>");
				printWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			model.addAttribute("joinId", idcheck);
		}
		
		return "join";
	}
	
	@PostMapping(value = "/loginOk")
	public String loginOk(HttpServletRequest request, Model model, HttpSession session, HttpServletResponse response) {
		
		String mid = request.getParameter("mid");
		String mpw = request.getParameter("mpw");
		
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		int loginFlag = mDao.loginDao(mid, mpw);//1이면 로그인 성공, 0이면 실패
		
		if(loginFlag == 1) {//로그인 성공
			try {
				response.setContentType("text/html;charset=utf-8");//경고창 텍스트를 utf-8로 인코딩
				response.setCharacterEncoding("utf-8");
				PrintWriter printWriter = response.getWriter();
				printWriter.println("<script>alert('"+"안녕하세요. 로그인 성공하셨습니다."+"');</script>");
				printWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.setAttribute("sessionid", mid);
		} else {
			model.addAttribute("msg", "아이디 또는 비밀번호가 잘못 되었습니다.다시 확인하신 후 로그인하세요.");
			model.addAttribute("url", "login");
			
			return "alert/alert";
		}
		
		return "index";
	}
	
	
	
	
	

}