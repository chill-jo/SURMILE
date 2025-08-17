package com.example.surveyapp.config.testmockbeans;

import com.example.surveyapp.domain.admin.application.AdminService;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import com.example.surveyapp.domain.order.application.OrderService;
import com.example.surveyapp.domain.point.application.PointService;
import com.example.surveyapp.domain.product.application.ProductService;
import com.example.surveyapp.domain.survey.application.OptionsService;
import com.example.surveyapp.domain.survey.application.QuestionService;
import com.example.surveyapp.domain.survey.application.SurveyService;
import com.example.surveyapp.domain.survey.domain.service.SurveyQuestionService;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerQueryService;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerService;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerStatisticsService;
import com.example.surveyapp.domain.user.application.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestMockBeans {
    @Bean public UserService userService() {return mock(UserService.class);}
    @Bean public AdminService adminService() {return mock(AdminService.class);}
    @Bean public OrderService orderService() {return mock(OrderService.class);}
    @Bean public PointService pointService() {return mock(PointService.class);}
    @Bean public ProductService productService() {return mock(ProductService.class);}
    @Bean public OptionsService optionsService() {return mock(OptionsService.class);}
    @Bean public QuestionService questionService() {return mock(QuestionService.class);}
    @Bean public SurveyService surveyService() {return mock(SurveyService.class);}
    @Bean public SurveyQuestionService surveyQuestionService() {return mock(SurveyQuestionService.class);}
    @Bean public SurveyAnswerService surveyAnswerService() {return mock(SurveyAnswerService.class);}
    @Bean public SurveyAnswerQueryService surveyAnswerQueryService() {return mock(SurveyAnswerQueryService.class);}
    @Bean public SurveyAnswerStatisticsService surveyAnswerStatisticsService() {return mock(SurveyAnswerStatisticsService.class);}
    @Bean public ChatService chatService() {return mock(ChatService.class);}
}
