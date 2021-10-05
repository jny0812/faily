package Project.Projectspring.Question.Controller;

import Project.Projectspring.Group.VO.UserGroupVO;
import Project.Projectspring.Join.Controller.JoinController;
import Project.Projectspring.Question.Service.QuestionService;
import Project.Projectspring.Question.VO.AllQuestionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.HashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final JoinController joinController;


//    public static void main(String[] args) {
//        Calendar today = Calendar.getInstance();
//        today.add(Calendar.HOUR, 9);
//        int date = today.get(Calendar.DATE);
//        int question_number = date % 10;
//        System.out.println("question_number = " + question_number);
//    }


    //오늘의 질문 불러오기
    @RequestMapping(value = "/todayQuestion", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> todayQuestion() throws Exception {

        //QuestionController questionController = new QuestionController(questionService);
        HashMap<String, Object> result = new HashMap<>();

        Calendar today = Calendar.getInstance();
        today.add(Calendar.HOUR, 9);
        int date = today.get(Calendar.DATE);
        int question_number = date % 10;

        String question = questionService.questionNumberCheck(question_number); //질문 내용
        int question_id = questionService.questionId(question);


        if(question == null){

            result.put("isSuccess", false);
            result.put("code",404);
            result.put("message","유효하지 않은 사용자입니다.");
        }
        else {

            String a = joinController.remakeJwtToken();
            String e_mail = joinController.getSubject(); //이메일 추출
            int user_id = questionService.userIdCheck(e_mail);
            questionService.statusChangeToZero(user_id); //답변 미완료 상태로 변경

            int user_group_id = questionService.questionUserGroupId(user_id);  //질문한 user의 group_id 추출

            AllQuestionVO allQuestionVO = new AllQuestionVO(user_group_id,question_id,null);
            questionService.createGroupQuestion(allQuestionVO); //group_question 테이블에 insert

            result.put("isSuccess", true);
            result.put("code",200);
            result.put("message","오늘의 질문을 불러왔어요!");

            result.put("todayQuestion", question);

        }
        return result;
    }
}
