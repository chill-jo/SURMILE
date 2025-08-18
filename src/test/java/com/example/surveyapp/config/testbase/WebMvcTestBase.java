package com.example.surveyapp.config.testbase;

import com.example.surveyapp.domain.admin.presentation.AdminController;
import com.example.surveyapp.domain.ai.chat.presentation.ChatController;
import com.example.surveyapp.domain.order.presentation.OrderController;
import com.example.surveyapp.domain.point.presentation.PointController;
import com.example.surveyapp.domain.product.presentation.ProductController;
import com.example.surveyapp.domain.survey.presentation.OptionsController;
import com.example.surveyapp.domain.survey.presentation.QuestionController;
import com.example.surveyapp.domain.survey.presentation.SurveyController;
import com.example.surveyapp.domain.surveyanswer.presentation.SurveyAnswerController;
import com.example.surveyapp.domain.user.presentation.UserController;
import com.example.surveyapp.global.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = {UserController.class, AdminController.class, OrderController.class,
                PointController.class, ProductController.class, OptionsController.class,
                QuestionController.class, SurveyController.class, SurveyAnswerController.class,
                ChatController.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class WebMvcTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
